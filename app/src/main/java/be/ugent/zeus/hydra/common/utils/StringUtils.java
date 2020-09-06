package be.ugent.zeus.hydra.common.utils;

import androidx.annotation.NonNull;

import java.util.Locale;

/**
 * @author Niko Strijbol
 */
public class StringUtils {

    /**
     * Ensure the first letter is capitalised, while not touching the rest.
     *
     * @param s The string to capitalise.
     * @return Capitalised string.
     */
    public static String capitaliseFirst(@NonNull String s) {
        return s.substring(0, 1).toUpperCase(Locale.getDefault()) + s.substring(1);
    }
}
