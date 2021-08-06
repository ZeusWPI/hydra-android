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

package be.ugent.zeus.hydra.association.preference;

import android.util.Pair;
import android.view.View;
import android.widget.CheckBox;
import androidx.recyclerview.widget.RecyclerView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.association.Association;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.assertTextIs;
import static be.ugent.zeus.hydra.testing.RobolectricUtils.inflate;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class AssociationViewHolderTest {

    private SearchableAssociationsAdapter associationsAdapter;

    @Before
    public void setUp() {
        associationsAdapter = mock(SearchableAssociationsAdapter.class);
    }

    @Test
    public void populateChecked() {
        View view = inflate(R.layout.item_checkbox_string);
        Association association = generate(Association.class);
        Pair<Association, Boolean> data = new Pair<>(association, true);
        AssociationViewHolder viewHolder = new AssociationViewHolder(view, associationsAdapter);
        viewHolder.populate(data);

        CheckBox checkBox = view.findViewById(R.id.checkbox);

        assertTextIs(association.getName(), view.findViewById(R.id.title_checkbox));
        assertTrue(checkBox.isChecked());

        view.performClick();
        assertFalse(checkBox.isChecked());

        verify(associationsAdapter, times(1)).setChecked(RecyclerView.NO_POSITION);
    }
}