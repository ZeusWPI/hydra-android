package be.ugent.zeus.hydra.feed.cards.implementations.news;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.view.View;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.association.news.NewsArticleActivity;
import be.ugent.zeus.hydra.association.news.UgentNewsArticle;
import be.ugent.zeus.hydra.preferences.ArticleFragment;
import be.ugent.zeus.hydra.feed.cards.implementations.AbstractFeedViewHolderTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.*;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.junit.Assert.assertEquals;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class NewsItemViewHolderTest extends AbstractFeedViewHolderTest {

    @Test
    public void populate() {
        View view = inflate(activityContext, R.layout.home_card_news_item);
        NewsItemViewHolder viewHolder = new NewsItemViewHolder(view, adapter);
        NewsItemCard card = generate(NewsItemCard.class);
        UgentNewsArticle item = card.getNewsItem();
        viewHolder.populate(card);

        assertTextIs(item.getTitle(), view.findViewById(R.id.name));
        assertNotEmpty(view.findViewById(R.id.info));

        // No custom tabs, we check this elsewhere.
        PreferenceManager.getDefaultSharedPreferences(view.getContext())
                .edit()
                .putBoolean(ArticleFragment.PREF_USE_CUSTOM_TABS, false)
                .commit();
        view.performClick();

        Intent expected = new Intent(view.getContext(), NewsArticleActivity.class);
        Intent actual = getShadowApplication().getNextStartedActivity();

        assertEquals(expected.getComponent(), actual.getComponent());
        assertEquals(item, actual.getParcelableExtra(NewsArticleActivity.PARCEL_NAME));
    }
}