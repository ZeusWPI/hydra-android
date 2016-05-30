package be.ugent.zeus.hydra.activities.resto;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.adapters.resto.MetaAdapter;
import be.ugent.zeus.hydra.common.DividerItemDecoration;
import be.ugent.zeus.hydra.models.resto.RestoLocation;
import be.ugent.zeus.hydra.models.resto.RestoMeta;
import be.ugent.zeus.hydra.requests.RestoMetaRequest;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;

/**
 * Activity that shows a list of sandwiches.
 *
 * @author Niko Strijbol
 */
public class MetaActivity extends RestoActivity<RestoLocation> {

    private static final String DATA = "meta_data_list";

    private MetaAdapter adapter;
    private RecyclerView recyclerView;

    /**
     * Use a special menu.
     */
    @Override
    protected int getMenuId() {
        return R.menu.menu_resto_meta;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_resto_meta);
        super.onCreate(savedInstanceState);

        recyclerView = (RecyclerView) findViewById(R.id.resto_meta_recycler);

        //Divider
        recyclerView.addItemDecoration(new DividerItemDecoration(this));

        //Adapter
        adapter = new MetaAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        initFromBundle(savedInstanceState);
    }

    /**
     * Request the menu DATA. Once the DATA is loaded, pass it to the {@link MetaAdapter}.
     *
     * @param force If true, the cache is cleared.
     */
    public void performRequest(final boolean force) {

        RestoMetaRequest r = new RestoMetaRequest();
        SpiceManager manager = getManager();

        if (force) {
            manager.removeDataFromCache(r.getResultType(), r.getCacheKey());
        }

        manager.execute(r, r.getCacheKey(), r.getCacheDuration(), new RequestListener<RestoMeta>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                requestFailure();
            }

            @Override
            public void onRequestSuccess(final RestoMeta meta) {
                receiveData(meta.locations);
                if (force) {
                    Toast.makeText(getContext(), R.string.end_refresh, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    /**
     * This method is used to receive new data, from the request for example.
     *
     * @param data The new data.
     */
    @Override
    public void receiveData(ArrayList<RestoLocation> data) {
        super.receiveData(data);
        adapter.replaceData(data);
    }

    /**
     * @return The name of the saved data.
     */
    @Override
    protected String getDataName() {
        return DATA;
    }

    /**
     * @return The main view of this activity. Currently this is used for snackbars, but that may change.
     */
    @Override
    protected View getMainView() {
        return recyclerView;
    }
}