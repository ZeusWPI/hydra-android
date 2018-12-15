package be.ugent.zeus.hydra.minerva.calendar.sync;

import android.Manifest;
import android.accounts.Account;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.*;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ChannelCreator;
import be.ugent.zeus.hydra.common.ExtendedSparseArray;
import be.ugent.zeus.hydra.common.request.RequestException;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.common.sync.Synchronisation;
import be.ugent.zeus.hydra.minerva.account.MinervaConfig;
import be.ugent.zeus.hydra.minerva.calendar.AgendaItem;
import be.ugent.zeus.hydra.minerva.calendar.AgendaItemRepository;
import be.ugent.zeus.hydra.minerva.course.Course;
import be.ugent.zeus.hydra.minerva.course.CourseRepository;
import be.ugent.zeus.hydra.minerva.preference.MinervaPreferenceFragment;
import be.ugent.zeus.hydra.utils.StringUtils;
import java9.util.function.Function;
import java9.util.stream.Collectors;
import java9.util.stream.StreamSupport;
import jonathanfinerty.once.Once;
import org.threeten.bp.DateTimeUtils;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

import java.util.*;

import static be.ugent.zeus.hydra.utils.IterableUtils.transform;

/**
 * Synchronises the calendar with Minerva and, if permissions allow, with the built-in calendar.
 *
 * @author Niko Strijbol
 */
public class CalendarSync {

    private static final String TAG = "CalendarSync";
    private static final String FIRST_SYNC_BUILT_IN_CALENDAR = "once_first_calendar";
    public static final String MINERVA_CALENDAR_NOTIFIED_ABOUT_BUG = "pref_minerva_stop_bugging";
    private static final long NO_CALENDAR = -1;
    private final AgendaItemRepository calendarRepository;
    private final CourseRepository courseDao;
    private final Context context;

    public CalendarSync(AgendaItemRepository calendarRepository, CourseRepository courseDao, Context context) {
        this.calendarRepository = calendarRepository;
        this.courseDao = courseDao;
        this.context = context;
    }

