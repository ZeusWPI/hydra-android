package be.ugent.zeus.hydra.testing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Stream;

import be.ugent.zeus.hydra.minerva.AbstractDaoTest;
import be.ugent.zeus.hydra.minerva.course.Module;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.Randomizer;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.EqualsVerifierApi;
import nl.jqno.equalsverifier.Warning;
import okio.BufferedSource;
import okio.Okio;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.OffsetDateTime;
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
                .randomize(OffsetDateTime.class, (Randomizer<OffsetDateTime>) OffsetDateTime::now)
                .randomize(Instant.class, (Randomizer<Instant>) Instant::now)
                .randomize(EnumSet.class, (Randomizer<EnumSet<Module>>) () -> EnumSet.noneOf(Module.class)) // HACK
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
                .randomize(EnumSet.class, (Randomizer<EnumSet<Module>>) () -> EnumSet.noneOf(Module.class)) // HACK
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
    public static <T> EqualsVerifierApi<T> defaultVerifier(Class<T> clazz) {
        return EqualsVerifier.forClass(clazz)
                .suppress(Warning.NONFINAL_FIELDS)
                .withPrefabValues(ZonedDateTime.class, ZonedDateTime.now(), ZonedDateTime.now().minusDays(2));
    }

    public static <T> T readJson(Moshi moshi, String file, Type type) throws IOException {
        BufferedSource source = Okio.buffer(Okio.source(new FileInputStream(getResourceFile(file))));
        JsonAdapter<T> adapter = moshi.adapter(type);
        return adapter.fromJson(source);
    }

    @SuppressWarnings("ConstantConditions")
    public static File getResourceFile(String resourcePath) {
        return new File(AbstractDaoTest.class.getClassLoader().getResource(resourcePath).getFile());
    }

    private static final Random random = new Random();

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