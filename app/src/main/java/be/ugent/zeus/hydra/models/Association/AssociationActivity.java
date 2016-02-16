package be.ugent.zeus.hydra.models.Association;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import org.apache.commons.lang3.NotImplementedException;

import be.ugent.zeus.hydra.models.converters.BooleanJsonAdapter;
import be.ugent.zeus.hydra.models.converters.DateJsonAdapter;

/**
 * Created by feliciaan on 27/01/16.
 */
public class AssociationActivity {
    public String title;

    @JsonAdapter(DateJsonAdapter.class)
    public Date start;
    @JsonAdapter(DateJsonAdapter.class)
    public Date end;
    public String location;
    public double latitude;
    public double longitude;
    public String description;
    public String url;
    public String facebook_id;
    @JsonAdapter(BooleanJsonAdapter.class)
    public boolean highlighted;
    public Association association;
}