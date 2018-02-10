package be.ugent.zeus.hydra.common.converter;

import be.ugent.zeus.hydra.utils.TtbUtils;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.threeten.bp.ZonedDateTime;

import java.io.IOException;

/**
 * Parses a date to a {@link ZonedDateTime}.
 *
 * @author Niko Strijbol
 */
public class ZonedThreeTenTimeStampAdapter extends TypeAdapter<ZonedDateTime> {

    @Override
    public void write(JsonWriter out, ZonedDateTime date) throws IOException {
        out.value(TtbUtils.serialize(date));
    }

    @Override
    public ZonedDateTime read(JsonReader in) throws IOException {
        return TtbUtils.unserialize(in.nextLong());
    }
}