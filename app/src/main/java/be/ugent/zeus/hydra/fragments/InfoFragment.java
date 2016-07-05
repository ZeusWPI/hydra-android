package be.ugent.zeus.hydra.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.InfoSubItemActivity;
import be.ugent.zeus.hydra.fragments.common.LoaderFragment;
import be.ugent.zeus.hydra.loader.cache.CacheRequest;
import be.ugent.zeus.hydra.models.info.InfoItem;
import be.ugent.zeus.hydra.models.info.InfoList;
import be.ugent.zeus.hydra.recyclerview.adapters.InfoListAdapter;
import be.ugent.zeus.hydra.requests.InfoRequest;

import java.util.ArrayList;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

public class InfoFragment extends LoaderFragment<InfoList> {

    private InfoListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = $(view, R.id.recycler_view);
        adapter = new InfoListAdapter();

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Bundle bundle = getArguments();
        if (bundle != null) {
            InfoList infoItems = new InfoList();
            ArrayList<InfoItem> list = bundle.getParcelableArrayList(InfoSubItemActivity.INFO_ITEMS);
            infoItems.addAll(list);
            receiveData(infoItems);
            this.autoStart = false;
        }
    }

    /**
     * This must be called when data is received that has no errors.
     *
     * @param data The data.
     */
    @Override
    public void receiveData(@NonNull InfoList data) {
        adapter.setItems(data);
        hideProgressBar();
    }

    /**
     * @return The request that will be executed.
     */
    @Override
    public CacheRequest<InfoList> getRequest() {
        return new InfoRequest();
    }
}