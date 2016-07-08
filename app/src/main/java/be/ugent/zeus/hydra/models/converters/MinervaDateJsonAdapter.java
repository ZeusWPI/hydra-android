package be.ugent.zeus.hydra.models.converters;

/**
 * Created by feliciaan on 03/07/16.
 */
public class MinervaDateJsonAdapter extends AbstractDateJsonAdapter {
    public MinervaDateJsonAdapter() {
        super("yyyy-MM-dd HH:mm:ss.SSSSSS");
    }
}
