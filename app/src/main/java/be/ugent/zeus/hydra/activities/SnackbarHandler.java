package be.ugent.zeus.hydra.activities;

import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;

/**
 * @author Niko Strijbol
 */
public interface SnackbarHandler {

    void showSnackbar(String message, @Snackbar.Duration int length, @Nullable SwipeRefreshLayout.OnRefreshListener listener);

}