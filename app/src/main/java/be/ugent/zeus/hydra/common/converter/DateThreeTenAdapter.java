package be.ugent.zeus.hydra.common.converter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;
import org.threeten.bp.LocalDate;

import java.io.IOException;

/**
 * Adapter for {@link LocalDate}s.
 *
 * @author Niko Strijbol
 */
public class DateThreeTenAdapter extends TypeAdapter<LocalDate> {

    @Override
    public void write(JsonWriter out, LocalDate date) throws IOException {
        if(date == null) {
            out.nullValue();
            return;
        }

        out.value(write(date));
    }

    @Override
    public LocalDate read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        return read(in.nextString());
    }

    @FromJson
    LocalDate read(String value) {
        return LocalDate.parse(value);
    }

    @ToJson
    String write(LocalDate value) {
        return value.toString();
    }
}