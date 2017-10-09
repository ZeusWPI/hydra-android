package be.ugent.zeus.hydra.data.sync.minerva.helpers;

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
import android.util.Log;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.ChannelCreator;
import be.ugent.zeus.hydra.data.auth.MinervaConfig;
import be.ugent.zeus.hydra.data.database.minerva.AgendaDao;
import be.ugent.zeus.hydra.data.models.minerva.Agenda;
import be.ugent.zeus.hydra.data.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.data.network.requests.minerva.AgendaDuplicateDetector;
import be.ugent.zeus.hydra.data.network.requests.minerva.AgendaRequest;
import be.ugent.zeus.hydra.data.sync.Synchronisation;
import be.ugent.zeus.hydra.repository.requests.RequestException;
import be.ugent.zeus.hydra.repository.requests.Result;
import be.ugent.zeus.hydra.ui.minerva.CalendarPermissionActivity;
import be.ugent.zeus.hydra.ui.preferences.MinervaFragment;
import java8.util.Maps;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import jonathanfinerty.once.Once;
import org.threeten.bp.DateTimeUtils;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

import java.util.Collection;
import java.util.Map;
import java.util.TimeZone;

/**
 * Synchronises the calendar with Minerva and, if permissions allow, with the built-in calendar.
 *
 * @author Niko Strijbol
 */
public class CalendarSync {

    private static long NO_CALENDAR = -1;

    private static final String TAG = "CalendarSync";

    private final AgendaDao calendarDao;
    private final Context context;

    public CalendarSync(AgendaDao calendarDao, Context context) {
        this.calendarDao = calendarDao;
        this.context = context;
    }

    public void synchronise(Account account, boolean isInitialSync) throws RequestException {

        // Get the calendar from the server in one go. This does mean it is possible to receive a calendar item
        // for which there is no course in the local database. If that is the case, we just let the sql propagate.
        // Android should automatically start a new synchronisation for us.
        AgendaRequest request = new AgendaRequest(context, account);
        // We synchronise two month behind and four months ahead.
        ZonedDateTime now = LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault());
        request.setStart(now.minusMonths(2));
        // End time. We take 4 month (+1 day for the start time).
        request.setEnd(now.plusMonths(4).plusDays(1));

        // The result.
        Result<Agenda> agendaResult = request.performRequest(null);

        // Do we want to use the duplication detection or not?
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        Agenda agenda;

        if (preferences.getBoolean(MinervaFragment.PREF_DETECT_DUPLICATES, false)) {
            agenda = agendaResult.map(new AgendaDuplicateDetector()).getOrThrow();
        } else {
            agenda = agendaResult.getOrThrow();
        }

        // Get existing items and the calendar ids.
        Map<AgendaItem, Long> allItems = calendarDao.getAll();

        Collection<Integer> existingIds = StreamSupport.stream(allItems.keySet())
                .map(AgendaItem::getItemId)
                .collect(Collectors.toList());

        Synchronisation<AgendaItem, Integer> sync = new Synchronisation<>(
                existingIds,
                agenda.getItems(),
                AgendaItem::getItemId);
        Synchronisation.Diff<AgendaItem, Integer> diff = sync.diff();

        // Save the id's of the built-in items.
        for (AgendaItem item: diff.getUpdated()) {
            item.setCalendarId(Maps.getOrDefault(allItems, item, AgendaItem.NO_CALENDAR_ID));
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
            // First, we synchronise the items to the calendar. This will modify the items if necessary.
            // It will insert Calendar ids to the items that need it.
            synchronizeCalendar(account, isInitialSync, diff);
        }

        // Apply synchronisation. We can only do this after the built-in calendar has been synchronised, since
        // we need the id's of deleted items, and the calendar will adjust the calendar ID's.
        diff.apply(calendarDao);
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

        // Make sure the notification channel is present
        ChannelCreator channelCreator = ChannelCreator.getInstance(context);
        channelCreator.createMinervaAccountChannel();

