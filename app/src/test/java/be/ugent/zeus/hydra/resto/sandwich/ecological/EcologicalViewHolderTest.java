package be.ugent.zeus.hydra.resto.sandwich.ecological;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.Collection;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.MultiSelectAdapter;
import net.cachapa.expandablelayout.ExpandableLayout;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.ParameterizedRobolectricTestRunner;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.assertNotEmpty;
import static be.ugent.zeus.hydra.testing.RobolectricUtils.assertTextIs;
import static be.ugent.zeus.hydra.testing.RobolectricUtils.inflate;
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
public class EcologicalViewHolderTest {

    private final boolean expanded;

    public EcologicalViewHolderTest(boolean expanded) {
        this.expanded = expanded;
    }

    @ParameterizedRobolectricTestRunner.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{{true}, {false}});
    }

    @Test
    public void populate() {
        View view = inflate(R.layout.item_sandwich_eco);
        EcologicalSandwich sandwich = generate(EcologicalSandwich.class);
        @SuppressWarnings("unchecked")
        MultiSelectAdapter<EcologicalSandwich> adapter = mock(MultiSelectAdapter.class);
        when(adapter.isChecked(anyInt())).thenReturn(expanded);
        EcologicalViewHolder viewHolder = new EcologicalViewHolder(view, adapter);
        viewHolder.populate(sandwich);

        assertTextIs(sandwich.getName(), view.findViewById(R.id.name));
        assertNotEmpty(view.findViewById(R.id.dates));
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
