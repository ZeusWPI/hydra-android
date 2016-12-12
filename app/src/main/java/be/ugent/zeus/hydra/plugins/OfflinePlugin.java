package be.ugent.zeus.hydra.plugins;

import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.SnackbarHandler;
import be.ugent.zeus.hydra.plugins.common.Plugin;

/**
 * @author Niko Strijbol
 */
public class OfflinePlugin extends Plugin implements SnackbarHandler {

    private View view;

    private Snackbar snackbar;

    public void dismiss() {
        if (snackbar != null) {
            snackbar.dismiss();
            snackbar = null;
        }
    }

    public void setView(View view) {
        this.view = view;
    }

    public void showSnackbar(String message, @Snackbar.Duration int length, @Nullable SwipeRefreshLayout.OnRefreshListener listener) {

        if (snackbar == null) {
            snackbar = Snackbar.make(view, message, length);
            if (listener != null) {
                snackbar.setAction(R.string.action_refresh, v -> listener.onRefresh());
            }
        } else {
            snackbar.setText(message);
            snackbar.setDuration(length);
        }

        snackbar.show();
    }
}