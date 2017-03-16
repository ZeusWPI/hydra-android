package be.ugent.zeus.hydra.testing;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.Randomizer;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZonedDateTime;

/**
 * General utilities and helper methods for use within the tests.
 *
 * @author Niko Strijbol
 */
public class Utils {

    public static <T> T generate(Class<T> clazz, String... exclude) {
        return EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
                .scanClasspathForConcreteTypes(true)
                .randomize(ZonedDateTime.class, (Randomizer<ZonedDateTime>) ZonedDateTime::now)
                .randomize(LocalDate.class, (Randomizer<LocalDate>) LocalDate::now)
                .build()
                .nextObject(clazz, exclude);
    }

    /**
     * Default verifier with support for {@link ZonedDateTime}.
     *
     * @param clazz Class to verify.
     * @param <T> Type of class.
     * @return The verifier.
     */
    public static <T> EqualsVerifier<T> defaultVerifier(Class<T> clazz) {
        return EqualsVerifier.forClass(clazz)
                .withPrefabValues(ZonedDateTime.class, ZonedDateTime.now(), ZonedDateTime.now().minusDays(2));
    }
}