package be.ugent.zeus.hydra.utils;

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
    public static String capitaliseFirst(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }


    /**
     * Convert input stream to string.
     *
     * @param is The input stream.
     * @return The string
     */
    public static String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}