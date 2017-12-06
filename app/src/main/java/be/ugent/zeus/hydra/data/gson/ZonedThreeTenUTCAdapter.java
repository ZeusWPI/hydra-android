package be.ugent.zeus.hydra.data.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;

import java.io.IOException;

/**
 * Parses a date to a {@link ZonedDateTime}.
 *
 * @author Niko Strijbol
 */
public class ZonedThreeTenUTCAdapter extends TypeAdapter<ZonedDateTime> {

    @Override
    public void write(JsonWriter out, ZonedDateTime date) throws IOException {
        if(date == null) {
            out.nullValue();
            return;
        }

        out.value(date.toString());
    }

    @Override
    public ZonedDateTime read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        return LocalDateTime.parse(in.nextString()).atZone(ZoneOffset.UTC);
    }
}