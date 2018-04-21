package be.ugent.zeus.hydra.feed.cards;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Niko Strijbol
 */
public class PriorityUtilsTest {

    @Test
    public void lerp() {

        //These tests only work for FEED_MAX_VALUE = 100;
        assertEquals(1000 + PriorityUtils.FEED_SPECIAL_SHIFT, PriorityUtils.FEED_MAX_VALUE);

        assertEquals(PriorityUtils.FEED_MAX_VALUE + PriorityUtils.FEED_SPECIAL_SHIFT, PriorityUtils.lerp(200, 0, 100));
        assertEquals(-10 + PriorityUtils.FEED_SPECIAL_SHIFT, PriorityUtils.lerp(-1, 0, 100));
    }

}