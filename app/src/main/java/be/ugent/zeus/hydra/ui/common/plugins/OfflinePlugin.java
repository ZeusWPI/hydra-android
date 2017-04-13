package be.ugent.zeus.hydra.ui.common.plugins;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.network.OfflineBroadcaster;
import be.ugent.zeus.hydra.ui.common.plugins.common.Plugin;

/**
 * @author Niko Strijbol
 */
public class OfflinePlugin extends Plugin {

    private View view;

    private Snackbar snackbar;

    private SwipeRefreshLayout.OnRefreshListener listener;

    public OfflinePlugin(@Nullable SwipeRefreshLayout.OnRefreshListener listener) {
        this.listener = listener;
    }

    public void dismiss() {
        if (snackbar != null) {
            snackbar.dismiss();
            snackbar = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getHost().getContext());
        manager.unregisterReceiver(receiver);
        dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getHost().getContext());
        manager.registerReceiver(receiver, OfflineBroadcaster.getBroadcastFilter());
    }

    @Override
    protected void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
    }

    public void showSnackbar(String message, @Snackbar.Duration int length) {

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

    public void showSnackbar(@StringRes int message, @Snackbar.Duration int length) {
        showSnackbar(getHost().getContext().getString(message), length);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(OfflineBroadcaster.OFFLINE)) {
                //noinspection WrongConstant -> library bug
                showSnackbar(R.string.offline_data_use, Snackbar.LENGTH_INDEFINITE);
            }
        }
    };
}