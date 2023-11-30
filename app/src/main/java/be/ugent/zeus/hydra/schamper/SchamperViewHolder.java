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

package be.ugent.zeus.hydra.schamper;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.cardview.widget.CardView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ArticleViewer;
import be.ugent.zeus.hydra.common.ui.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.common.utils.ColourUtils;
import be.ugent.zeus.hydra.common.utils.DateUtils;
import be.ugent.zeus.hydra.common.utils.NetworkUtils;
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
    private final CardView schamperCardView;

    private final ActivityHelper helper;

    @ColorInt
    private final int initialTitleColour;
    @ColorInt
    private final int initialDateColour;
    @ColorInt
    private final int initialAuthorColour;
    @ColorInt
    private final int initialCategoryColour;
    @ColorInt
    private final int initialCardViewColour;

    SchamperViewHolder(View itemView, ActivityHelper helper) {
        super(itemView);

        title = itemView.findViewById(R.id.title);
        initialTitleColour = title.getCurrentTextColor();
        date = itemView.findViewById(R.id.date);
        initialDateColour = date.getCurrentTextColor();
        author = itemView.findViewById(R.id.author);
        initialAuthorColour = author.getCurrentTextColor();
        image = itemView.findViewById(R.id.card_image);
        category = itemView.findViewById(R.id.schamper_category);
        initialCategoryColour = category.getCurrentTextColor();
        schamperCardView = itemView.findViewById(R.id.schamper_card_view);
        initialCardViewColour = schamperCardView.getCardBackgroundColor().getDefaultColor();
        this.helper = helper;
    }

    @Override
    public void populate(final Article article) {
        title.setText(article.title());
        date.setText(DateUtils.relativeDateTimeString(article.pubDate(), itemView.getContext()));
        author.setText(article.author());
        category.setText(article.category());

        if (article.hasCategoryColour()) {
            int colour = Color.parseColor(article.categoryColour());
            if (ColourUtils.isDark(colour)) {
                schamperCardView.setCardBackgroundColor(Color.parseColor(article.categoryColour()));
                title.setTextColor(Color.WHITE);
                date.setTextColor(Color.WHITE);
                author.setTextColor(Color.WHITE);
                category.setTextColor(Color.WHITE);
            } else {
                setDefaultColours();
            }
        } else {
            setDefaultColours();
        }

        if (NetworkUtils.isMeteredConnection(itemView.getContext())) {
            Picasso.get().load(article.image()).into(image);
        } else {
            Picasso.get().load(article.largeImage()).into(image);
        }

        this.itemView.setOnClickListener(v -> ArticleViewer.viewArticle(v.getContext(), article, helper));
    }

    private void setDefaultColours() {
        title.setTextColor(initialTitleColour);
        date.setTextColor(initialDateColour);
        author.setTextColor(initialAuthorColour);
        category.setTextColor(initialCategoryColour);
        schamperCardView.setCardBackgroundColor(initialCardViewColour);
    }
}
