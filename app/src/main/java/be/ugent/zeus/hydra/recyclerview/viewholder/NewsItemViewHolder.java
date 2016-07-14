package be.ugent.zeus.hydra.recyclerview.viewholder;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.NewsArticleActivity;
import be.ugent.zeus.hydra.models.association.NewsItem;
import be.ugent.zeus.hydra.utils.DateUtils;

import java.util.Locale;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Created by feliciaan on 18/06/16.
 */
public class NewsItemViewHolder extends RecyclerView.ViewHolder implements DataViewHolder<NewsItem> {

    public static final String PARCEL_NAME = "newsItem";

    private TextView info;
    private TextView title;

    public NewsItemViewHolder(View v) {
        super(v);
        title = $(v, R.id.name);
        info = $(v, R.id.info);
    }

    public void populateData(final NewsItem newsItem) {

        title.setText(newsItem.getTitle());

        String infoText = String.format(new Locale("nl"), "%s door %s",
                DateUtils.relativeDateString(newsItem.getDate(), itemView.getContext()),
                newsItem.getAssociation().getName());
        info.setText(infoText);
        if (newsItem.isHighlighted()) {
            title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.star, 0);
        } else {
            title.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(itemView.getContext(), NewsArticleActivity.class);
                intent.putExtra(PARCEL_NAME, (Parcelable) newsItem);
                itemView.getContext().startActivity(intent);
            }
        });
    }
}
