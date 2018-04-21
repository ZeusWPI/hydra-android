package be.ugent.zeus.hydra.resto.sandwich;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.MultiSelectAdapter;
import net.cachapa.expandablelayout.ExpandableLayout;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.ParameterizedRobolectricTestRunner;

import java.util.Arrays;
import java.util.Collection;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.*;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Niko Strijbol
 */
@RunWith(ParameterizedRobolectricTestRunner.class)
public class SandwichHolderTest {

    private final boolean expanded;

    public SandwichHolderTest(boolean expanded) {
        this.expanded = expanded;
    }

    @ParameterizedRobolectricTestRunner.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{{true}, {false}});
    }

    @Test
    public void populate() {
        View view = inflate(R.layout.item_sandwich);
        Sandwich sandwich = generate(Sandwich.class);
        @SuppressWarnings("unchecked")
        MultiSelectAdapter<Sandwich> adapter = mock(MultiSelectAdapter.class);
        when(adapter.isChecked(anyInt())).thenReturn(expanded);
        SandwichHolder viewHolder = new SandwichHolder(view, adapter);
        viewHolder.populate(sandwich);

        assertTextIs(sandwich.getName(), view.findViewById(R.id.sandwich_name));
        assertNotEmpty(view.findViewById(R.id.sandwich_price_medium));
        assertNotEmpty(view.findViewById(R.id.sandwich_price_small));
        assertNotEmpty(view.findViewById(R.id.sandwich_ingredients));

        ExpandableLayout layout = view.findViewById(R.id.expandable_layout);
        assertEquals(expanded, layout.isExpanded());

        ArgumentCaptor<Integer> positionCaptor = ArgumentCaptor.forClass(Integer.class);
        view.performClick();
        verify(adapter).setChecked(positionCaptor.capture());
        assertEquals(RecyclerView.NO_POSITION, (int) positionCaptor.getValue());

        assertEquals(!expanded, layout.isExpanded());
    }

}