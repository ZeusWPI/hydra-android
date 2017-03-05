package be.ugent.zeus.hydra.minerva.agenda.sync;

import android.Manifest;
import android.accounts.Account;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.*;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.util.SparseArray;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.minerva.CalendarPermissionActivity;
import be.ugent.zeus.hydra.minerva.agenda.AgendaDao;
import be.ugent.zeus.hydra.minerva.auth.MinervaConfig;
import be.ugent.zeus.hydra.minerva.course.CourseDao;
import be.ugent.zeus.hydra.minerva.requests.AgendaRequest;
import be.ugent.zeus.hydra.minerva.sync.MinervaAdapter;
import be.ugent.zeus.hydra.minerva.sync.SyncBroadcast;
import be.ugent.zeus.hydra.minerva.sync.SyncUtils;
import be.ugent.zeus.hydra.minerva.sync.Synchronisation;
import be.ugent.zeus.hydra.models.minerva.Agenda;
import be.ugent.zeus.hydra.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.models.minerva.Course;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;
import java8.util.function.Functions;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import jonathanfinerty.once.Once;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

import java.util.*;

/**
 * Sync adapter for the Minerva calendar.
 *
 * @author Niko Strijbol
 */
public class Adapter extends MinervaAdapter {

    private static final String TAG = "MinervaCalendarAdapter";

    private static long NO_CALENDAR = -1;

    private AgendaDao dao;

    public Adapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    protected void onPerformCheckedSync(Account account,
                                        Bundle extras,
                                        String authority,
                                        ContentProviderClient provider,
                                        SyncResult results,
                                        boolean isFirstSync) throws RequestFailureException {

        dao = new AgendaDao(getContext());
        final CourseDao courseDao = new CourseDao(getContext());
        List<Course> courses = courseDao.getAll();

        // Delete everything on the first sync.
        if (isFirstSync) {
            dao.deleteAll();
        }

        AgendaRequest agendaRequest = new AgendaRequest(getContext(), account);
        request = agendaRequest; // Enable errors.
        ZonedDateTime now = LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault());
        // Start time.
        agendaRequest.setStart(now.minusMonths(2));
        // End time. We take 1 month (+1 day for the start time).
        agendaRequest.setEnd(now.plusMonths(1).plusDays(1));

        Agenda agenda = agendaRequest.performRequest();

        Collection<Integer> existingIds = dao.getAllIds();

        Synchronisation<AgendaItem, Integer> sync = new Synchronisation<>(
                existingIds,
                agenda.getItems(),
                AgendaItem::getItemId);
        Synchronisation.Diff<AgendaItem, Integer> diff = sync.diff();

