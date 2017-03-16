package be.ugent.zeus.hydra.homefeed.content.schamper;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.homefeed.HomeFeedAdapter;
import be.ugent.zeus.hydra.homefeed.content.FeedUtils;
import be.ugent.zeus.hydra.homefeed.content.HideableViewHolder;
import be.ugent.zeus.hydra.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.data.models.schamper.Article;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.utils.customtabs.ActivityHelper;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Home feed view holder for Schamper articles.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public class SchamperViewHolder extends HideableViewHolder {

    private static final String TAG = "SchamperViewHolder";

    private final TextView title;
    private final TextView date;
    private final TextView author;
    private final ImageView image;

    public SchamperViewHolder(View v, HomeFeedAdapter adapter) {
        super(v, adapter);
        title = $(v, R.id.title);
        date = $(v, R.id.date);
        author = $(v, R.id.author);
        image = $(v, R.id.image);
    }

    @Override
    public void populate(HomeCard card) {
        super.populate(card);

        Article article = card.<SchamperCard>checkCard(HomeCard.CardType.SCHAMPER).getArticle();

        title.setText(article.getTitle());
        date.setText(DateUtils.relativeDateTimeString(article.getPubDate(), itemView.getContext()));
        author.setText(article.getAuthor());

        FeedUtils.loadThumbnail(itemView.getContext(), article.getImage(), image);

        this.itemView.setOnClickListener(v -> {
            ActivityHelper helper = adapter.getHelper();
            if (helper != null) {
                helper.openCustomTab(Uri.parse(article.getLink()));
            } else {
                Log.w(TAG, "Could not acquire ActivityHelper, aborting article.");
                Toast.makeText(v.getContext(), R.string.schamper_error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}