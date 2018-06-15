package be.ugent.zeus.hydra.feed.preferences;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.widget.Toast;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.arch.observers.EventObserver;

/**
 * Settings about the home feed.
 *
 * @author Niko Strijbol
 */
public class HomeFragment extends PreferenceFragment implements LifecycleOwner {

    public static final String PREF_DATA_SAVER = "pref_home_feed_save_data";
    public static final boolean PREF_DATA_SAVER_DEFAULT = false;

    private DeleteViewModel viewModel;
    private LifecycleRegistry registry;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registry = new LifecycleRegistry(this);
        registry.markState(Lifecycle.State.CREATED);

        viewModel = new DeleteViewModel(getActivity());

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_home_feed);

        viewModel.getLiveData().observe(this, new EventObserver<Context>() {
            @Override
            protected void onUnhandled(Context data) {
                Toast.makeText(data, R.string.feed_pref_hidden_cleared, Toast.LENGTH_SHORT).show();
            }
        });

        findPreference("pref_home_feed_clickable").setOnPreferenceClickListener(preference -> {
            viewModel.deleteAll();
            return true;
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        registry.markState(Lifecycle.State.STARTED);
    }

    @Override
    public void onResume() {
        super.onResume();
        registry.markState(Lifecycle.State.RESUMED);
    }

    @Override
    public void onPause() {
        super.onPause();
        registry.markState(Lifecycle.State.STARTED);
    }

    @Override
    public void onStop() {
        super.onStop();
        registry.markState(Lifecycle.State.CREATED);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        registry.markState(Lifecycle.State.DESTROYED);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return registry;
    }
}