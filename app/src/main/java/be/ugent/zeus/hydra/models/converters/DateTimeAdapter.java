package be.ugent.zeus.hydra.models.converters;

/**
 * @author Niko Strijbol
 * @version 1/06/2016
 */

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;

/**
 * GSON serialiser/deserialiser for converting Joda {@link DateTime} objects.
 */
public class DateTimeAdapter extends TypeAdapter<DateTime> {

    private static final DateTimeFormatter fmt = ISODateTimeFormat.dateTime();

    /**
     * Writes one JSON value (an array, object, string, number, boolean or null) for {@code value}.
     *
     * @param out
     * @param value the Java object to write. May be null.
     */
    @Override
    public void write(JsonWriter out, DateTime value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }

        out.value(fmt.print(value));
    }

    /**
     * Reads one JSON value (an array, object, string, number, boolean or null) and converts it to a Java object.
     * Returns the converted object.
     *
     * @param in
     *
     * @return the converted Java object. May be null.
     */
    @Override
    public DateTime read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        return fmt.parseDateTime(in.nextString());
    }
}