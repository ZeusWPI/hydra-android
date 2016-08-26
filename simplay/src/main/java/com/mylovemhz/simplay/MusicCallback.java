package com.mylovemhz.simplay;

/**
 *
 * @author Niko Strijbol
 */
public interface MusicCallback {
    void onPermissionRequired(int requestCode, String permission, String rationale);
    void onLoading();
    void onPlaybackStarted();
    void onPlaybackStopped();
    void onError();
}
