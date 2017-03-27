package be.ugent.zeus.hydra.ui.main.homefeed.content;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Niko Strijbol
 */
public class FeedUtilsTest {

    @Test
    public void lerp() throws Exception {

        //These tests only work for FEED_MAX_VALUE = 100;
        assertEquals(1000 + FeedUtils.FEED_SPECIAL_SHIFT, FeedUtils.FEED_MAX_VALUE);

        assertEquals(FeedUtils.FEED_MAX_VALUE + FeedUtils.FEED_SPECIAL_SHIFT, FeedUtils.lerp(200, 0, 100));
        assertEquals(-10 + FeedUtils.FEED_SPECIAL_SHIFT, FeedUtils.lerp(-1, 0, 100));
    }

}