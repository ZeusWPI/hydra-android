package be.ugent.zeus.hydra.models;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.Randomizer;
import org.threeten.bp.ZonedDateTime;

/**
 * Utility to create objects.
 *
 * @author Niko Strijbol
 */
public class ObjectCreator {

    public static <T> T generate(Class<T> clazz, String... exclude) {
        return EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
                .randomize(ZonedDateTime.class, (Randomizer<ZonedDateTime>) ZonedDateTime::now)
                .build()
                .nextObject(clazz, exclude);
    }
}