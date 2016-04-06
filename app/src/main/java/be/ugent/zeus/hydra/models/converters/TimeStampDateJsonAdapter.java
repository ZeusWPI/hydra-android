package be.ugent.zeus.hydra.models.converters;

/**
 * Created by feliciaan on 17/02/16.
 */
public class TimeStampDateJsonAdapter extends AbstractDateJsonAdapter {

    public TimeStampDateJsonAdapter() {
        super("yyyy-MM-dd'T'HH:mm:ss");
    }
}
