package be.ugent.zeus.hydra.fragments.preferences;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.design.widget.Snackbar;
import android.view.View;
import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.loader.ThrowableEither;
import be.ugent.zeus.hydra.cache.SystemCachedAsyncTaskLoader;
import be.ugent.zeus.hydra.models.association.Association;
import be.ugent.zeus.hydra.models.association.Associations;
import be.ugent.zeus.hydra.requests.AssociationsRequest;

import java.util.*;

/**
 * Settings about the association events.
 *
 * @author Rien Maertens
 */
public class ActivityFragment extends PreferenceFragment implements LoaderManager.LoaderCallbacks<ThrowableEither<Associations>> {

    private static final int LOADER = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_activities);

        getLoaderManager().initLoader(LOADER, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        HydraApplication.getApplication(getActivity()).sendScreenName("Settings > Events");
    }

    private void addPreferencesFromRequest(final List<Association> associations) {
        Set<String> set = new HashSet<>();
        PreferenceScreen target = (PreferenceScreen) findPreference("associationPrefListScreen");

        target.setTitle("Verenigingen");

        for (Association asso : associations) {
            PreferenceCategory parentCategory;
            if (!set.contains(asso.getParentAssociation())) {
                parentCategory = new PreferenceCategory(target.getContext());
                parentCategory.setKey(asso.getParentAssociation());
                parentCategory.setTitle(asso.getParentAssociation());
                target.addPreference(parentCategory);
                set.add(asso.getParentAssociation());
            }
            parentCategory = (PreferenceCategory) findPreference(asso.getParentAssociation());
            CheckBoxPreference checkBoxPreference = new CheckBoxPreference(target.getContext());
            checkBoxPreference.setKey(asso.getName());
            checkBoxPreference.setChecked(false);
            checkBoxPreference.setTitle(asso.getName());
            parentCategory.addPreference(checkBoxPreference);
        }
    }

    private void showFailureSnackbar() {
        assert getView() != null;
        Snackbar.make(getView(), "Oeps! Kon verenigingen niet ophalen.", Snackbar.LENGTH_LONG)
                .setAction("Opnieuw proberen", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getLoaderManager().restartLoader(LOADER, null, ActivityFragment.this);
                    }
                })
                .show();
    }

    @Override
    public Loader<ThrowableEither<Associations>> onCreateLoader(int id, Bundle args) {
        return new SystemCachedAsyncTaskLoader<>(new AssociationsRequest(), getActivity().getApplicationContext());
    }

    @Override
    public void onLoadFinished(Loader<ThrowableEither<Associations>> loader, ThrowableEither<Associations> data) {
        if (data.hasError()) {
            showFailureSnackbar();
        } else {
            List<Association> associations = data.getData();
            Collections.sort(associations, new Comparator<Association>() {
                @Override
                public int compare(Association lhs, Association rhs) {
                    return lhs.getName().compareToIgnoreCase(rhs.getName());
                }
            });
            addPreferencesFromRequest(associations);
        }
    }

    @Override
    public void onLoaderReset(Loader<ThrowableEither<Associations>> loader) {
        loader.reset();
    }
}