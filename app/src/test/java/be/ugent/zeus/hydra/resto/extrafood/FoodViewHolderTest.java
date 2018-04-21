package be.ugent.zeus.hydra.resto.extrafood;

import android.view.View;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.RobolectricTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.assertNotEmpty;
import static be.ugent.zeus.hydra.testing.RobolectricUtils.assertTextIs;
import static be.ugent.zeus.hydra.testing.RobolectricUtils.inflate;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.junit.Assert.*;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class FoodViewHolderTest {

    @Test
    public void populate() {
        View view = inflate(R.layout.item_resto_fooditem);
        Food food = generate(Food.class);
        FoodViewHolder viewHolder = new FoodViewHolder(view);
        viewHolder.populate(food);

        assertTextIs(food.getName(), view.findViewById(R.id.food_item_name));
        assertNotEmpty(view.findViewById(R.id.food_item_price));
    }
}