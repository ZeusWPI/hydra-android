package be.ugent.zeus.hydra.activities.minerva;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.ToolbarActivity;
import be.ugent.zeus.hydra.models.minerva.Announcement;
import be.ugent.zeus.hydra.recyclerview.adapters.minerva.CourseAnnouncementAdapter;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.utils.html.PicassoImageGetter;
import be.ugent.zeus.hydra.utils.html.Utils;

/**
 * Show a Minerva announcement.
 * @author Niko Strijbol
 */
public class AnnouncementActivity extends ToolbarActivity {

    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);

        Intent intent = getIntent();
        Announcement article = intent.getParcelableExtra(CourseAnnouncementAdapter.AnnouncementViewHolder.PARCEL_NAME);

        TextView title = $(R.id.title);
        TextView date = $(R.id.date);
        TextView text = $(R.id.text);
        TextView author = $(R.id.author);

        if(article.getLecturer() != null ) {
            author.setText(article.getLecturer());
        }

        if(article.getDate() != null) {
            date.setText(DateUtils.relativeDateString(article.getDate(), date.getContext()));
        }

        if(article.getContent() != null) {
            text.setText(Utils.fromHtml(article.getContent(), new PicassoImageGetter(text, getResources(), this)));
            text.setMovementMethod(LinkMovementMethod.getInstance());
        }

        if(article.getTitle() != null) {
            title.setText(article.getTitle());
            this.title = article.getTitle();
        }
    }

    @Override
    protected void sendScreen(HydraApplication application) {
        application.sendScreenName("Minerva announcement > " + title);
    }
}