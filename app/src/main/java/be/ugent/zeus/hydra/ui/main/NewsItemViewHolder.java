package be.ugent.zeus.hydra.ui.main;

import android.content.Intent;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.association.UgentNewsItem;
import be.ugent.zeus.hydra.ui.NewsArticleActivity;
import be.ugent.zeus.hydra.ui.common.recyclerview.DataViewHolder;
import be.ugent.zeus.hydra.utils.DateUtils;

import static be.ugent.zeus.hydra.ui.NewsArticleActivity.PARCEL_NAME;
import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Created by feliciaan on 18/06/16.
 */
public class NewsItemViewHolder extends DataViewHolder<UgentNewsItem> {

    private TextView info;
    private TextView title;

    public NewsItemViewHolder(View v) {
        super(v);
        title = $(v, R.id.name);
        info = $(v, R.id.info);
    }

    @Override
    public void populate(final UgentNewsItem newsItem) {

        title.setText(newsItem.getTitle());

        String author = newsItem.getCreators().isEmpty() ? "" : newsItem.getCreators().iterator().next();

        String infoText = itemView.getContext().getString(R.string.agenda_subtitle,
                DateUtils.relativeDateTimeString(newsItem.getCreated(), itemView.getContext()),
                author);
        info.setText(infoText);
        title.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), NewsArticleActivity.class);
            intent.putExtra(PARCEL_NAME, (Parcelable) newsItem);
            v.getContext().startActivity(intent);
        });
    }
}