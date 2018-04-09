package be.ugent.zeus.hydra.feed.cards.implementations.schamper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.View;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.association.news.ArticlePreferenceFragment;
import be.ugent.zeus.hydra.common.ui.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.feed.cards.implementations.AbstractFeedViewHolderTest;
import be.ugent.zeus.hydra.schamper.Article;
import be.ugent.zeus.hydra.schamper.FullArticleActivity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowApplication;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.assertNotEmpty;
import static be.ugent.zeus.hydra.testing.RobolectricUtils.assertTextIs;
import static be.ugent.zeus.hydra.testing.RobolectricUtils.inflate;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class SchamperViewHolderTest extends AbstractFeedViewHolderTest {

    @Test
    public void populate() {
        View view = inflate(R.layout.home_card_schamper);
        SchamperViewHolder schamperViewHolder = new SchamperViewHolder(view, adapter);
        Article article = generate(Article.class, "image", "categoryColour");
        SchamperCard card = new SchamperCard(article);
        schamperViewHolder.populate(card);

        assertTextIs(article.getTitle(), view.findViewById(R.id.title));
        assertNotEmpty(view.findViewById(R.id.date));
        assertTextIs(article.getAuthor(), view.findViewById(R.id.author));

        PreferenceManager.getDefaultSharedPreferences(view.getContext())
                .edit()
                .putBoolean(ArticlePreferenceFragment.PREF_USE_CUSTOM_TABS, true)
                .commit();
        view.performClick();

        verify(helper, times(1)).openCustomTab(any(Uri.class));
    }
}