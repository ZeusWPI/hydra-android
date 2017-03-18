package be.ugent.zeus.hydra.ui.main;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.association.NewsItem;
import be.ugent.zeus.hydra.ui.NewsArticleActivity;
import be.ugent.zeus.hydra.ui.common.recyclerview.DataViewHolder;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.utils.ViewUtils;

import static be.ugent.zeus.hydra.ui.NewsArticleActivity.PARCEL_NAME;
import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Created by feliciaan on 18/06/16.
 */
public class NewsItemViewHolder extends DataViewHolder<NewsItem> {

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

        String infoText = itemView.getContext().getString(R.string.agenda_subtitle,
                DateUtils.relativeDateTimeString(newsItem.getDate(), itemView.getContext()),
                newsItem.getAssociation().name());
        info.setText(infoText);
        if (newsItem.isHighlighted()) {
            Drawable d = ViewUtils.getTintedVectorDrawable(itemView.getContext(), R.drawable.ic_star, R.color.ugent_yellow_dark);
            title.setCompoundDrawablesWithIntrinsicBounds(null, null, d, null);
        } else {
            title.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), NewsArticleActivity.class);
            intent.putExtra(PARCEL_NAME, (Parcelable) newsItem);
            v.getContext().startActivity(intent);
        });
    }
}