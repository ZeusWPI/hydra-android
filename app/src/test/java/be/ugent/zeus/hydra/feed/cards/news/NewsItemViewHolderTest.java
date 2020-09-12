package be.ugent.zeus.hydra.feed.cards.news;

import android.view.View;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.feed.cards.implementations.AbstractFeedViewHolderTest;
import be.ugent.zeus.hydra.news.UgentNewsArticle;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.assertNotEmpty;
import static be.ugent.zeus.hydra.testing.RobolectricUtils.assertTextIs;
import static be.ugent.zeus.hydra.testing.RobolectricUtils.inflate;
import static be.ugent.zeus.hydra.testing.Utils.generate;

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
    }
}
