package be.ugent.zeus.hydra.utils;

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
}