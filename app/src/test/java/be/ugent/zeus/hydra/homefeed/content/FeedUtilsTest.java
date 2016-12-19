package be.ugent.zeus.hydra.homefeed.content;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Niko Strijbol
 */
public class FeedUtilsTest {

    @Test
    public void lerp() throws Exception {

        //These tests only work for FEED_MAX_VALUE = 100;
        assertEquals(1000, FeedUtils.FEED_MAX_VALUE);

        for(int i = 0; i <= 100; i++) {
            assertEquals(i * 10, FeedUtils.lerp(i, 0, 100));
        }

        assertEquals(FeedUtils.FEED_MAX_VALUE, FeedUtils.lerp(200, 0, 100));
        assertEquals(-10, FeedUtils.lerp(-1, 0, 100));
    }

}