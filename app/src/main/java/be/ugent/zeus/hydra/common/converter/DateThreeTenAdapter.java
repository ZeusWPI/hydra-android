package be.ugent.zeus.hydra.common.converter;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;
import org.threeten.bp.LocalDate;

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