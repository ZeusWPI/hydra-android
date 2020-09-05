package be.ugent.zeus.hydra.schamper;

import android.view.View;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.customtabs.ActivityHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.*;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class SchamperViewHolderTest {

    private ActivityHelper helper;

    @Before
    public void setUp() {
        helper = mock(ActivityHelper.class);
    }

    @Test
    public void populate() {
        View view = inflate(R.layout.item_schamper);
        SchamperViewHolder viewHolder = new SchamperViewHolder(view, helper);
        Article article = generate(Article.class, "categoryColour");
        viewHolder.populate(article);

        assertTextIs(article.getTitle(), view.findViewById(R.id.title));
        assertNotEmpty(view.findViewById(R.id.date));
        assertTextIs(article.getAuthor(), view.findViewById(R.id.author));
        // Ignore other fields, maybe one day somebody wants to test them.
        assertTrue(view.hasOnClickListeners());
    }
}
