package be.ugent.zeus.hydra.ui.main.schamper;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.schamper.Article;
import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.ui.common.customtabs.ActivityHelper;
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

    private final ActivityHelper helper;

    SchamperViewHolder(View itemView, ActivityHelper helper) {
        super(itemView);

        title = itemView.findViewById(R.id.title);
        date = itemView.findViewById(R.id.date);
        author = itemView.findViewById(R.id.author);
        image = itemView.findViewById(R.id.card_image);
        category = itemView.findViewById(R.id.schamper_category);
        this.helper = helper;
    }

    public void populate(final Article article) {
        title.setText(article.getTitle());
        date.setText(DateUtils.relativeDateTimeString(article.getPubDate(), itemView.getContext()));
        author.setText(article.getAuthor());
        category.setText(article.getCategory());

        if (NetworkUtils.isMeteredConnection(itemView.getContext())) {
            Picasso.with(this.itemView.getContext()).load(article.getImage()).into(image);
        } else {
            Picasso.with(this.itemView.getContext()).load(article.getLargeImage()).into(image);
        }

        this.itemView.setOnClickListener(v -> helper.openCustomTab(Uri.parse(article.getLink())));
    }
}