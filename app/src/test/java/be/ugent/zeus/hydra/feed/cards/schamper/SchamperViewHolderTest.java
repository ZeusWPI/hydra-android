package be.ugent.zeus.hydra.feed.cards.schamper;

import android.net.Uri;
import android.view.View;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.feed.cards.implementations.AbstractFeedViewHolderTest;
import be.ugent.zeus.hydra.schamper.Article;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.assertNotEmpty;
import static be.ugent.zeus.hydra.testing.RobolectricUtils.assertTextIs;
import static be.ugent.zeus.hydra.testing.RobolectricUtils.inflate;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class SchamperViewHolderTest extends AbstractFeedViewHolderTest {

    @Test
    public void populate() {
        View view = inflate(activityContext, R.layout.home_card_schamper);

        SchamperViewHolder schamperViewHolder = new SchamperViewHolder(view, adapter);
        Article article = generate(Article.class, "image", "categoryColour");
        SchamperCard card = new SchamperCard(article);
        schamperViewHolder.populate(card);

        assertTextIs(article.getTitle(), view.findViewById(R.id.title));
        assertNotEmpty(view.findViewById(R.id.date));
        assertTextIs(article.getAuthor(), view.findViewById(R.id.author));

        view.performClick();
        verify(helper, times(1)).openCustomTab(any(Uri.class));
    }
}
