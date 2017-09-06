package be.ugent.zeus.hydra.data.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.threeten.bp.Instant;

import java.io.IOException;

/**
 * Parses a date to a {@link Instant}.
 *
 * @author Niko Strijbol
 */
public class InstantThreeTenAdapter extends TypeAdapter<Instant> {

    @Override
    public void write(JsonWriter out, Instant date) throws IOException {
        if(date == null) {
            out.nullValue();
            return;
        }

        out.value(date.toString());
    }

    @Override
    public Instant read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        return Instant.parse(in.nextString());
    }
}