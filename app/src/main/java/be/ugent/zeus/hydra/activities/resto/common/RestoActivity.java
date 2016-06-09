package be.ugent.zeus.hydra.activities.resto.common;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.LoaderToolbarActivity;

import java.io.Serializable;

/**
 * Abstract class for activities for the various resto feature. This class contains common logic for the action bar and
 * the overflow menu.
 *
 * @param <D> The type of data.
 *
 * @author Niko Strijbol
 */
public abstract class RestoActivity<D extends Serializable> extends LoaderToolbarActivity<D> {

    //The progress bar. Is used if not null.
    protected ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the progress bar.
        progressBar = $(R.id.progress_bar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(getMenuId(), menu);

        // We need to manually set the color of this Drawable for some reason.
        setWhiteIcon(menu, R.id.resto_refresh);

        return true;
    }

    /**
     * @return The ID of the menu to use.
     */
    @MenuRes
    protected abstract int getMenuId();

    /**
     * Add the refresh item.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.resto_refresh) {
            refresh();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is used to receive new data, from the request for example.
     *
     * @param data The new data.
     */
    public void receiveData(@NonNull D data) {
        hideProgressBar();
        this.shouldRenew = false;
    }

    /**
     * Launch a new request.
     */
    private void refresh() {
        Toast.makeText(getApplicationContext(), R.string.begin_refresh, Toast.LENGTH_SHORT).show();
        this.shouldRenew = true;
        restartLoader();
    }

    /**
     * Hide the progress bar.
     */
    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    /**
     * @return The main view of this activity. Currently this is used for snack bars, but that may change.
     */
    protected abstract View getMainView();

    /**
     * When the request has failed.
     */
    public void receiveError(@NonNull Throwable throwable) {
        Snackbar.make(getMainView(), getString(R.string.failure), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.again), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refresh();
                    }
                })
                .show();
        hideProgressBar();
    }

    protected Context getContext() {
        return getApplicationContext();
    }
}