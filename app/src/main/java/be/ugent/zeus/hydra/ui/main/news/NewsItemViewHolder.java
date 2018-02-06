package be.ugent.zeus.hydra.ui.main.news;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.association.UgentNewsItem;
import be.ugent.zeus.hydra.common.ui.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.common.ui.html.Utils;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.ui.NewsArticleActivity;
import be.ugent.zeus.hydra.utils.DateUtils;

/**
 * View holder for the news items in the news tab or section.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
class NewsItemViewHolder extends DataViewHolder<UgentNewsItem> {

    private final TextView info;
    private final TextView title;
    private final TextView excerpt;
    private final ActivityHelper helper;

    NewsItemViewHolder(View v, ActivityHelper activityHelper) {
        super(v);
        title = v.findViewById(R.id.name);
        info = v.findViewById(R.id.info);
        excerpt = v.findViewById(R.id.article_excerpt);
        this.helper = activityHelper;
    }

    @Override
    public void populate(final UgentNewsItem newsItem) {

        title.setText(newsItem.getTitle());

        String author = newsItem.getCreators().isEmpty() ? "" : newsItem.getCreators().iterator().next();

        String infoText = itemView.getContext().getString(R.string.agenda_subtitle,
                DateUtils.relativeDateTimeString(newsItem.getCreated(), itemView.getContext()),
                author);
        info.setText(infoText);

        if (!TextUtils.isEmpty(newsItem.getDescription())) {
            excerpt.setText(Utils.fromHtml(newsItem.getDescription()).toString().trim());
        } else {
            excerpt.setText(Utils.fromHtml(newsItem.getText()).toString().trim());
        }

        itemView.setOnClickListener(v -> NewsArticleActivity.viewArticle(v.getContext(), newsItem, helper));
    }
}