package be.ugent.zeus.hydra.models.Association;

/**
 * Created by feliciaan on 04/02/16.
 */
public class AssociationNewsItem {
    public int id;
    public String title;
    public String content;
    public Association association;
    //public Date date; //TODO: fix parsing
    public int highlighted; //TODO: make boolean
}
