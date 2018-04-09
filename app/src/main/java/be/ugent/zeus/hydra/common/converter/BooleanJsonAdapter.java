package be.ugent.zeus.hydra.common.converter;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

/**
 * Read booleans from a zero (0) or one (1). Integers that are not 0 or 1 will result in undefined behaviour.
 *
 * @author Niko Strijbol
 */
public class BooleanJsonAdapter {

    @ToJson
    int write(@IntBoolean boolean value) {
        return value ? 1 : 0;
    }

    @FromJson
    @IntBoolean
    boolean read(int value) {
        return value != 0;
    }
}