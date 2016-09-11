package be.ugent.zeus.hydra.fragments.sko;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.cache.CachedAsyncTaskLoader;
import be.ugent.zeus.hydra.fragments.common.LoaderFragment;
import be.ugent.zeus.hydra.loader.ThrowableEither;
import be.ugent.zeus.hydra.models.sko.Stage;
import be.ugent.zeus.hydra.models.sko.Stages;
import be.ugent.zeus.hydra.recyclerview.adapters.sko.LineupAdapter;
import be.ugent.zeus.hydra.requests.sko.LineupRequest;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
public class LineupFragment extends LoaderFragment<Stages> {

    protected RecyclerView recyclerView;
    protected LineupAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = $(view, R.id.recycler_view);

        adapter = new LineupAdapter();

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    /**
     * This must be called when data is received that has no errors.
     *
     * @param data The data.
     */
    @Override
    public void receiveData(@NonNull Stages data) {

        //Get first stage
        Stage stage = data.get(0);

        adapter.setItems(stage.getArtists());
    }

    @Override
    public Loader<ThrowableEither<Stages>> onCreateLoader(int id, Bundle args) {
        return new CachedAsyncTaskLoader<>(new LineupRequest(), getContext());
    }
}
