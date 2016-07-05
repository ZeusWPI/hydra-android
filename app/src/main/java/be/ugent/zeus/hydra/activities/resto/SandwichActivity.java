package be.ugent.zeus.hydra.activities.resto;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.resto.common.RestoWebsiteActivity;
import be.ugent.zeus.hydra.loader.cache.CacheRequest;
import be.ugent.zeus.hydra.models.resto.Sandwich;
import be.ugent.zeus.hydra.models.resto.Sandwiches;
import be.ugent.zeus.hydra.recyclerview.adapters.resto.SandwichAdapter;
import be.ugent.zeus.hydra.requests.RestoSandwichesRequest;
import be.ugent.zeus.hydra.utils.DividerItemDecoration;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;

import java.util.Collections;
import java.util.Comparator;

/**
 * Activity that shows a list of sandwiches.
 *
 * @author Niko Strijbol
 */
public class SandwichActivity extends RestoWebsiteActivity<Sandwiches> {

    private static final String URL = "http://www.ugent.be/student/nl/meer-dan-studeren/resto/broodjes/overzicht.htm";

    private SandwichAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resto_sandwich);

        recyclerView = $(R.id.resto_sandwich_recycler);

        //Divider
        recyclerView.addItemDecoration(new DividerItemDecoration(this));

        //Adapter
        adapter = new SandwichAdapter(getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        RecyclerFastScroller s = $(R.id.fast_scroller);
        s.attachRecyclerView(recyclerView);

        startLoader();
    }

    /**
     * @return The URL for the overflow button to display a website link.
     */
    @Override
    protected String getUrl() {
        return URL;
    }

    /**
     * This method is used to receive new data, from the request for example.
     *
     * @param data The new data.
     */
    @Override
    public void receiveData(@NonNull Sandwiches data) {
        Collections.sort(data, new Comparator<Sandwich>() {
            @Override
            public int compare(Sandwich lhs, Sandwich rhs) {
                return lhs.name.compareToIgnoreCase(rhs.name);
            }
        });
        adapter.replaceData(data);
    }

    /**
     * @return The main view of this activity. Currently this is used for snackbars, but that may change.
     */
    @Override
    protected View getView() {
        return recyclerView;
    }

    @Override
    public CacheRequest<Sandwiches> getRequest() {
        return new RestoSandwichesRequest();
    }
}