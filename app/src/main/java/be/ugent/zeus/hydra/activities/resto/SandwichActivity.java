package be.ugent.zeus.hydra.activities.resto;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.resto.Sandwiches;
import be.ugent.zeus.hydra.recyclerview.adapters.resto.SandwichAdapter;
import be.ugent.zeus.hydra.requests.resto.RestoSandwichesRequest;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;

import java.util.Collections;

/**
 * Activity that shows a list of sandwiches.
 *
 * @author Niko Strijbol
 */
public class SandwichActivity extends RestoActivity<Sandwiches> {

    private static final String URL = "http://www.ugent.be/student/nl/meer-dan-studeren/resto/broodjes/overzicht.htm";

    private SandwichAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resto_sandwich);

        RecyclerView recyclerView = $(R.id.resto_sandwich_recycler);

        //Divider
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        //Adapter
        adapter = new SandwichAdapter(getApplicationContext());
        recyclerView.setAdapter(adapter);

        RecyclerFastScroller s = $(R.id.fast_scroller);
        s.attachRecyclerView(recyclerView);

        loaderPlugin.startLoader();
    }

    @Override
    protected String getUrl() {
        return URL;
    }

    @Override
    public void receiveData(@NonNull Sandwiches data) {
        Collections.sort(data, (lhs, rhs) -> lhs.name.compareToIgnoreCase(rhs.name));
        adapter.replaceData(data);
    }

    @Override
    public RestoSandwichesRequest getRequest() {
        return new RestoSandwichesRequest();
    }
}