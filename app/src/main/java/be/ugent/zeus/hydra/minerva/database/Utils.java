package be.ugent.zeus.hydra.minerva.database;

import android.text.TextUtils;

import java.util.Arrays;

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
}