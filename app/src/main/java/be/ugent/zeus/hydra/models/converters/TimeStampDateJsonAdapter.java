package be.ugent.zeus.hydra.models.converters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import org.apache.commons.lang3.NotImplementedException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by feliciaan on 17/02/16.
 */
public class TimeStampDateJsonAdapter extends AbstractDateJsonAdapter {

    public TimeStampDateJsonAdapter() {
        super("yyyy-MM-dd'T'HH:mm:ss");
    }
}
