package be.ugent.zeus.hydra.ui.minerva;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.common.BaseActivity;
import be.ugent.zeus.hydra.ui.preferences.MinervaFragment;
import be.ugent.zeus.hydra.data.database.minerva.AnnouncementDao;
import be.ugent.zeus.hydra.data.models.minerva.Announcement;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.utils.NetworkUtils;
import be.ugent.zeus.hydra.ui.common.html.PicassoImageGetter;
import be.ugent.zeus.hydra.ui.common.html.Utils;
import org.threeten.bp.ZonedDateTime;

/**
 * Show a Minerva announcement.
 *
 * @author Niko Strijbol
 */
public class AnnouncementActivity extends BaseActivity {

    public static final String ARG_ANNOUNCEMENT = "announcement_view";

    private static final String ONLINE_URL_MOBILE = "https://minerva.ugent.be/mobile/courses/%s/announcement";
    private static final String ONLINE_URL_DESKTOP = "http://minerva.ugent.be/main/announcements/announcements.php?cidReq=%s";

    private Announcement announcement;
    private AnnouncementDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minerva_announcement);

        Intent intent = getIntent();
        announcement = intent.getParcelableExtra(ARG_ANNOUNCEMENT);

        dao = new AnnouncementDao(getApplicationContext());

        TextView title = $(R.id.title);
        TextView date = $(R.id.date);
        TextView text = $(R.id.text);
        TextView author = $(R.id.author);
        TextView course = $(R.id.course);

        course.setText(announcement.getCourse().getTitle());

        if (announcement.getLecturer() != null) {
            author.setText(announcement.getLecturer());
        }

        if (announcement.getDate() != null) {
            date.setText(DateUtils.relativeDateTimeString(announcement.getDate(), this));
        }

        if (announcement.getContent() != null) {
            text.setText(Utils.fromHtml(announcement.getContent(), new PicassoImageGetter(text, getResources(), this)));
            text.setMovementMethod(LinkMovementMethod.getInstance());
        }

        if (announcement.getTitle() != null) {
            title.setText(announcement.getTitle());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.minerva_announcement_link:
                NetworkUtils.maybeLaunchBrowser(this, getOnlineUrl());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_minerva_announcement, menu);
        tintToolbarIcons(menu, R.id.minerva_announcement_link);
        return super.onCreateOptionsMenu(menu);
    }

    private String getOnlineUrl() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean(MinervaFragment.PREF_USE_MOBILE_URL, false)) {
            return String.format(ONLINE_URL_MOBILE, announcement.getCourse().getId());
        } else {
            return String.format(ONLINE_URL_DESKTOP, announcement.getCourse().getId());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Set the read date if needed
        if (!announcement.isRead()) {
            announcement.setRead(ZonedDateTime.now());
            AsyncTask.execute(() -> dao.update(announcement));
        }
    }
}