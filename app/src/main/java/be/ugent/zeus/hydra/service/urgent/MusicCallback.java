package be.ugent.zeus.hydra.service.urgent;

/**
 *
 * @author Niko Strijbol
 */
@Deprecated
public interface MusicCallback {
    void onPermissionRequired(int requestCode, String permission, String rationale);
    void onLoading();
    void onPlaybackStarted();
    void onPlaybackStopped();
    void onError();
}
