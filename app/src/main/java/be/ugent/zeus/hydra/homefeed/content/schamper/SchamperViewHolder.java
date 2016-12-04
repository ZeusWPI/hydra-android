package be.ugent.zeus.hydra.homefeed.content.schamper;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.SchamperArticleActivity;
import be.ugent.zeus.hydra.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.homefeed.HomeFeedAdapter;
import be.ugent.zeus.hydra.homefeed.content.HideableViewHolder;
import be.ugent.zeus.hydra.models.schamper.Article;
import be.ugent.zeus.hydra.utils.DateUtils;
import com.squareup.picasso.Picasso;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Home feed view holder for Schamper articles.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public class SchamperViewHolder extends HideableViewHolder {

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

        Picasso.with(this.itemView.getContext()).load(article.getImage()).fit().centerInside().into(image);

        this.itemView.setOnClickListener(v -> SchamperArticleActivity.launchWithAnimation((Activity) itemView.getContext(), image, "hero", article));
    }
}