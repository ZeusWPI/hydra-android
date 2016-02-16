package be.ugent.zeus.hydra.models.Association;

import com.google.gson.annotations.JsonAdapter;

import java.util.Date;

import be.ugent.zeus.hydra.models.converters.BooleanJsonAdapter;
import be.ugent.zeus.hydra.models.converters.DateJsonAdapter;

/**
 * Created by feliciaan on 04/02/16.
 */
public class AssociationNewsItem {
    public int id;
    public String title;
    public String content;
    public Association association;
    @JsonAdapter(DateJsonAdapter.class)
    public Date date;
    @JsonAdapter(BooleanJsonAdapter.class)
    public boolean highlighted;
}
