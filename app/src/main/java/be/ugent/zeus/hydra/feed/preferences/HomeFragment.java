package be.ugent.zeus.hydra.feed.preferences;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.StringDef;
import android.widget.Toast;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.arch.observers.EventObserver;
import be.ugent.zeus.hydra.common.ui.widgets.MenuTable;

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

    private static final String PREF_RESTO_KINDS = "pref_feed_resto_kinds";
    private static final String PREF_RESTO_KINDS_DEFAULT = FeedRestoKind.ALL;

    /**
     * The possible values for the {@link #PREF_RESTO_KINDS} preference. These are also defined in an XML array
     * resource.
     */
    @StringDef({FeedRestoKind.ALL, FeedRestoKind.MAIN, FeedRestoKind.SOUP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FeedRestoKind {
        String ALL = "all";
        String SOUP = "soup";
        String MAIN = "main";
    }

    @MenuTable.DisplayKind
    public static int getFeedRestoKind(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        @FeedRestoKind
        String value = pref.getString(PREF_RESTO_KINDS, PREF_RESTO_KINDS_DEFAULT);

        switch (value) {
            case FeedRestoKind.SOUP:
                return MenuTable.DisplayKind.SOUP;
            case FeedRestoKind.MAIN:
                return MenuTable.DisplayKind.MAIN;
            case FeedRestoKind.ALL:
            default:
                // Don't show vegetables.
                return MenuTable.DisplayKind.ALL & ~MenuTable.DisplayKind.VEGETABLES;
        }
    }

    @FeedRestoKind
    public static String getFeedRestoKindRaw(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(PREF_RESTO_KINDS, PREF_RESTO_KINDS_DEFAULT);
    }

    public static void setFeedRestoKind(Context context, @FeedRestoKind String kind) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit()
                .putString(PREF_RESTO_KINDS, kind)
                .apply();
    }
}