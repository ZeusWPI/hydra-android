package be.ugent.zeus.hydra.models.library;

import android.os.Parcel;
import be.ugent.zeus.hydra.models.MockParcel;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;
import org.threeten.bp.ZonedDateTime;

import static be.ugent.zeus.hydra.models.ObjectCreator.generate;
import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;

/**
 * @author Niko Strijbol
 */
public class LibraryTest {

    @Test
    public void equalsAndHash() {
        EqualsVerifier.forClass(Library.class)
                .withOnlyTheseFields("code")
                .suppress(Warning.NONFINAL_FIELDS)
                .withPrefabValues(ZonedDateTime.class, ZonedDateTime.now(), ZonedDateTime.now().minusDays(2))
                .verify();
    }

    @Test
    public void parcelable() {
        Library original = generate(Library.class);
        Parcel parcel = MockParcel.writeToParcelable(original);
        Library other = Library.CREATOR.createFromParcel(parcel);
        assertThat(other, sameBeanAs(original));
    }
}