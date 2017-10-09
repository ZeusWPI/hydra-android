package be.ugent.zeus.hydra.data.sync.minerva;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.ChannelCreator;
import be.ugent.zeus.hydra.data.models.minerva.Announcement;
import be.ugent.zeus.hydra.data.models.minerva.Course;
import be.ugent.zeus.hydra.ui.common.html.Utils;
import be.ugent.zeus.hydra.ui.main.MainActivity;
import be.ugent.zeus.hydra.ui.minerva.AnnouncementActivity;
import be.ugent.zeus.hydra.ui.minerva.overview.CourseActivity;

import java.util.Collection;
import java.util.List;

import static android.support.v4.app.NotificationCompat.CATEGORY_EMAIL;

/**
 * Contains a bunch of helper methods to do notifications.
 *
 * @author Niko Strijbol
 */
public class NotificationHelper {

    public static final int NOTIFICATION_ID = 5646;
    private static final String TAG = "AnnounceNotiBuilder";

    private static final int MAX_NUMBER_OF_LINES = 5;

    private final Context context;

    @DrawableRes
    private int smallIcon;

    public NotificationHelper(Context context) {
        this.context = context.getApplicationContext();
        //Set default values
        smallIcon = R.drawable.ic_notification_announcement;
    }

    /**
     * Remove all notifications. This will remove all notifications, but this is necessary: we can't remove
     * notifications with an ID alone, so otherwise we would have to track all active notifications.
     *
     * @param context The context.
     */
    public static void cancelAll(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
    }

    private String stripHtml(String containingHtml) {
        return Utils.fromHtml(containingHtml).toString();
    }

    private PendingIntent upIntentOne(Announcement announcement) {
        Intent resultIntent = new Intent(context, AnnouncementActivity.class);
        resultIntent.putExtra(AnnouncementActivity.ARG_ANNOUNCEMENT, (Parcelable) announcement);

        Intent parentIntent = new Intent(context, CourseActivity.class);
        parentIntent.putExtra(CourseActivity.ARG_COURSE, (Parcelable) announcement.getCourse());

        return TaskStackBuilder.create(context)
                .addNextIntent(mainActivity())
                .addNextIntent(parentIntent)
                .addNextIntent(resultIntent)
                .getPendingIntent(announcement.getItemId(), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent upIntentMore(Course course) {
        Intent resultIntent = new Intent(context, CourseActivity.class);
        resultIntent.putExtra(CourseActivity.ARG_COURSE, (Parcelable) course);

        return TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(mainActivity())
                .addNextIntent(resultIntent)
                .getPendingIntent(course.getId().hashCode(), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private Intent mainActivity() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MainActivity.ARG_TAB, R.id.drawer_minerva);
        return intent;
    }

    /**
     * Show one or more notifications for announcements for a course.
     *
     * @param course        The course to show the announcements for.
     * @param announcements The announcements to show.
     */
    public void showNotificationsFor(Course course, List<Announcement> announcements) {

        if (announcements.isEmpty()) {
            return;
        }

        // Ensure the notification channel exists.
        ChannelCreator channelCreator = ChannelCreator.getInstance(context);
        channelCreator.createMinervaAnnouncementChannel();

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create and publish a notification for each announcement.
        for (Announcement announcement : announcements) {

            // Ensure course is set.
            announcement.setCourse(course);

            String stripped = stripHtml(announcement.getContent());

            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.setBigContentTitle(announcement.getTitle());
            bigTextStyle.bigText(stripped);
            bigTextStyle.setSummaryText(announcement.getLecturer());

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ChannelCreator.MINERVA_ANNOUNCEMENT_CHANNEL);
            builder.setSmallIcon(smallIcon)
                    .setCategory(CATEGORY_EMAIL)
                    .setAutoCancel(true)
                    .setContentText(stripped)
                    .setContentTitle(getNotificationAnnouncementTitle(announcement))
                    .setStyle(bigTextStyle)
                    .setGroup(course.getId())
                    .setContentIntent(upIntentOne(announcement));

            manager.notify(course.getId(), announcement.getItemId(), builder.build());
        }

        // Get the summary for the group notification.
        Notification summary = createGroupSummary(course, announcements);
        manager.notify(course.getId(), course.getId().hashCode(), summary);
    }

    private String getNotificationCourseTitle(Course course) {
        if (TextUtils.isEmpty(course.getTitle())) {
            return context.getString(R.string.announcement_notification_title, course.getCode());
        } else {
            return course.getTitle();
        }
    }

    private String getNotificationAnnouncementTitle(Announcement announcement) {
        if (TextUtils.isEmpty(announcement.getTitle())) {
            return context.getString(R.string.announcement_notification_content);
        } else {
            return announcement.getTitle();
        }
    }

    /**
     * Create a summary notification for the bundle.
     *
     * @param course        The course.
     * @param announcements The announcements.
     *
     * @return The notification builder.
     */
    private Notification createGroupSummary(Course course, List<Announcement> announcements) {

        // First, we create the style for our notification.
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(getNotificationCourseTitle(course));

        // Add lines for the first 5 announcements.
        for (Announcement announcement : announcements.subList(0, Math.max(MAX_NUMBER_OF_LINES, announcements.size()))) {
            inboxStyle.addLine(announcement.getTitle());
        }

        // Add a summary.
        inboxStyle.setSummaryText(context.getResources().getQuantityString(R.plurals.home_feed_announcement_title, announcements.size(), announcements.size()));

        // Secondly, we create the notification itself.

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ChannelCreator.MINERVA_ANNOUNCEMENT_CHANNEL);
        builder.setSmallIcon(smallIcon)
                .setCategory(CATEGORY_EMAIL)
                .setAutoCancel(true)
                .setContentTitle(getNotificationCourseTitle(course))
                .setContentText(context.getResources().getQuantityString(R.plurals.home_feed_announcement_title, announcements.size(), announcements.size()))
                .setContentIntent(upIntentMore(course))
                .setStyle(inboxStyle)
                .setNumber(announcements.size())
                .setGroupSummary(true)
                .setGroup(course.getId());

        return builder.build();
    }

    /**
     * Cancel specified announcements for a course.
     *
     * @param course The course.
     * @param announcementIds The ID's of the announcements.
     */
    public void cancel(Course course, Collection<Integer> announcementIds) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        for (Integer announcementId : announcementIds) {
            manager.cancel(course.getId(), announcementId);
        }
    }
}