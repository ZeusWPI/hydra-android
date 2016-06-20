package be.ugent.zeus.hydra.models.converters;

/**
 * Created by feliciaan on 17/06/16.
 */
public class ISO8601DateJsonAdapter extends AbstractDateJsonAdapter {
    public ISO8601DateJsonAdapter() {
        super("yyyy-MM-dd'T'HH:mm:ssZ");
    }
}
