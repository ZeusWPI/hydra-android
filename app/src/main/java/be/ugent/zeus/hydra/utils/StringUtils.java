package be.ugent.zeus.hydra.utils;

import android.support.annotation.NonNull;

import java.io.InputStream;

/**
 * @author Niko Strijbol
 */
public class StringUtils {

    /**
     * Ensure the first letter is capitalised, while not touching the rest.
     *
     * @param s The string to capitalise.
     *
     * @return Capitalised string.
     */
    public static String capitaliseFirst(@NonNull String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }


    /**
     * Convert input stream to string.
     *
     * @param is The input stream.
     * @return The string
     */
    public static String convertStreamToString(@NonNull InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    /**
     * Generate an acronym for a string. This will split the string on whitespace, take the first letter of every word,
     * capitalize that first letter and concat the result. If a word does not contain any letters, it is fully added.
     *
     * For example, {@code Algoritmen en datastructuren III} will become {@code AEDIII}.
     *
     * TODO: can we make this less dumb, and ignore words as 'en', 'of', etc?
     *
     * @param name The string to acronymize.
     *
     * @return The acronym.
     */
    public static String generateAcronymFor(String name) {
        StringBuilder result = new StringBuilder();
        for (String word : name.split("\\s")) {
            int i = 0;
            char c;
            do {
                c = word.charAt(i++);
            } while (!Character.isLetter(c) && i < word.length());
            if (Character.isLetter(c)) {
                result.append(Character.toUpperCase(c));
            } else {
                // There are no letters in this part, so just add it completely.
                result.append(word);
            }
        }
        return result.toString();
    }
}