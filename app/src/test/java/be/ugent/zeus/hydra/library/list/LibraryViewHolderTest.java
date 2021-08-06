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

package be.ugent.zeus.hydra.library.list;

import android.content.Intent;
import android.util.Pair;
import android.view.View;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.library.Library;
import be.ugent.zeus.hydra.library.details.LibraryDetailActivity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.assertTextIs;
import static be.ugent.zeus.hydra.testing.RobolectricUtils.getShadowApplication;
import static be.ugent.zeus.hydra.testing.RobolectricUtils.inflate;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class LibraryViewHolderTest {

    @Test
    public void populate() {
        View view = inflate(R.layout.item_library);
        Library library = generate(Library.class);
        LibraryListAdapter adapter = mock(LibraryListAdapter.class);
        LibraryViewHolder viewHolder = new LibraryViewHolder(view, adapter);
        viewHolder.populate(Pair.create(library, true));

        assertTextIs(library.getName(), view.findViewById(R.id.title));
        assertTextIs(library.getCampus(), view.findViewById(R.id.subtitle));

        view.performClick();

        Intent expected = new Intent(view.getContext(), LibraryDetailActivity.class);
        Intent actual = getShadowApplication().getNextStartedActivity();

        assertEquals(expected.getComponent(), actual.getComponent());
        assertEquals(library, actual.getParcelableExtra(LibraryDetailActivity.ARG_LIBRARY));
    }

    @Test
    public void populateFavourite() {
        View view = inflate(R.layout.item_library);
        Library library = generate(Library.class);
        LibraryListAdapter adapter = mock(LibraryListAdapter.class);
        LibraryViewHolder viewHolder = new LibraryViewHolder(view, adapter);

        viewHolder.populate(Pair.create(library, true));
        assertEquals(view.findViewById(R.id.library_favourite_image).getVisibility(), View.VISIBLE);

        viewHolder.populate(Pair.create(library, false));
        assertEquals(view.findViewById(R.id.library_favourite_image).getVisibility(), View.GONE);
    }
}