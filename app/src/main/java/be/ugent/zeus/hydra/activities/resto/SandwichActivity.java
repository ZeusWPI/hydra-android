package be.ugent.zeus.hydra.activities.resto;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.adapters.resto.MenuPageAdapter;
import be.ugent.zeus.hydra.adapters.resto.SandwichAdapter;
import be.ugent.zeus.hydra.common.DividerItemDecoration;
import be.ugent.zeus.hydra.models.resto.Sandwich;
import be.ugent.zeus.hydra.requests.RestoSandwichesRequest;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Activity that shows a list of sandwiches.
 *
 * @author Niko Strijbol
 */
public class SandwichActivity extends RestoWebsiteActivity<Sandwich> {

    private static final String DATA = "sandwich_data_list";
    private static final String URL = "http://www.ugent.be/student/nl/meer-dan-studeren/resto/broodjes/overzicht.htm";

    private SandwichAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resto_sandwich);
        setUpActionBar();

        recyclerView = (RecyclerView) findViewById(R.id.resto_sandwich_recycler);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        //Divider
        recyclerView.addItemDecoration(new DividerItemDecoration(this));

        //Adapter
        adapter = new SandwichAdapter(getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        RecyclerFastScroller s = (RecyclerFastScroller) findViewById(R.id.fast_scroller);
        s.attachRecyclerView(recyclerView);

        initFromBundle(savedInstanceState);
    }

    /**
     * @return The URL for the overflow button to display a website link.
     */
    @Override
    protected String getUrl() {
        return URL;
    }

    /**
     * Request the menu DATA. Once the DATA is loaded, pass it to the {@link MenuPageAdapter}.
     *
     * @param force If true, the cache is cleared.
     */
    public void performRequest(final boolean force) {
        performRequest(force, new RestoSandwichesRequest());
    }

    /**
     * This method is used to receive new data, from the request for example.
     *
     * @param data The new data.
     */
    @Override
    public void receiveData(ArrayList<Sandwich> data) {
        Collections.sort(data, new Comparator<Sandwich>() {
            @Override
            public int compare(Sandwich lhs, Sandwich rhs) {
                return lhs.name.compareToIgnoreCase(rhs.name);
            }
        });
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