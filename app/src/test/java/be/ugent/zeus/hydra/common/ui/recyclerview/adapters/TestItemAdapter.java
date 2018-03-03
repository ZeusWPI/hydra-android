package be.ugent.zeus.hydra.common.ui.recyclerview.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.SimpleViewHolder;

/**
 * @author Niko Strijbol
 */
public class TestItemAdapter extends Adapter<Integer, SimpleViewHolder<?>> {

    private boolean isChanged;

    public TestItemAdapter() {
        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                isChanged = true;
            }
        });
    }

    @NonNull
    @Override
    public SimpleViewHolder<?> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SimpleViewHolder<>(new View(null));
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleViewHolder<?> holder, int position) {
        // DO NOTHING.
    }

    /**
     * Take the changed value and set it to false.
     *
     * @return The changed value.
     */
    public boolean takeChanged() {
        boolean current = isChanged;
        isChanged = false;
        return current;
    }
}