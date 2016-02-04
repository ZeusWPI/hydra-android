package be.ugent.zeus.hydra.models;

import java.util.Date;

/**
 * Created by feliciaan on 27/01/16.
 */
public class AssociationActivity {
    public String title;
    //public Date start; //TODO: add @-with format
    //public Date end;
    public String location;
    public double latitude;
    public double longitude;
    public String description;
    public String url;
    public String facebook_id;
    public int highlighted; //TODO: make boolean
    public Association association;

    public boolean isHiglighted() {
        return highlighted == 1;
    }
}
