/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.ugent.zeus.hydra.resto.sandwich.regular;

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
public class RegularViewHolderTest {

    private final boolean expanded;

    public RegularViewHolderTest(boolean expanded) {
        this.expanded = expanded;
    }

    @ParameterizedRobolectricTestRunner.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{{true}, {false}});
    }

    @Test
    public void populate() {
        View view = inflate(R.layout.item_sandwich);
        RegularSandwich sandwich = generate(RegularSandwich.class);
        @SuppressWarnings("unchecked")
        MultiSelectAdapter<RegularSandwich> adapter = mock(MultiSelectAdapter.class);
        when(adapter.isChecked(anyInt())).thenReturn(expanded);
        RegularHolder viewHolder = new RegularHolder(view, adapter);
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
