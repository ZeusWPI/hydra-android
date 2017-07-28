package be.ugent.zeus.hydra.service.urgent;

import android.os.Binder;

/**
 * A simple binder that gives access to the service.
 *
 * @author Niko Strijbol
 */
public class MusicBinder2 extends Binder {

    private final MusicService2 service;

    public MusicBinder2(MusicService2 service) {
        this.service = service;
    }

    /**
     * @return Get the music service.
     */
    public MusicService2 getService() {
        return service;
    }

    public MediaManager getManager() {
        return service.getMediaManager();
    }
}