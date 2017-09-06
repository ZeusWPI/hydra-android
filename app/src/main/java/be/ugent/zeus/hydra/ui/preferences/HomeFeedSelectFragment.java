package be.ugent.zeus.hydra.ui.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.common.ViewUtils;
import be.ugent.zeus.hydra.ui.common.recyclerview.adapters.MultiSelectDiffAdapter;
import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DescriptionMultiSelectListViewHolder;

import java.util.*;

/**
 * Enables choosing the home feed card types.
 *
 * @author Niko Strijbol
 */
public class HomeFeedSelectFragment extends Fragment {

    private FeedOptionsAdapter adapter;
    private Map<String, String> valueMapper = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_feed_select, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        adapter = new FeedOptionsAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //TODO improve how this is saved.
        String[] values = getResources().getStringArray(R.array.card_types_names);
        String[] descriptions = getResources().getStringArray(R.array.card_types_descriptions);
        String[] ints = getResources().getStringArray(R.array.card_types_nr);

        List<Tuple> itemTuples = new ArrayList<>();
        valueMapper.clear();
        for (int i = 0; i < values.length; i++) {
            valueMapper.put(values[i], ints[i]);
            itemTuples.add(new Tuple(values[i], descriptions[i]));
        }

        adapter.setItems(itemTuples, true);
    }

    @Override
    public void onPause() {
        super.onPause();

        //Save the settings.
        //We save which cards we DON'T want, so we need to inverse it.
        Iterable<Pair<Tuple, Boolean>> values = adapter.getItemsAndState();
        Set<String> disabled = new HashSet<>();

        for (Pair<Tuple, Boolean> value : values) {
            if (!value.second) {
                disabled.add(valueMapper.get(value.first.getTitle()));
            }
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        preferences.edit().putStringSet(be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedFragment.PREF_DISABLED_CARDS, disabled).apply();
    }

    private static class Tuple {

        private final String title;
        private final String description;

        private Tuple(String title, String description) {
            this.title = title;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }
    }

    private static class FeedOptionsAdapter extends MultiSelectDiffAdapter<Tuple> {

        @Override
        public DataViewHolder<Pair<Tuple, Boolean>> onCreateViewHolder(ViewGroup parent, int viewType) {
            return new DescriptionMultiSelectListViewHolder<>(
                    ViewUtils.inflate(parent, R.layout.item_checkbox_string_description),
                    this,
                    Tuple::getTitle,
                    Tuple::getDescription
            );
        }
    }
}