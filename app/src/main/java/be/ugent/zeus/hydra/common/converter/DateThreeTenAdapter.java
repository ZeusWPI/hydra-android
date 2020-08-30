package be.ugent.zeus.hydra.common.converter;

import java.time.LocalDate;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

/**
 * Adapter for {@link LocalDate}s.
 *
 * @author Niko Strijbol
 */
public class DateThreeTenAdapter {

    @FromJson
    LocalDate read(String value) {
        return LocalDate.parse(value);
    }

    @ToJson
    String write(LocalDate value) {
        return value.toString();
    }
}