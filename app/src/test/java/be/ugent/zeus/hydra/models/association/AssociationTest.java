package be.ugent.zeus.hydra.models.association;

import android.os.Parcel;
import be.ugent.zeus.hydra.models.MockParcel;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

import static be.ugent.zeus.hydra.models.ObjectCreator.generate;
import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.junit.Assert.assertEquals;

/**
 * @author Niko Strijbol
 */
public class AssociationTest {

    @Test
    public void getName() {
        Association full = generate(Association.class);
        assertEquals(full.getFullName(), full.getName());
        Association partial = generate(Association.class, "fullName");
        assertEquals(partial.getDisplayName(), partial.getName());
    }

    @Test
    public void equalsAndHash() {
        EqualsVerifier.forClass(Association.class)
                .withOnlyTheseFields("internalName")
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }

    @Test
    public void parcelable() {
        Association original = generate(Association.class);
        Parcel parcel = MockParcel.writeToParcelable(original);
        Association other = Association.CREATOR.createFromParcel(parcel);
        assertThat(other, sameBeanAs(original));
    }
}