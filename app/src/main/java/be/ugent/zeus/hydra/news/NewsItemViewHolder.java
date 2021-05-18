package be.ugent.zeus.hydra.news;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ArticleViewer;
import be.ugent.zeus.hydra.common.ui.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.common.ui.html.Utils;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.common.utils.DateUtils;

/**
 * View holder for the news items in the news tab or section.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
class NewsItemViewHolder extends DataViewHolder<NewsArticle> {

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
    public void populate(final NewsArticle newsItem) {
        title.setText(newsItem.getTitle());

        CharSequence dateString;
        if (newsItem.getPublished().toLocalDate().isEqual(newsItem.getUpdated().toLocalDate())) {
            dateString = DateUtils.relativeDateTimeString(newsItem.getPublished(), itemView.getContext());
        } else {
            dateString = itemView.getContext().getString(R.string.article_date_changed,
                    DateUtils.relativeDateTimeString(newsItem.getPublished(), itemView.getContext(), true),
                    DateUtils.relativeDateTimeString(newsItem.getUpdated(), itemView.getContext(), true)
            );
        }

        info.setText(dateString);

        if (TextUtils.isEmpty(newsItem.getSummary())) {
            excerpt.setText(Utils.fromHtml(newsItem.getContent()).toString().trim());
        } else {
            excerpt.setText(newsItem.getSummary());
        }

        itemView.setOnClickListener(v -> ArticleViewer.viewArticle(v.getContext(), newsItem, helper));
    }
}
