package be.ugent.zeus.hydra.activities.resto;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.resto.common.RestoActivity;
import be.ugent.zeus.hydra.models.resto.RestoMeta;
import be.ugent.zeus.hydra.recyclerview.adapters.resto.MetaAdapter;
import be.ugent.zeus.hydra.requests.resto.RestoMetaRequest;

/**
 * Activity that shows a list of resto's.
 *
 * @author Niko Strijbol
 */
public class MetaActivity extends RestoActivity<RestoMeta> {

    private MetaAdapter adapter;

    @Override
    protected int getMenuId() {
        return R.menu.menu_resto_meta;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resto_meta);

        RecyclerView recyclerView = $(R.id.resto_meta_recycler);

        //Divider
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        //Adapter
        adapter = new MetaAdapter();
        recyclerView.setAdapter(adapter);

        loaderHandler.startLoader();
    }

    @Override
    public void receiveData(@NonNull RestoMeta data) {
        adapter.replaceData(data.locations);
    }

    @Override
    public RestoMetaRequest getRequest() {
        return new RestoMetaRequest();
    }
}