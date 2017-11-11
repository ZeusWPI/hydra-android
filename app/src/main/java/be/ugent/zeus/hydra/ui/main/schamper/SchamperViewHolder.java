package be.ugent.zeus.hydra.ui.main.schamper;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.domain.models.schamper.Article;
import be.ugent.zeus.hydra.ui.SchamperArticleActivity;
import be.ugent.zeus.hydra.ui.common.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.utils.ColourUtils;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

/**
 * View holder for the schamper fragment.
 *
 * @author Niko Strijbol
 */
class SchamperViewHolder extends DataViewHolder<Article> {

    private final TextView title;
    private final TextView date;
    private final TextView author;
    private final TextView category;
    private final ImageView image;
    private final CardView schamperCardView;

    private final ActivityHelper helper;

    @ColorInt private final int initialTitleColour;
    @ColorInt private final int initialDateColour;
    @ColorInt private final int initialAuthorColour;
    @ColorInt private final int initialCategoryColour;
    @ColorInt private final int initialCardViewColour;

    SchamperViewHolder(View itemView, ActivityHelper helper) {
        super(itemView);

        title = itemView.findViewById(R.id.title);
        initialTitleColour = title.getCurrentTextColor();
        date = itemView.findViewById(R.id.date);
        initialDateColour = date.getCurrentTextColor();
        author = itemView.findViewById(R.id.author);
        initialAuthorColour = author.getCurrentTextColor();
        image = itemView.findViewById(R.id.card_image);
        category = itemView.findViewById(R.id.schamper_category);
        initialCategoryColour = category.getCurrentTextColor();
        schamperCardView = itemView.findViewById(R.id.schamper_card_view);
        initialCardViewColour = schamperCardView.getCardBackgroundColor().getDefaultColor();
        this.helper = helper;
    }

    public void populate(final Article article) {
        title.setText(article.getTitle());
        date.setText(DateUtils.relativeDateTimeString(article.getPubDate(), itemView.getContext()));
        author.setText(article.getAuthor());
        category.setText(article.getCategory());

        if (article.hasCategoryColour()) {
            int colour = Color.parseColor(article.getCategoryColour());
            if (ColourUtils.isDark(colour)) {
                schamperCardView.setCardBackgroundColor(Color.parseColor(article.getCategoryColour()));
                title.setTextColor(Color.WHITE);
                date.setTextColor(Color.WHITE);
                author.setTextColor(Color.WHITE);
                category.setTextColor(Color.WHITE);
            } else {
                setDefaultColours();
            }
        } else {
            setDefaultColours();
        }

        if (NetworkUtils.isMeteredConnection(itemView.getContext())) {
            Picasso.with(this.itemView.getContext()).load(article.getImage()).into(image);
        } else {
            Picasso.with(this.itemView.getContext()).load(article.getLargeImage()).into(image);
        }

        this.itemView.setOnClickListener(v -> SchamperArticleActivity.viewArticle(v.getContext(), article, helper, image));
    }

    private void setDefaultColours() {
        title.setTextColor(initialTitleColour);
        date.setTextColor(initialDateColour);
        author.setTextColor(initialAuthorColour);
        category.setTextColor(initialCategoryColour);
        schamperCardView.setCardBackgroundColor(initialCardViewColour);
    }
}