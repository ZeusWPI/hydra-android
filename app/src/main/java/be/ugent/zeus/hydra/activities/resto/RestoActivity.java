package be.ugent.zeus.hydra.activities.resto;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.MenuRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.RequestHandler;
import be.ugent.zeus.hydra.common.activities.SpiceToolbarActivity;
import be.ugent.zeus.hydra.requests.AbstractRequest;

import java.util.ArrayList;

/**
 * Abstract class for activities for the various resto feature. This class contains common logic for the action bar and
 * the overflow menu.
 *
 * @param <D> The type of data.
 *
 * @author Niko Strijbol
 */
public abstract class RestoActivity<D extends Parcelable> extends SpiceToolbarActivity implements RequestHandler.Requester<ArrayList<D>> {

    //The progress bar. Is used if not null.
    protected ProgressBar progressBar;

    protected ArrayList<D> data = new ArrayList<>();

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

    protected void initFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            data = savedInstanceState.getParcelableArrayList(getDataName());
            receiveData(data);
            hideProgressBar();
        } else {
            // Probably initialize members with default values for a new instance
            performRequest(false);
        }
    }

    /**
     * This method is used to receive new data, from the request for example.
     *
     * @param data The new data.
     */
    public void receiveData(ArrayList<D> data) {
        hideProgressBar();
        this.data = data;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        progressBar = $(R.id.progress_bar);
        super.onCreate(savedInstanceState);
    }

    /**
     * @return The name of the saved data.
     */
    protected abstract String getDataName();

    /**
     * Launch a new request.
     */
    private void refresh() {
        Toast.makeText(getApplicationContext(), R.string.begin_refresh, Toast.LENGTH_SHORT).show();
        performRequest(true);
    }

    /**
     * Hide the progress bar.
     */
    private void hideProgressBar() {
        if(progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putParcelableArrayList(getDataName(), data);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Perform a spice request.
     *
     * @param force If the cache should be deleted or not.
     * @param r The request to execute.
     * @param <L> Parameter due to Java's type erasure.
     */
    <L extends ArrayList<D>> void performRequest(final boolean force, final AbstractRequest<L> r) {
        RequestHandler.performListRequest(force, r, this);
    }

    /**
     * @return The main view of this activity. Currently this is used for snack bars, but that may change.
     */
    protected abstract View getMainView();

    /**
     * When the request has failed.
     */
    public void requestFailure() {
        Snackbar.make(getMainView(), getString(R.string.failure), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.again), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        performRequest(false);
                    }
                })
                .show();
        hideProgressBar();
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }
}
