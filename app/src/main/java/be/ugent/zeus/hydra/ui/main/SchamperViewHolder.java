package be.ugent.zeus.hydra.ui.main;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.schamper.Article;
import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.ui.common.customtabs.ActivityHelper;
import com.squareup.picasso.Picasso;

import static be.ugent.zeus.hydra.ui.common.ViewUtils.$;

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

    private final ActivityHelper helper;

    SchamperViewHolder(View itemView, ActivityHelper helper) {
        super(itemView);

        title = $(itemView, R.id.title);
        date = $(itemView, R.id.date);
        author = $(itemView, R.id.author);
        image = $(itemView, R.id.card_image);
        category = $(itemView, R.id.schamper_category);
        this.helper = helper;
    }

    public void populate(final Article article) {
        title.setText(article.getTitle());
        date.setText(DateUtils.relativeDateTimeString(article.getPubDate(), itemView.getContext()));
        author.setText(article.getAuthor());
        category.setText(article.getCategory());
        Picasso.with(this.itemView.getContext()).load(article.getImage()).into(image);
        this.itemView.setOnClickListener(v -> helper.openCustomTab(Uri.parse(article.getLink())));
    }
}