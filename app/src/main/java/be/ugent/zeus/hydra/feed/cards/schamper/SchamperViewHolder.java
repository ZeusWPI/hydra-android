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

package be.ugent.zeus.hydra.feed.cards.schamper;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ArticleViewer;
import be.ugent.zeus.hydra.common.utils.DateUtils;
import be.ugent.zeus.hydra.feed.HomeFeedAdapter;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.CardViewHolder;
import be.ugent.zeus.hydra.feed.cards.PriorityUtils;

/**
 * Home feed view holder for Schamper articles.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public class SchamperViewHolder extends CardViewHolder {

    private final TextView title;
    private final TextView date;
    private final TextView author;
    private final ImageView image;

    public SchamperViewHolder(View v, HomeFeedAdapter adapter) {
        super(v, adapter);
        title = v.findViewById(R.id.title);
        date = v.findViewById(R.id.date);
        author = v.findViewById(R.id.author);
        image = v.findViewById(R.id.image);
    }

    @Override
    public void populate(Card card) {
        super.populate(card);

        var article = card.<SchamperCard>checkCard(Card.Type.SCHAMPER).article;

        title.setText(article.title());

        // Construct coloured text
        Spannable category;
        if (article.hasCategoryColour()) {
            int colour = Color.parseColor(article.categoryColour());
            category = new SpannableString(article.category());
            category.setSpan(new ForegroundColorSpan(colour), 0, category.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            category = new SpannableString(article.category());
        }

        date.setText(TextUtils.concat(DateUtils.relativeDateTimeString(article.pubDate(), itemView.getContext()), " • ", category));
        author.setText(article.author());

        PriorityUtils.loadThumbnail(itemView.getContext(), article.image(), image);

        this.itemView.setOnClickListener(v -> ArticleViewer.viewArticle(v.getContext(), article, adapter.companion().helper()));
    }
}
