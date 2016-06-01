package be.ugent.zeus.hydra.models.converters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by feliciaan on 17/02/16.
 */
public class AbstractDateJsonAdapter extends TypeAdapter<Date> {

    private DateFormat format;

    public AbstractDateJsonAdapter(String dateFormat) {
        format = new SimpleDateFormat(dateFormat);
    }

    @Override
    public void write(JsonWriter out, Date value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }

        out.value(format.format(value));
    }

    @Override
    public Date read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        String dateString = in.nextString();
        Date date = null;
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            System.err.println("Parsing failed!");
        }
        return date;
    }
}