        // Check for permission for Calendar access.
        int permissionRead = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CALENDAR);
        int permissionWrite = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CALENDAR);

        // We only synchronise with the Calendar if we have access.
        if (permissionRead == PackageManager.PERMISSION_DENIED || permissionWrite == PackageManager.PERMISSION_DENIED) {
            Log.d(TAG, "Missing permissions");
            handleNoPermission();
        } else {
            // First, we synchronise the items to the calendar. This will modify the items if necessary.
            // It will insert Calendar ids to the items that need it.
            synchronizeCalendar(courses, account, diff, isFirstSync);
        }

        // Remove stale items from our database.
        dao.delete(diff.getStaleIds());

        // Update existing items in our database.
        dao.update(diff.getUpdated());

        try {
            // Add new items to our database.
            dao.insert(diff.getNew());
        } catch (SQLException e) {
            // This is thrown when the foreign key fails.
            // This means we have an agenda item for a course that doesn't exist!
            // We abort the synchronisation, and launch the course synchronisation.
            Bundle bundle = new Bundle();
            bundle.putBoolean(be.ugent.zeus.hydra.minerva.course.sync.Adapter.EXTRA_SCHEDULE_AGENDA, true);
            SyncUtils.requestSync(account, MinervaConfig.COURSE_AUTHORITY, bundle);

            broadcast.publishIntent(SyncBroadcast.SYNC_ERROR);
        }

        // Synchronisation is finished.
        broadcast.publishIntent(SyncBroadcast.SYNC_AGENDA);
    }

    /**
     * Invoked when the app doesn't have permission to access the calendars. This will issue a notification, if the
     * user hasn't permanently disabled our access. In that case, this does nothing;
     */
    private void handleNoPermission() {
        // Check if we may notify the user.
        if (Once.beenDone(CalendarPermissionActivity.MINERVA_CALENDAR_STOP_ASKING)) {
            return;
        }

        // Create a notification and show it to the user.
        Intent intent = new Intent(getContext(), CalendarPermissionActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(getContext())
                .setSmallIcon(R.drawable.ic_notification_warning)
                .setCategory(NotificationCompat.CATEGORY_ERROR)
                .setContentTitle("Machtigingen voor Hydra")
                .setContentText("Geef toestemming voor de Minerva-agenda")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Hydra heeft machtigingen nodig om Minerva-agenda te synchroniseren.")
                )
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager manager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notification);
    }

    /**
     * Synchronise AgendaItems with the calendar.
     */
    private void synchronizeCalendar(List<Course> courses, Account account, Synchronisation.Diff<AgendaItem, Integer> diff, boolean isFirstSync) {

        ContentResolver resolver = getContext().getContentResolver();
        Uri uri = adapterUri(CalendarContract.Events.CONTENT_URI, account);

        Map<String, Course> courseMap = StreamSupport.stream(courses)
                .collect(Collectors.toMap(Course::getId, Functions.identity()));

        // Add calendar if needed
        long calendarId = getCalendarId(account);
        if (calendarId == NO_CALENDAR) {
            insertCalendar(account, resolver);
            calendarId = getCalendarId(account);
        } else if (isFirstSync) { // If there a calendar and it is the first sync, delete everything in it.
            Uri calendarUri = adapterUri(CalendarContract.Calendars.CONTENT_URI, account);
            resolver.delete(ContentUris.withAppendedId(calendarUri, calendarId), null, null);
            insertCalendar(account, resolver);
            calendarId = getCalendarId(account);
        }

        // Remove Calendar items we don't need anymore.
        Collection<Long> toRemove = dao.getCalendarIdsForIds(diff.getStaleIds());

        for (long id: toRemove) {
            Uri itemUri = ContentUris.withAppendedId(uri, id);
            resolver.delete(itemUri, null, null);
        }

        List<AgendaItem> items = new ArrayList<>(diff.getUpdated());

        // Get agenda id's
        Collection<Long> updatedIds = dao.getCalendarIdsForIds(StreamSupport.stream(items)
                .map(AgendaItem::getItemId)
                .collect(Collectors.toList()));

        SparseArray<Long> map = new SparseArray<>();
        Iterator<Long> itLong = updatedIds.iterator();
        Iterator<AgendaItem> it = items.iterator();
        while (it.hasNext() && itLong.hasNext()) {
            map.append(it.next().getItemId(), itLong.next());
        }

        // Update Calendar items, as they might have changed.
        for (AgendaItem updatedItem: diff.getUpdated()) {
            updatedItem.setCourse(courseMap.get(updatedItem.getCourseId()));
            long itemCalendarId = map.get(updatedItem.getItemId(), AgendaItem.NO_CALENDAR_ID);
            ContentValues values = toCalendarValues(calendarId, updatedItem);
            // The item was not found.
            if (itemCalendarId == AgendaItem.NO_CALENDAR_ID) {
                long id = insert(resolver, values, account);
                updatedItem.setCalendarId(id);
            } else { // Update the item.
                Uri itemUri = ContentUris.withAppendedId(uri, itemCalendarId);
                resolver.update(itemUri, values, null, null);
                updatedItem.setCalendarId(itemCalendarId);
            }
        }

        // Add new items
        for (AgendaItem newItem: diff.getNew()) {
            newItem.setCourse(courseMap.get(newItem.getCourseId()));
            ContentValues values = toCalendarValues(calendarId, newItem);
            long id = insert(resolver, values, account);
            newItem.setCalendarId(id);
        }
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
     * Add our calendar.
     *
     * @param account The account for which the calendar must be added.
     * @param resolver The content resolver.
     */
    private void insertCalendar(Account account, ContentResolver resolver) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Calendars.ACCOUNT_NAME, account.name);
        values.put(CalendarContract.Calendars.ACCOUNT_TYPE, MinervaConfig.ACCOUNT_TYPE);
        values.put(CalendarContract.Calendars.NAME, getCalendarName());
        values.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, getCalendarName());
        values.put(CalendarContract.Calendars.CALENDAR_COLOR, getCalendarColour());
        values.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_RESPOND);
        values.put(CalendarContract.Calendars.OWNER_ACCOUNT, account.name);
        values.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, getCalendarTimeZone());
        values.put(CalendarContract.Calendars.CAN_MODIFY_TIME_ZONE, 1);
        values.put(CalendarContract.Calendars.SYNC_EVENTS, 1);

        // Add the calendar.
        Uri uri = adapterUri(CalendarContract.Calendars.CONTENT_URI, account);
        Uri result = resolver.insert(uri, values);
    }

    /**
     * Convert an AgendaItem to ContentValues for the android Calendar.
     *
     * @param calendarId The ID of the calendar.
     * @param item The item to convert.
     * @return The converted item.
     */
    private ContentValues toCalendarValues(long calendarId, AgendaItem item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CalendarContract.Events.CALENDAR_ID, calendarId);
        contentValues.put(CalendarContract.Events.TITLE, item.getCourse().getTitle() + ": " + item.getTitle());
        contentValues.put(CalendarContract.Events.DESCRIPTION, item.getContent());
        contentValues.put(CalendarContract.Events.DTSTART, item.getStartDate().toInstant().toEpochMilli());
        contentValues.put(CalendarContract.Events.DTEND, item.getEndDate().toInstant().toEpochMilli());
        contentValues.put(CalendarContract.Events.EVENT_LOCATION, item.getLocation());
        contentValues.put(CalendarContract.Events.SYNC_DATA1, item.getItemId());
        return contentValues;
    }

    /**
     * Insert an item into the calendar.
     *
     * @param resolver The content resolver.
     * @param values The values of the item to insert.
     * @return The ID of the item.
     */
    private long insert(ContentResolver resolver, ContentValues values, Account account) {
        Uri result = resolver.insert(adapterUri(CalendarContract.Events.CONTENT_URI, account), values);
        return Long.parseLong(result.getLastPathSegment());
    }

    /**
     * @return The name of the calendar.
     */
    private String getCalendarName() {
        return getContext().getString(R.string.minerva_calender_name);
    }

    /**
     * @return The colour of the calendar.
     */
    private int getCalendarColour() {
        return ContextCompat.getColor(getContext(), R.color.ugent_blue_dark);
    }

    /**
     * @return Time zone of the calendar.
     */
    private String getCalendarTimeZone() {
        return "Europe/Brussels";
    }

    /**
     * Get the ID of the calendar for our account.
     *
     * @param account The account.
     *
     * @return The ID or {@link #NO_CALENDAR} if there is no calendar.
     */
    private long getCalendarId(Account account) {

        String selection =
                CalendarContract.Calendars.ACCOUNT_NAME +
                " = ? AND " +
                CalendarContract.Calendars.ACCOUNT_TYPE +
                " = ? ";

        String[] selArgs = new String[] {account.name, MinervaConfig.ACCOUNT_TYPE};

        Cursor cursor = getContext()
                .getContentResolver()
                .query(
                        CalendarContract.Calendars.CONTENT_URI,
                        new String[] {CalendarContract.Calendars._ID},
                        selection,
                        selArgs,
                        null
                );
        try {
            if (cursor.moveToFirst()) {
                return cursor.getLong(0);
            }
        } finally {
            cursor.close();
        }

        return NO_CALENDAR;
    }

    @Override
    protected void afterSync(Account account, Bundle extras, boolean isFirstSync) {
        if (isFirstSync) {
            long frequency = SyncUtils.frequencyFor(getContext(), MinervaConfig.CALENDAR_AUTHORITY);
            SyncUtils.enable(account, MinervaConfig.CALENDAR_AUTHORITY, frequency);
        }
    }
}