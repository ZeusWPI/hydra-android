package be.ugent.zeus.hydra.models.converters;

import android.support.annotation.Nullable;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.IOException;

/**
 * @author Niko Strijbol
 */
public class DateThreeTenAdapter extends TypeAdapter<LocalDate> {

    @Override
    public void write(JsonWriter out, LocalDate date) throws IOException {
        if(date == null) {
            out.nullValue();
            return;
        }

        if(getFormatter() == null) {
            out.value(date.toString());
        } else {
            out.value(date.format(getFormatter()));
        }
    }

    @Override
    public LocalDate read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        if(getFormatter() == null) {
            return LocalDate.parse(in.nextString());
        } else {
            return LocalDate.parse(in.nextString(), getFormatter());
        }
    }

    /**
     * Provide the formatter to use to parse and write the date.
     *
     * If null is returned, the {@link org.threeten.bp.format.DateTimeFormatter#ISO_ZONED_DATE_TIME} format will
     * be used.
     *
     * @see ZonedDateTime#parse(CharSequence, DateTimeFormatter)
     *
     * @return The formatter to use.
     */
    @Nullable
    protected DateTimeFormatter getFormatter() {
        return null;
    }
}

