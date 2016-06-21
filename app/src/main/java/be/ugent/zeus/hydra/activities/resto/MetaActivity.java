package be.ugent.zeus.hydra.activities.resto;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.resto.common.RestoActivity;
import be.ugent.zeus.hydra.loader.cache.Request;
import be.ugent.zeus.hydra.models.resto.RestoMeta;
import be.ugent.zeus.hydra.recyclerview.adapters.resto.MetaAdapter;
import be.ugent.zeus.hydra.requests.RestoMetaRequest;
import be.ugent.zeus.hydra.utils.DividerItemDecoration;

/**
 * Activity that shows a list of sandwiches.
 *
 * @author Niko Strijbol
 */
public class MetaActivity extends RestoActivity<RestoMeta> {

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

        recyclerView = $(R.id.resto_meta_recycler);

        //Divider
        recyclerView.addItemDecoration(new DividerItemDecoration(this));

        //Adapter
        adapter = new MetaAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        startLoader();
    }

    /**
     * This method is used to receive new data, from the request for example.
     *
     * @param data The new data.
     */
    @Override
    public void receiveData(@NonNull RestoMeta data) {
        adapter.replaceData(data.locations);
    }

    /**
     * @return The main view of this activity. Currently this is used for snack bars, but that may change.
     */
    @Override
    protected View getView() {
        return recyclerView;
    }

    @Override
    public Request<RestoMeta> getRequest() {
        return new RestoMetaRequest();
    }
}