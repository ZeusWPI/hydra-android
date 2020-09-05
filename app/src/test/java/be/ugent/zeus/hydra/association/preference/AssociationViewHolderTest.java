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