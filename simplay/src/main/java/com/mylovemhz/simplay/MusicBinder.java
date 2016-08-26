package com.mylovemhz.simplay;

import android.os.Binder;

/**
 * A simple binder that gives access to the service.
 *
 * TODO: maybe route all communication to the service through this binder?
 *
 * @author Niko Strijbol
 */
public class MusicBinder extends Binder {

    private final MusicService service;

    public MusicBinder(MusicService service) {
        this.service = service;
    }

    /**
     * @return Get the music service.
     */
    public MusicService getService() {
        return service;
    }
}