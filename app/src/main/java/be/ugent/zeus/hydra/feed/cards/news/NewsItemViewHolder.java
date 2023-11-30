/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.ugent.zeus.hydra.feed.cards.news;

import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ArticleViewer;
import be.ugent.zeus.hydra.common.utils.DateUtils;
import be.ugent.zeus.hydra.feed.HomeFeedAdapter;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.CardViewHolder;
import be.ugent.zeus.hydra.news.NewsArticle;

/**
 * View holder for the news card in the home feed.
 *
 * @author feliciaan
 * @author Niko Strijbol
 */
public class NewsItemViewHolder extends CardViewHolder {

    private final TextView info;
    private final TextView title;

    public NewsItemViewHolder(View v, HomeFeedAdapter adapter) {
        super(v, adapter);
        title = v.findViewById(R.id.name);
        info = v.findViewById(R.id.info);
    }

    @Override
    public void populate(final Card card) {
        super.populate(card);

        NewsArticle newsItem = card.<NewsItemCard>checkCard(Card.Type.NEWS_ITEM).newsItem();

        title.setText(newsItem.title());
        
        CharSequence dateString;
        if (newsItem.published().toLocalDate().isEqual(newsItem.updated().toLocalDate())) {
            dateString = DateUtils.relativeDateTimeString(newsItem.published(), itemView.getContext());
        } else {
            dateString = itemView.getContext().getString(R.string.article_date_changed,
                    DateUtils.relativeDateTimeString(newsItem.published(), itemView.getContext(), true),
                    DateUtils.relativeDateTimeString(newsItem.updated(), itemView.getContext(), true)
            );
        }
        
        info.setText(dateString);
        title.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        itemView.setOnClickListener(v -> ArticleViewer.viewArticle(v.getContext(), newsItem, adapter.companion().helper()));
    }
}