        // Create a notification and show it to the user.
        Intent intent = new Intent(context, CalendarPermissionActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context, ChannelCreator.MINERVA_ACCOUNT_CHANNEL)
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

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notification);
    }

    /**
     * Synchronise our database with the built-in database.
     */
    private void synchronizeCalendar(Account account, boolean isInitialSync, Synchronisation.Diff<AgendaItem, Integer> diff) {

        // Get the URI for our calendar.
        ContentResolver resolver = context.getContentResolver();
        Uri uri = adapterUri(CalendarContract.Events.CONTENT_URI, account);

        // Get the ID of our calendar.
        long calendarId = getCalendarId(account, resolver);
        if (calendarId == NO_CALENDAR) {
            // Attempt to insert our calendar.
            Uri result = insertCalendar(account, resolver);
            if (result == null) {
                Log.e(TAG, "Inserting the calendar failed for some reason, abort sync.");
                return;
            }
            calendarId = getCalendarId(account, resolver);
        } else if (isInitialSync) {
            // Remove existing things.
            Uri calendarUri = adapterUri(CalendarContract.Calendars.CONTENT_URI, account);
            resolver.delete(ContentUris.withAppendedId(calendarUri, calendarId), null, null);
            Uri result = insertCalendar(account, resolver);
            if (result == null) {
                Log.e(TAG, "Inserting the calendar failed for some reason, abort sync.");
                return;
            }
            calendarId = getCalendarId(account, resolver);
        }

        // Remove Calendar items we don't need anymore.
        Collection<Long> toRemove = calendarDao.getCalendarIdsForIds(diff.getStaleIds());

        for (long id: toRemove) {
            // We cannot delete non-existing values.
            if (id != AgendaItem.NO_CALENDAR_ID) {
                Uri itemUri = ContentUris.withAppendedId(uri, id);
                resolver.delete(itemUri, null, null);
            }
        }

        // Update Calendar items, as they might have changed.
        for (AgendaItem updatedItem: diff.getUpdated()) {
            long itemCalendarId = updatedItem.getCalendarId();
            ContentValues values = toCalendarValues(calendarId, updatedItem);
            // The item was not found.
            if (itemCalendarId == AgendaItem.NO_CALENDAR_ID) {
                long id = insert(resolver, values, account);
                updatedItem.setCalendarId(id);
            } else { // Update the item.
                Uri itemUri = ContentUris.withAppendedId(uri, itemCalendarId);
                resolver.update(itemUri, values, null, null);
            }
        }

        // Add new items to the calendar.
        for (AgendaItem newItem: diff.getNew()) {
            ContentValues values = toCalendarValues(calendarId, newItem);
            long id = insert(resolver, values, account);
            newItem.setCalendarId(id);
        }
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
        if (result == null) {
            Log.e(TAG, "Error while inserting in calendar, abort this item.");
            return AgendaItem.NO_CALENDAR_ID;
        }
        return Long.parseLong(result.getLastPathSegment());
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
     * Get the ID of the calendar for our account.
     *
     * @param account The account.
     *
     * @return The ID or {@link #NO_CALENDAR} if there is no calendar.
     */
    private long getCalendarId(Account account, ContentResolver resolver) {

        String selection =
                CalendarContract.Calendars.ACCOUNT_NAME +
                        " = ? AND " +
                        CalendarContract.Calendars.ACCOUNT_TYPE +
                        " = ? ";

        String[] selArgs = new String[] {account.name, MinervaConfig.ACCOUNT_TYPE};

        Cursor cursor = resolver
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

    /**
     * Add our calendar.
     *
     * @param account The account for which the calendar must be added.
     * @param resolver The content resolver.
     */
    private Uri insertCalendar(Account account, ContentResolver resolver) {
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

        // Add the calendar.
        Uri uri = adapterUri(CalendarContract.Calendars.CONTENT_URI, account);
        return resolver.insert(uri, values);
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
        return ContextCompat.getColor(context, R.color.ugent_blue_dark);
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
        contentValues.put(CalendarContract.Events.TITLE, item.getTitle());
        contentValues.put(CalendarContract.Events.DESCRIPTION, item.getContent());
        contentValues.put(CalendarContract.Events.DTSTART, item.getStartDate().toInstant().toEpochMilli());
        contentValues.put(CalendarContract.Events.DTEND, item.getEndDate().toInstant().toEpochMilli());
        // Convert Java 8 TimeZone to old TimeZone
        @SuppressWarnings("UseOfObsoleteDateTimeApi")
        TimeZone zone = DateTimeUtils.toTimeZone(item.getStartDate().getZone());
        contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, zone.getID());
        contentValues.put(CalendarContract.Events.EVENT_LOCATION, item.getLocation());
        contentValues.put(CalendarContract.Events.SYNC_DATA1, item.getItemId());
        contentValues.put(CalendarContract.Events.CUSTOM_APP_PACKAGE, BuildConfig.APPLICATION_ID);
        contentValues.put(CalendarContract.Events.CUSTOM_APP_URI, item.getUri());
        return contentValues;
    }
}
