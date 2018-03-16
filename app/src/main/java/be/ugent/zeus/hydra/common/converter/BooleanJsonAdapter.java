package be.ugent.zeus.hydra.common.converter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import java.io.IOException;

/**
 * Read booleans from a zero (0) or one (1).
 *
 * @author feliciaan
 */
public class BooleanJsonAdapter extends TypeAdapter<Boolean> {

    @Override
    public void write(JsonWriter out, Boolean value) throws IOException {
        out.value(write(value));
    }

    @Override
    public Boolean read(JsonReader in) throws IOException {

        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        return read(in.nextInt());
    }

    @ToJson
    int write(@IntBoolean boolean value) {
        return value ? 1 : 0;
    }

    @FromJson
    @IntBoolean boolean read(int value) {
        return value != 0;
    }
}