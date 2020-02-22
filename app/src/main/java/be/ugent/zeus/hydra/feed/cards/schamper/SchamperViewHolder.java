package be.ugent.zeus.hydra.feed.cards.schamper;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.CardViewHolder;
import be.ugent.zeus.hydra.schamper.Article;
import be.ugent.zeus.hydra.common.ArticleViewer;
import be.ugent.zeus.hydra.feed.HomeFeedAdapter;
import be.ugent.zeus.hydra.feed.cards.PriorityUtils;
import be.ugent.zeus.hydra.utils.DateUtils;

/**
 * Home feed view holder for Schamper articles.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public class SchamperViewHolder extends CardViewHolder {

    private final TextView title;
    private final TextView date;
    private final TextView author;
    private final ImageView image;

    public SchamperViewHolder(View v, HomeFeedAdapter adapter) {
        super(v, adapter);
        title = v.findViewById(R.id.title);
        date = v.findViewById(R.id.date);
        author = v.findViewById(R.id.author);
        image = v.findViewById(R.id.image);
    }

    @Override
    public void populate(Card card) {
        super.populate(card);

        Article article = card.<SchamperCard>checkCard(Card.Type.SCHAMPER).getArticle();

        title.setText(article.getTitle());

        // Construct coloured text
        Spannable category;
        if (article.hasCategoryColour()) {
            int colour = Color.parseColor(article.getCategoryColour());
            category = new SpannableString(article.getCategory());
            category.setSpan(new ForegroundColorSpan(colour), 0, category.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            category = new SpannableString(article.getCategory());
        }

        date.setText(TextUtils.concat(DateUtils.relativeDateTimeString(article.getPubDate(), itemView.getContext()), " • ", category));
        author.setText(article.getAuthor());

        PriorityUtils.loadThumbnail(itemView.getContext(), article.getImage(), image);

        this.itemView.setOnClickListener(v -> ArticleViewer.viewArticle(v.getContext(), article, adapter.getCompanion().getHelper()));
    }
}
