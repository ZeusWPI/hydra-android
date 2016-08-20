package be.ugent.zeus.hydra.activities.minerva;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.ToolbarActivity;
import be.ugent.zeus.hydra.models.minerva.Announcement;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.utils.html.PicassoImageGetter;
import be.ugent.zeus.hydra.utils.html.Utils;

/**
 * Show a Minerva announcement.
 * @author Niko Strijbol
 */
public class AnnouncementActivity extends ToolbarActivity {

    public static final String ARG_ANNOUNCEMENT = "announcement_view";

    private static final String ONLINE_URL_MOBILE = "https://minerva.ugent.be/mobile/courses/%s/announcement";
    private static final String ONLINE_URL_DESKTOP = "http://minerva.ugent.be/main/announcements/announcements.php?cidReq=%s";

    private Announcement announcement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);

        Intent intent = getIntent();
        announcement = intent.getParcelableExtra(ARG_ANNOUNCEMENT);

        TextView title = $(R.id.title);
        TextView date = $(R.id.date);
        TextView text = $(R.id.text);
        TextView author = $(R.id.author);

        if(announcement.getLecturer() != null ) {
            author.setText(announcement.getLecturer());
        }

        if(announcement.getDate() != null) {
            date.setText(DateUtils.relativeDateString(announcement.getDate(), date.getContext()));
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
        super.onCreateOptionsMenu(menu);

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_minerva_announcement, menu);

        // We need to manually set the color of this Drawable for some reason.
        tintToolbarIcons(menu, R.id.minerva_announcement_link);

        return true;
    }

    private String getOnlineUrl() {
        //TODO: use preferences
        return String.format(ONLINE_URL_DESKTOP, announcement.getCourse().getId());
    }
}