package be.ugent.zeus.hydra.utils;

import android.support.annotation.Nullable;
import java8.util.function.Consumer;
import java8.util.function.Consumers;

/**
 * @author Niko Strijbol
 */
public class FunctionalUtils {

    /**
     * Return a composed Consumer based on the parameters.
     *
     * @param _this The current consumer, or null if there is none.
     * @param then The consumer to chain, or null to reset.
     * @param <D> The type of the consumers.
     * @return The composed consumer, or null if there should not be a consumer (the reset).
     */
    @Nullable
    public static <D> Consumer<D> maybeAndThen(@Nullable Consumer<D> _this, @Nullable Consumer<D> then) {
        if (then != null && _this != null) {
            //There is an existing listener and we don't want to reset.
            return Consumers.andThen(_this, then);
        } else {
            //We want to reset or there is no listener yet.
            return then;
        }
    }
}