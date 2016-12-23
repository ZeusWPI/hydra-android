package be.ugent.zeus.hydra.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.preferences.AssociationSelectPrefActivity;
import be.ugent.zeus.hydra.activities.preferences.SettingsActivity;
import be.ugent.zeus.hydra.models.association.Event;
import be.ugent.zeus.hydra.models.association.Events;
import be.ugent.zeus.hydra.plugins.RecyclerViewPlugin;
import be.ugent.zeus.hydra.plugins.common.Plugin;
import be.ugent.zeus.hydra.plugins.common.PluginFragment;
import be.ugent.zeus.hydra.recyclerview.adapters.EventAdapter;
import be.ugent.zeus.hydra.requests.association.FilteredEventRequest;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.List;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Displays a list of activities, filtered by the settings.
 *
 * @author ellen
 * @author Niko Strijbol
 */
public class ActivitiesFragment extends PluginFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final EventAdapter adapter = new EventAdapter();
    private final RecyclerViewPlugin<Event, Events> plugin = new RecyclerViewPlugin<>(FilteredEventRequest::new, adapter);
    private LinearLayout noData;

    //If the data is invalidated.
    private boolean invalid = false;

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);

        plugin.defaultError()
                .hasProgress()
                .setDataCallback(this::receiveData);

        plugins.add(plugin);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activities, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        noData = $(view, R.id.events_no_data);

        plugin.addItemDecoration(new StickyRecyclerHeadersDecoration(adapter));
        plugin.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        Button refresh = $(view, R.id.events_no_data_button_refresh);
        Button filters = $(view, R.id.events_no_data_button_filters);

        refresh.setOnClickListener(v -> plugin.refresh());
        filters.setOnClickListener(v -> startActivity(new Intent(getContext(), SettingsActivity.class)));

        //Register this class in the settings.
        PreferenceManager.getDefaultSharedPreferences(getContext()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        //Refresh the data.
        if (invalid) {
            plugin.restartLoader();
            invalid = false;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(AssociationSelectPrefActivity.PREF_ASSOCIATIONS_SHOWING.equals(key)) {
            invalid = true;
        }
    }

    private void receiveData(Events data) {
        //If empty, show it.
        if(data.isEmpty()) {
            noData.setVisibility(View.VISIBLE);
        } else {
            noData.setVisibility(View.GONE);
        }
    }
}