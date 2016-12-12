package be.ugent.zeus.hydra.recyclerview.viewholder;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.NewsArticleActivity;
import be.ugent.zeus.hydra.models.association.NewsItem;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.utils.ViewUtils;

import java.util.Locale;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Created by feliciaan on 18/06/16.
 */
public class NewsItemViewHolder extends DataViewHolder<NewsItem> {

    public static final String PARCEL_NAME = "newsItem";

    private TextView info;
    private TextView title;

    public NewsItemViewHolder(View v) {
        super(v);
        title = $(v, R.id.name);
        info = $(v, R.id.info);
    }

    @Override
    public void populate(final NewsItem newsItem) {

        title.setText(newsItem.getTitle());

        String infoText = String.format(new Locale("nl"), "%s door %s",
                DateUtils.relativeDateTimeString(newsItem.getDate(), itemView.getContext()),
                newsItem.getAssociation().getName());
        info.setText(infoText);
        if (newsItem.isHighlighted()) {
            Drawable d = ViewUtils.getTintedVectorDrawable(itemView.getContext(), R.drawable.ic_star, R.color.ugent_yellow_dark);
            title.setCompoundDrawablesWithIntrinsicBounds(null, null, d, null);
        } else {
            title.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        itemView.setOnClickListener(v -> {
            Intent intent = new Intent(itemView.getContext(), NewsArticleActivity.class);
            intent.putExtra(PARCEL_NAME, (Parcelable) newsItem);
            itemView.getContext().startActivity(intent);
        });
    }
}
