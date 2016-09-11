package be.ugent.zeus.hydra.requests.sko;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.cache.Cache;
import be.ugent.zeus.hydra.models.sko.Exhibitors;

/**
 * Get exhibitors in the Student Village.
 *
 * @author Niko Strijbol
 */
public class StuVilExhibitors extends SkoRequest<Exhibitors> {

    public StuVilExhibitors() {
        super(Exhibitors.class);
    }

    @NonNull
    @Override
    public String getCacheKey() {
        return "student_village_exhibitors.json";
    }

    @Override
    public long getCacheDuration() {
        return Cache.ONE_DAY;
    }
}
