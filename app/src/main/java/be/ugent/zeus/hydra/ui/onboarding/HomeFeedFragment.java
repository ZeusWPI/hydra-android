package be.ugent.zeus.hydra.ui.onboarding;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.common.recyclerview.MultiSelectListAdapter;
import com.heinrichreimersoftware.materialintro.app.SlideFragment;

import java.util.*;

import static be.ugent.zeus.hydra.ui.common.ViewUtils.$;

/**
 * This is not fun, because this class needs a manual multi checkbox list.
 * @author Niko Strijbol
 */
public class HomeFeedFragment extends SlideFragment {

    private MultiSelectListAdapter<String> adapter;
    private Map<String, String> valueMapper = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_onboarding_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = $(view, R.id.recycler_view);

        adapter = new MultiSelectListAdapter<>();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //TODO improve how this is saved.
        String[] values = getResources().getStringArray(R.array.card_types_names);
        String[] ints = getResources().getStringArray(R.array.card_types_nr);

        valueMapper.clear();
        for(int i = 0; i < values.length; i++) {
            valueMapper.put(values[i], ints[i]);
        }

        adapter.setValues(Arrays.asList(values), true);
    }

    @Override
    public void onPause() {
        super.onPause();

        //Save the settings.
        //We save which cards we DON'T want, so we need to inverse it.
        Collection<Pair<String, Boolean>> values = adapter.getItems();
        Set<String> disabled = new HashSet<>();

        for(Pair<String, Boolean> value: values) {
            if(!value.second) {
                disabled.add(valueMapper.get(value.first));
            }
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        preferences.edit().putStringSet(be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedFragment.PREF_DISABLED_CARDS, disabled).apply();
    }
}