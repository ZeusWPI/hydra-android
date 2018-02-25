package be.ugent.zeus.hydra.testing;

import be.ugent.zeus.hydra.minerva.course.Module;
import com.google.gson.Gson;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.Randomizer;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZonedDateTime;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Stream;

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
                .randomize(OffsetDateTime.class, (Randomizer<OffsetDateTime>) OffsetDateTime::now)
                .randomize(Instant.class, (Randomizer<Instant>) Instant::now)
                .randomize(EnumSet.class, (Randomizer<EnumSet>) () -> EnumSet.noneOf(Module.class)) // HACK
                .build()
                .nextObject(clazz, exclude);
    }

    public static <T> Stream<T> generate(Class<T> clazz, int amount, String... exclude) {
        return EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
                .scanClasspathForConcreteTypes(true)
                .randomize(ZonedDateTime.class, (Randomizer<ZonedDateTime>) ZonedDateTime::now)
                .randomize(LocalDate.class, (Randomizer<LocalDate>) LocalDate::now)
                .randomize(OffsetDateTime.class, (Randomizer<OffsetDateTime>) OffsetDateTime::now)
                .randomize(Instant.class, (Randomizer<Instant>) Instant::now)
                .randomize(EnumSet.class, (Randomizer<EnumSet>) () -> EnumSet.noneOf(Module.class)) // HACK
                .build()
                .objects(clazz, amount, exclude);
    }

    /**
     * Default verifier with support for {@link ZonedDateTime}.
     *
     * @param clazz Class to verify.
     * @param <T>   Type of class.
     *
     * @return The verifier.
     */
    public static <T> EqualsVerifier<T> defaultVerifier(Class<T> clazz) {
        return EqualsVerifier.forClass(clazz)
                .withPrefabValues(ZonedDateTime.class, ZonedDateTime.now(), ZonedDateTime.now().minusDays(2));
    }

    /**
     * Read a JSON file from the resources folder.
     *
     * @param clazz    The class of the file.
     * @param filename The name of the file.
     * @param <T>      The type.
     *
     * @return The file.
     *
     * @throws IOException If the file was not found or not readable.
     */
    public static <T> T readJsonResource(Class<T> clazz, String filename) throws IOException {
        Gson gson = new Gson();
        Resource resource = new ClassPathResource(filename);
        return gson.fromJson(new InputStreamReader(resource.getInputStream()), clazz);
    }

    private static Random random = new Random();

    public static <T> T getRandom(List<T> collection) {
        return collection.get(random.nextInt(collection.size()));
    }

    public static <T> List<T> getRandom(final List<T> collection, int amount) {
        if (amount > collection.size()) {
            throw new IllegalArgumentException("The number of requested items cannot be larger than the number of total items.");
        }

        List<T> copy = new ArrayList<>(collection);
        Collections.shuffle(copy);
        return copy.subList(0, amount);
    }
}