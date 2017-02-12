package be.ugent.zeus.hydra.minerva.database;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import java8.util.stream.RefStreams;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;

import java.util.Arrays;
import java.util.Collection;

/**
 * Database utilities.
 *
 * @author Niko Strijbol
 */
public class Utils {

    /**
     * Get a string containing {@code n} question marks (?) separated by comma's (,).
     *
     * @param n The amount of question marks.
     *
     * @return The string.
     */
    public static String commaSeparatedQuestionMarks(int n) {
        String[] array = new String[n];
        Arrays.fill(array, "?");
        return TextUtils.join(", ", array);
    }

    /**
     * Convert a boolean to an int.
     *
     * @param bool The boolean.
     *
     * @return 1 if true, 0 otherwise.
     */
    public static int boolToInt(boolean bool) {
        return bool ? 1 : 0;
    }

    public static boolean intToBool(int anInt) {
        return anInt == 1;
    }

    /**
     * Utility method to generate a where statement, with one parameter, for a given column.
     *
     * @param columnName The column.
     * @return The where statement.
     */
    public static String where(@NonNull String columnName) {
        return columnName + " = ?";
    }

    /**
     * Convert a stream of things to a String[].
     *
     * @param arguments The things.
     * @return The string array.
     */
    public static String[] args(Stream<?> arguments) {
        return arguments.map(Object::toString)
                .toArray(String[]::new);
    }

    /**
     * Convert a stream of things to a String[].
     *
     * @param arguments The things.
     * @return The string array.
     */
    public static String[] args(Object... arguments) {
        return args(RefStreams.of(arguments));
    }

    /**
     * Convert a stream of things to a String[].
     *
     * @param arguments The things.
     * @return The string array.
     */
    public static String[] args(Collection<?> arguments) {
        return args(StreamSupport.stream(arguments));
    }

    public static String in(@NonNull String columnName, int nrOfArguments) {
        return columnName + " IN (" + commaSeparatedQuestionMarks(nrOfArguments) + ")";
    }
}