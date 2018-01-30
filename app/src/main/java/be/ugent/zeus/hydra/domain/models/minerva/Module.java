package be.ugent.zeus.hydra.domain.models.minerva;

import android.support.annotation.NonNull;

import java.util.EnumSet;
import java.util.Set;

/**
 * Represents the different modules a course can have. By default, a course has all modules enabled.
 *
 * Inexplicably, these are called "tools" in the Minerva API.
 *
 * @see <a href="http://icto.ugent.be/nl/categorie/modules">The user manual for modules</a>
 *
 * @author Niko Strijbol
 */
public enum Module {

    /**
     * The announcements. Is occasionally disabled.
     */
    @SuppressWarnings("PointlessBitwiseExpression") // It is not useless.
    ANNOUNCEMENTS(1 << 0),
    /**
     * The calendar. There are no known courses for which this is disabled, but we can never know.
     */
    CALENDAR(1 << 1);

    private final int numericValue;

    Module(int numericValue) {
        this.numericValue = numericValue;
    }

    /**
     * Convert the set of enums to a numerical value. There are no guarantees about the returned result, except that
     * it will yield a correct result when used with {@link #fromNumericalValue(int)}.
     *
     * @param enums The enums. Can be empty. Must not be null.
     *
     * @return The numerical values.
     */
    public static int toNumericalValue(@NonNull Set<Module> enums) {
        int i = 0;
        for (Module module: enums) {
            i |= module.numericValue;
        }
        return i;
    }

    /**
     * Convert a numerical value to a set of enums.
     *
     * @param numericValue The numerical value, as returned by {@link #toNumericalValue(Set)}.
     *
     * @return A set containing the enums.
     */
    @NonNull
    public static EnumSet<Module> fromNumericalValue(int numericValue) {
        EnumSet<Module> set = EnumSet.noneOf(Module.class);
        for (Module module: values()) {
            if ((numericValue & module.numericValue) == module.numericValue) {
                set.add(module);
            }
        }
        return set;
    }
}