    /**
     * Add Adapter parameters to Calendar URI.
     */
    private static Uri adapterUri(Uri uri, Account account) {
        return uri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, account.name)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
                .build();
    }

    /**
     * Sync the calendar.
     *
     * @param account       The account.
     * @param isInitialSync If this is the first sync.
     *
     * @return True if a resync is required, otherwise false.
     *
     * @throws RequestException When something else goes wrong.
     */
    public boolean synchronise(Account account, boolean isInitialSync) throws RequestException {

        // Get the calendar from the server in one go. This does mean it is possible to receive a calendar item
        // for which there is no course in the local database. If that is the case, we just let the sql propagate.
        // Android should automatically start a new synchronisation for us.
        GlobalCalendarRequest request = new GlobalCalendarRequest(context, account);
        // We synchronise two month behind and four months ahead.
        ZonedDateTime now = LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault());
        request.setStart(now.minusMonths(2));
        // End time. We take 4 month (+1 day for the start time).
        request.setEnd(now.plusMonths(4).plusDays(1));

        // Get the courses
        Map<String, Course> courses = StreamSupport.stream(courseDao.getAll())
                .collect(Collectors.toMap(Course::getId, Function.identity()));

        // The result.
        Result<List<AgendaItem>> agendaResult;
        try {
            agendaResult = request.execute()
                    .map(c -> c.items)
                    .map(list -> transform(list, i -> {
                        if (!courses.containsKey(i.courseId)) {
                            throw new MissingCourseException();
                        }
                        return ApiCalendarMapper.INSTANCE.convert(i, courses.get(i.courseId));
                    }));
        } catch (MissingCourseException e) {
            // There is a course we do not have, so stop it.
            return true;
        }

        // Do we want to use the duplication detection or not?
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        List<AgendaItem> agenda;

        if (preferences.getBoolean(MinervaPreferenceFragment.PREF_DETECT_DUPLICATES, false)) {
            agenda = agendaResult.map(new DuplicateDetector()).getOrThrow();
        } else {
            agenda = agendaResult.getOrThrow();
        }

        // Get existing items and the calendar ids.
        ExtendedSparseArray<Long> allItems = calendarRepository.getIdsAndCalendarIds();

        Synchronisation<AgendaItem, Integer> sync = new Synchronisation<>(
                allItems.getKeys(),
                agenda,
                AgendaItem::getItemId);
        Synchronisation.Diff<AgendaItem, Integer> diff = sync.diff();

        // Save the id's of the built-in items.
        for (AgendaItem item : diff.getUpdated()) {
            item.setCalendarId(allItems.get(item.getItemId(), AgendaItem.NO_CALENDAR_ID));
        }

        // Handle integration with the built-in calendar.
        // Check for permission for Calendar access.
        int permissionRead = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR);
        int permissionWrite = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR);

        // We only synchronise with the Calendar if we have access.
        if (permissionRead == PackageManager.PERMISSION_DENIED || permissionWrite == PackageManager.PERMISSION_DENIED) {
            Log.d(TAG, "Missing permissions");
            handleNoPermission();
        } else {
            try {
                // First, we synchronise the items to the calendar. This will modify the items if necessary.
                // It will insert Calendar ids to the items that need it.
                synchronizeCalendar(account, isInitialSync, diff);
            } catch (SecurityException e) {
                // This exception is possible, but very unlikely. This can happen if the user revokes the permission
                // between the check above and the actual execution. In reality, I don't think this can actually happen,
                // but to make Android lint happy, we handle it anyway.
                Log.w(TAG, "Permission problem during calendar sync.", e);
                handleNoPermission();
            }
        }

        // Apply synchronisation. We can only do this after the built-in calendar has been synchronised, since
        // we need the id's of deleted items, and the calendar will adjust the calendar ID's.
        diff.apply(calendarRepository);

        return false;
    }

    /**
     * Invoked when the app doesn't have permission to access the calendars. This will issue a notification, if the user
     * hasn't permanently disabled our access. In that case, this does nothing;
     */
    private void handleNoPermission() {
        // Check if we may notify the user.
        if (Once.beenDone(CalendarPermissionActivity.MINERVA_CALENDAR_STOP_ASKING)) {
            return;
        }

        // Make sure the notification channel is present
        ChannelCreator channelCreator = ChannelCreator.getInstance(context);
        channelCreator.createMinervaAccountChannel();

        // Create a notification and show it to the user.
        Intent intent = new Intent(context, CalendarPermissionActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context, ChannelCreator.MINERVA_ACCOUNT_CHANNEL)
                .setSmallIcon(R.drawable.ic_notification_warning)
                .setCategory(NotificationCompat.CATEGORY_ERROR)
                .setContentTitle(context.getString(R.string.minerva_calendar_permission_notification_title))
                .setContentText(context.getString(R.string.minerva_calendar_permission_notification_short))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(context.getString(R.string.minerva_calendar_permission_notification_long))
                )
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.notify(0, notification);
    }

    /**
     * Invoked when the app cannot access the calendar for some reason.
     */
    private void handleNoCalendar() {
        // Check if we may notify the user.
        if (Once.beenDone(MINERVA_CALENDAR_NOTIFIED_ABOUT_BUG)) {
            return;
        }

        // Make sure the notification channel is present
        ChannelCreator channelCreator = ChannelCreator.getInstance(context);
        channelCreator.createMinervaAccountChannel();

        // Create a notification and show it to the user.
        Notification notification = new NotificationCompat.Builder(context, ChannelCreator.MINERVA_ACCOUNT_CHANNEL)
                .setSmallIcon(R.drawable.ic_notification_warning)
                .setCategory(NotificationCompat.CATEGORY_ERROR)
                .setContentTitle(context.getString(R.string.minerva_calendar_missing_notification_title))
                .setContentText(context.getString(R.string.minerva_calendar_missing_notification_text))
                .setAutoCancel(true)
                .build();

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.notify(0, notification);

        Once.markDone(MINERVA_CALENDAR_NOTIFIED_ABOUT_BUG);
    }

    /**
     * Synchronise our database with the built-in database.
     *
     * @throws SecurityException Thrown in the rare case where we have permission, but the user revokes it during the sync.
     */
    private void synchronizeCalendar(Account account, boolean isInitialSync, Synchronisation.Diff<AgendaItem, Integer> diff) throws SecurityException {

        // Get the URI for our calendar.
        ContentResolver resolver = context.getContentResolver();
        Uri uri = adapterUri(CalendarContract.Events.CONTENT_URI, account);

        // Get the ID of our calendar.
        long calendarId = getCalendarId(account, resolver);
        try {
            if (calendarId == NO_CALENDAR) {
                // Attempt to insert our calendar.
                Uri result = insertCalendar(account, resolver);
                if (result == null) {
                    Log.e(TAG, "Inserting the calendar failed for some reason, abort sync.");
                    return;
                }
                calendarId = getCalendarId(account, resolver);
            } else if (isInitialSync || !Once.beenDone(FIRST_SYNC_BUILT_IN_CALENDAR)) {
                Log.i(TAG, "Removing existing calendar.");
                // Remove existing things.
                deleteCalendarFor(account, resolver);
                Uri result = insertCalendar(account, resolver);
                if (result == null) {
                    Log.e(TAG, "Inserting the calendar failed for some reason, abort sync.");
                    return;
                }
                calendarId = getCalendarId(account, resolver);
            }
        } catch (MissingCalendarException e) {
            // We could not insert our calendar.
            handleNoCalendar();
            return;
        }

        // Updated items (items that should already be on the device)
        Collection<AgendaItem> updatedItems = new HashSet<>(diff.getUpdated());
        // Calendar id's of items we will remove.
        Collection<Long> toRemove = new HashSet<>(calendarRepository.getCalendarIdsForIds(diff.getStaleIds()));

        // We remove ignored items from the updated set and add them to the "to be removed" set.
        for (AgendaItem item: diff.getUpdated()) {
            if (item.getCourse().getIgnoreCalendar()) {
                toRemove.add(item.getCalendarId());
                item.setCalendarId(AgendaItem.NO_CALENDAR_ID);
                updatedItems.remove(item);
            }
        }

        for (long id : toRemove) {
            // We cannot delete non-existing values.
            if (id != AgendaItem.NO_CALENDAR_ID) {
                Uri itemUri = ContentUris.withAppendedId(uri, id);
                resolver.delete(itemUri, null, null);
            }
        }

        // Sometimes our database loses events, or the user selects another option causing
        // the device calendar to contain things that are no longer in our calendar. We cannot know the id of those,
        // so we get all ids, remove the items we still know about and remove the rest.
        Set<Long> allDeviceIds = getAllIdsFromDeviceCalendar(account, resolver);

        // Update calendar items, as they might have changed. This should not contain any ignored items.
        for (AgendaItem updatedItem : updatedItems) {
            long itemCalendarId = updatedItem.getCalendarId();
            ContentValues values = toCalendarValues(calendarId, updatedItem);
            // The item was not found.
            if (itemCalendarId == AgendaItem.NO_CALENDAR_ID) {
                long id = insert(resolver, values, account);
                updatedItem.setCalendarId(id);
            } else { // Update the item.
                Uri itemUri = ContentUris.withAppendedId(uri, itemCalendarId);
                resolver.update(itemUri, values, null, null);
                allDeviceIds.remove(itemCalendarId);
            }
        }

        if (!allDeviceIds.isEmpty()) {
            Log.w(TAG, "Manually removing " + allDeviceIds.size() + " device calendar events. This is not normal!");
        }
        // Remove the events we lost from the calendar.
        for (Long id : allDeviceIds) {
            Uri itemUri = ContentUris.withAppendedId(uri, id);
            resolver.delete(itemUri, null, null);
        }

        // Add new items to the calendar.
        for (AgendaItem newItem : diff.getNew()) {
            // If we don't ignore the course of the item, add it.
            if (!newItem.getCourse().getIgnoreCalendar()) {
                ContentValues values = toCalendarValues(calendarId, newItem);
                long id = insert(resolver, values, account);
                newItem.setCalendarId(id);
            }
        }

        Once.markDone(FIRST_SYNC_BUILT_IN_CALENDAR);
    }

    /**
     * Insert an item into the calendar.
     *
     * @param resolver The content resolver.
     * @param values   The values of the item to insert.
     *
     * @return The ID of the item.
     */
    private static long insert(ContentResolver resolver, ContentValues values, Account account) {
        Uri result = resolver.insert(adapterUri(CalendarContract.Events.CONTENT_URI, account), values);
        if (result == null) {
            Log.e(TAG, "Error while inserting in calendar, abort this item.");
            return AgendaItem.NO_CALENDAR_ID;
        }
        return Long.parseLong(result.getLastPathSegment());
    }

    /**
     * Get the ID of the calendar for our account.
     *
     * @param account The account.
     *
     * @return The ID or {@link #NO_CALENDAR} if there is no calendar.
     *
     * @throws SecurityException Thrown in the rare case where we have permission, but the user revokes it during the sync.
     */
    private static long getCalendarId(Account account, ContentResolver resolver) throws SecurityException {

        String selection =
                CalendarContract.Calendars.ACCOUNT_NAME +
                        " = ? AND " +
                        CalendarContract.Calendars.ACCOUNT_TYPE +
                        " = ? ";

        String[] selArgs = {account.name, MinervaConfig.ACCOUNT_TYPE};

        Cursor cursor = resolver
                .query(
                        CalendarContract.Calendars.CONTENT_URI,
                        new String[]{CalendarContract.Calendars._ID},
                        selection,
                        selArgs,
                        null
                );

        // If the cursor is NULL here, something went wrong. Just return that there is no calendar in that case,
        // since there is nothing we can do. Normally this shouldn't be null, but apparently it is.
        if (cursor == null) {
            return NO_CALENDAR;
        }

        try {
            if (cursor.moveToFirst()) {
                return cursor.getLong(0);
            }
        } finally {
            cursor.close();
        }

        return NO_CALENDAR;
    }

    /**
     * Get the ID of the calendar for our account.
     *
     * @param account The account.
     *
     * @throws SecurityException Thrown in the very rare case that we have permission when starting the sync,
     * but the user revokes it during the sync, but before this has executed.
     */
    private static void deleteCalendarFor(Account account, ContentResolver resolver) throws SecurityException {

        String selection =
                CalendarContract.Calendars.ACCOUNT_NAME +
                        " = ? AND " +
                        CalendarContract.Calendars.ACCOUNT_TYPE +
                        " = ? ";

        String[] selArgs = {account.name, MinervaConfig.ACCOUNT_TYPE};

        resolver.delete(CalendarContract.Calendars.CONTENT_URI, selection, selArgs);
    }

    private static Set<Long> getAllIdsFromDeviceCalendar(Account account, ContentResolver resolver) throws SecurityException {

        String[] projection = {CalendarContract.Events._ID};

        String selection =
                CalendarContract.Calendars.ACCOUNT_NAME +
                        " = ? AND " +
                        CalendarContract.Calendars.ACCOUNT_TYPE +
                        " = ? ";

        String[] selArgs = {account.name, MinervaConfig.ACCOUNT_TYPE};

        Cursor result = resolver.query(CalendarContract.Events.CONTENT_URI, projection, selection, selArgs, null);
        if (result == null) {
            return new HashSet<>();
        }

        try {
            Set<Long> resultSet = new HashSet<>();
            while (result.moveToNext()) {
                resultSet.add(result.getLong(result.getColumnIndexOrThrow(CalendarContract.Events._ID)));
            }
            return resultSet;
        } finally {
            result.close();
        }
    }

    /**
     * Add our calendar.
     *
     * @param account  The account for which the calendar must be added.
     * @param resolver The content resolver.
     */
    private Uri insertCalendar(Account account, ContentResolver resolver) throws MissingCalendarException {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Calendars.ACCOUNT_NAME, account.name);
        values.put(CalendarContract.Calendars.ACCOUNT_TYPE, MinervaConfig.ACCOUNT_TYPE);
        values.put(CalendarContract.Calendars.NAME, getCalendarName());
        values.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, getCalendarName());
        values.put(CalendarContract.Calendars.CALENDAR_COLOR, getCalendarColour());
        values.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_RESPOND);
        values.put(CalendarContract.Calendars.OWNER_ACCOUNT, account.name);
        //values.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, getCalendarTimeZone());
        values.put(CalendarContract.Calendars.CAN_MODIFY_TIME_ZONE, 1);
        values.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        values.put(CalendarContract.Calendars.ALLOWED_REMINDERS, "METHOD_DEFAULT");

        // Add the calendar.
        Uri uri = adapterUri(CalendarContract.Calendars.CONTENT_URI, account);

        try {
            return resolver.insert(uri, values);
        } catch (IllegalArgumentException e) {
            throw new MissingCalendarException(e);
        }
    }

    /**
     * @return The name of the calendar.
     */
    private String getCalendarName() {
        return context.getString(R.string.minerva_calender_name);
    }

    /**
     * @return The colour of the calendar.
     */
    private int getCalendarColour() {
        return ContextCompat.getColor(context, R.color.hydra_primary_dark_colour);
    }

    /**
     * Convert an AgendaItem to ContentValues for the android Calendar.
     *
     * @param calendarId The ID of the calendar.
     * @param item       The item to convert.
     *
     * @return The converted item.
     */
    private ContentValues toCalendarValues(long calendarId, AgendaItem item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CalendarContract.Events.CALENDAR_ID, calendarId);
        contentValues.put(CalendarContract.Events.TITLE, getTitleFor(item));
        contentValues.put(CalendarContract.Events.DESCRIPTION, getDescriptionFor(item));
        contentValues.put(CalendarContract.Events.DTSTART, item.getStartDate().toInstant().toEpochMilli());
        contentValues.put(CalendarContract.Events.DTEND, item.getEndDate().toInstant().toEpochMilli());
        // Convert Java 8 TimeZone to old TimeZone
        @SuppressWarnings("UseOfObsoleteDateTimeApi")
        TimeZone zone = DateTimeUtils.toTimeZone(item.getStartDate().getOffset());
        contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, zone.getID());
        contentValues.put(CalendarContract.Events.EVENT_LOCATION, item.getLocation());
        contentValues.put(CalendarContract.Events.SYNC_DATA1, item.getItemId());
        contentValues.put(CalendarContract.Events.CUSTOM_APP_PACKAGE, BuildConfig.APPLICATION_ID);
        contentValues.put(CalendarContract.Events.CUSTOM_APP_URI, item.getUri());
        return contentValues;
    }

    /**
     * Get the title for an event in the calendar, intended for the device calendar. This method will consider the user
     * preferences in regards to prefixing with the course's name and using acronyms or not.
     *
     * @param item The item to get the title for.
     *
     * @return The title.
     */
    private String getTitleFor(AgendaItem item) {
        String title;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getBoolean(MinervaPreferenceFragment.PREF_PREFIX_EVENT_TITLES, false)
                && !TextUtils.equals(item.getTitle(), item.getCourse().getTitle())) {
            String courseTitle;
            if (preferences.getBoolean(MinervaPreferenceFragment.PREF_PREFIX_EVENT_ACRONYM, true)) {
                courseTitle = StringUtils.generateAcronymFor(item.getCourse().getTitle());
            } else {
                courseTitle = item.getCourse().getTitle();
            }
            title = context.getString(R.string.minerva_calendar_device_event_title, courseTitle, item.getTitle());
        } else {
            title = item.getTitle();
        }
        return title;
    }

    /**
     * Get the description for an event in the calendar, intended for the device calendar. This method will append the
     * course's name to the description, if the user preference for prefixing event titles was turned on.
     *
     * @param item The item to get the description from.
     *
     * @return The description.
     */
    private String getDescriptionFor(AgendaItem item) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getBoolean(MinervaPreferenceFragment.PREF_PREFIX_EVENT_TITLES, false)) {
            String original = item.getContent();
            String description = context.getString(R.string.minerva_calendar_device_description, item.getCourse().getTitle());
            if (TextUtils.isEmpty(original)) {
                return description;
            } else {
                return original + "\n\n" + description;
            }
        } else {
            return item.getContent();
        }
    }

    private static class MissingCourseException extends RuntimeException {

    }
}