package be.ugent.zeus.hydra.minerva.agenda.sync;

import android.Manifest;
import android.accounts.Account;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.*;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.fragments.minerva.CalendarPermissionActivity;
import be.ugent.zeus.hydra.minerva.agenda.AgendaDao;
import be.ugent.zeus.hydra.minerva.auth.MinervaConfig;
import be.ugent.zeus.hydra.minerva.requests.AgendaRequest;
import be.ugent.zeus.hydra.minerva.sync.MinervaAdapter;
import be.ugent.zeus.hydra.minerva.sync.SyncBroadcast;
import be.ugent.zeus.hydra.models.minerva.Agenda;
import be.ugent.zeus.hydra.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

/**
 * Sync adapter for the Minerva Calendar.
 *
 * @author Niko Strijbol
 */
public class Adapter extends MinervaAdapter {

    private static final String TAG = "MinervaCalendarAdapter";

    private static long NO_CALENDAR = -1;

    public Adapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    protected void onPerformCheckedSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult results, boolean isFirstSync) throws RequestFailureException {

        final AgendaDao dao = new AgendaDao(getContext());

        // The calendar is synchronized by removing all elements and then re-adding them.
        // This means we don't have to clear anything on the first sync.

        // Synchronise the calendar.
        AgendaRequest agendaRequest = new AgendaRequest(getContext(), account);
        ZonedDateTime now = LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault());
        // Start time.
        agendaRequest.setStart(now);
        // End time. We take 1 month (+1 day for the start time).
        agendaRequest.setEnd(now.plusMonths(1).plusDays(1));

        dao.replace(agendaRequest.performRequest().getItems());

        // First we check for permissions.
        int permissionRead = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CALENDAR);
        int permissionWrite = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CALENDAR);

        // If there is no permission
        if (permissionRead == PackageManager.PERMISSION_DENIED || permissionWrite == PackageManager.PERMISSION_DENIED) {
            Log.d(TAG, "Missing permissions");
            handleNoPermission();
            return;
        }

        Agenda agenda = new AgendaRequest(getContext(), account).performRequest();

        // Get the calender ID
        long id = getCalendarId(account);
        // Make it if necessary
        if (id == NO_CALENDAR) {
            Log.d(TAG, "Adding calendar...");
            insertCalendar(account);
        } else {
            Log.d(TAG, "Calendar already present");
        }

        Uri uri = calendarUri(CalendarContract.Events.CONTENT_URI, account);
        ContentResolver contentResolver = getContext().getContentResolver();

        //TODO: sync
        for (AgendaItem item: dao.getAll()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(CalendarContract.Events.CALENDAR_ID, id);
            contentValues.put(CalendarContract.Events.TITLE, item.getCourse().getTitle() + ": " + item.getTitle());
            contentValues.put(CalendarContract.Events.DESCRIPTION, item.getContent());
            contentValues.put(CalendarContract.Events.ORGANIZER, item.getLastEditUser());
            contentValues.put(CalendarContract.Events.DTSTART, item.getStartDate().toInstant().toEpochMilli());
            contentValues.put(CalendarContract.Events.DTEND, item.getEndDate().toInstant().toEpochMilli());
            contentValues.put(CalendarContract.Events.EVENT_LOCATION, item.getLocation());
            contentResolver.insert(uri, contentValues);
        }

        broadcast.publishIntent(SyncBroadcast.SYNC_AGENDA);
    }

    private static Uri calendarUri(Uri uri, Account account) {
        return uri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, account.name)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
                .build();
    }

    private void insertCalendar(Account account) {
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
        ContentResolver resolver = getContext().getContentResolver();
        Uri uri = calendarUri(CalendarContract.Calendars.CONTENT_URI, account);
        resolver.insert(uri, values);
    }

    private String getCalendarName() {
        return getContext().getString(R.string.minerva_calender_name);
    }

    private int getCalendarColour() {
        return ContextCompat.getColor(getContext(), R.color.ugent_blue_dark);
    }

    private String getCalendarTimeZone() {
        return "Europe/Brussels";
    }

    private long getCalendarId(Account account) {
        String[] projection = new String[] {CalendarContract.Calendars._ID};
        String selection =
                CalendarContract.Calendars.ACCOUNT_NAME +
                " = ? AND " +
                CalendarContract.Calendars.ACCOUNT_TYPE +
                " = ? ";

        String[] selArgs = new String[] {
                account.name,
                MinervaConfig.ACCOUNT_TYPE
        };

        Cursor cursor = getContext()
                .getContentResolver()
                .query(
                        CalendarContract.Calendars.CONTENT_URI,
                        projection,
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

    private void handleNoPermission() {

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
}