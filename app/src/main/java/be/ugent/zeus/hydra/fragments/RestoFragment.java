package be.ugent.zeus.hydra.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.octo.android.robospice.GsonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.Calendar;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.adapters.RestoCardAdapter;
import be.ugent.zeus.hydra.models.Resto.RestoMeal;
import be.ugent.zeus.hydra.models.Resto.RestoMenu;
import be.ugent.zeus.hydra.requests.RestoMenuRequest;

/**
 * Created by mivdnber on 2016-03-03.
 */

public class RestoFragment extends Fragment {
    protected SpiceManager spiceManager = new SpiceManager(GsonSpringAndroidSpiceService.class);
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(getContext());
    }

    @Override
    public void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_resto, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.resto_cards_view);
        adapter = new RestoCardAdapter(new String[] {
                "eetbare dingen", "vegetarisch", "net niet vervallen",
                "eetbare dingen", "vegetarisch", "net niet vervallen",
                "eetbare dingen", "vegetarisch", "net niet vervallen"
        });
        recyclerView.setAdapter(adapter);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new StickyRecyclerHeadersDecoration((StickyRecyclerHeadersAdapter) adapter));

        performLoadActivityRequest();

        return view;
    }

    private void performLoadActivityRequest() {
        Calendar c = Calendar.getInstance();

        final RestoMenuRequest r = new RestoMenuRequest(c.getTime());

        spiceManager.execute(r, r.getCacheKey(), r.getCacheDuration(), new RequestListener<RestoMenu>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                System.out.println("Request failed");
            }

            @Override
            public void onRequestSuccess(final RestoMenu restoMenu) {
                ArrayList<String> listItems=new ArrayList<String>();
                ArrayAdapter<String> adapter;

                for(RestoMeal meal: restoMenu.getMeals()) {
                    listItems.add(meal.getName() + " " + meal.getPrice());
                    RestoFragment.this.adapter.notifyDataSetChanged();
                }
            }
        });
    }
}
