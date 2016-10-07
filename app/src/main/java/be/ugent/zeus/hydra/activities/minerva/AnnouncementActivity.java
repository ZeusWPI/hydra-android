package be.ugent.zeus.hydra.activities.minerva;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.ToolbarActivity;
import be.ugent.zeus.hydra.fragments.preferences.MinervaFragment;
import be.ugent.zeus.hydra.minerva.announcement.AnnouncementDao;
import be.ugent.zeus.hydra.models.minerva.Announcement;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.utils.html.PicassoImageGetter;
import be.ugent.zeus.hydra.utils.html.Utils;
import org.threeten.bp.ZonedDateTime;

/**
 * Show a Minerva announcement.
 * @author Niko Strijbol
 */
public class AnnouncementActivity extends ToolbarActivity {

    public static final String ARG_ANNOUNCEMENT = "announcement_view";

    public static final int RESULT_ANNOUNCEMENT = 1;
    public static final String RESULT_ARG_ANNOUNCEMENT_ID = "argPos";
    public static final String RESULT_ARG_ANNOUNCEMENT_READ = "argRead";

    private static final String ONLINE_URL_MOBILE = "https://minerva.ugent.be/mobile/courses/%s/announcement";
    private static final String ONLINE_URL_DESKTOP = "http://minerva.ugent.be/main/announcements/announcements.php?cidReq=%s";

    private Announcement announcement;
    private AnnouncementDao dao;
    private boolean read = false;
    private boolean resultSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);

        Intent intent = getIntent();
        announcement = intent.getParcelableExtra(ARG_ANNOUNCEMENT);

        dao = new AnnouncementDao(getApplicationContext());

        TextView title = $(R.id.title);
        TextView date = $(R.id.date);
        TextView text = $(R.id.text);
        TextView author = $(R.id.author);

        if(announcement.getLecturer() != null ) {
            author.setText(announcement.getLecturer());
        }

        if(announcement.getDate() != null) {
            date.setText(DateUtils.relativeDateTimeString(announcement.getDate(), date.getContext()));
        }

        if(announcement.getContent() != null) {
            text.setText(Utils.fromHtml(announcement.getContent(), new PicassoImageGetter(text, getResources(), this)));
            text.setMovementMethod(LinkMovementMethod.getInstance());
        }

        if(announcement.getTitle() != null) {
            title.setText(announcement.getTitle());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.minerva_announcement_link:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getOnlineUrl())));
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
        if(preferences.getBoolean(MinervaFragment.PREF_USE_MOBILE_URL, false)) {
            return String.format(ONLINE_URL_MOBILE, announcement.getCourse().getId());
        } else {
            return String.format(ONLINE_URL_DESKTOP, announcement.getCourse().getId());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Set the read date if needed
        if(!announcement.isRead()) {
            read = true;
            announcement.setRead(ZonedDateTime.now());
        }
        setResult();
    }

    @Override
    protected void onStop() {
        super.onStop();

        //Save the things
        if(read) {
            dao.update(announcement);
            Intent intent = getIntent();
            intent.putExtra(ARG_ANNOUNCEMENT, (Parcelable) announcement);
            setIntent(intent);
        }
    }

    private void setResult() {
        if(!resultSet) {
            Intent result = new Intent();
            result.putExtra(RESULT_ARG_ANNOUNCEMENT_ID, announcement.getItemId());
            result.putExtra(RESULT_ARG_ANNOUNCEMENT_READ, read);
            setResult(RESULT_OK, result);
            resultSet = true;
        }
    }
}