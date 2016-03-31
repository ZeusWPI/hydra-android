package be.ugent.zeus.hydra.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.octo.android.robospice.GsonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.association.Association;
import be.ugent.zeus.hydra.models.association.Associations;
import be.ugent.zeus.hydra.requests.AssociationsRequest;

/**
 * @author Rien Maertens
 * @since 16/02/2016.
 */
public class SettingsFragment extends PreferenceFragment {
    protected SpiceManager spiceManager = new SpiceManager(GsonSpringAndroidSpiceService.class);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.layout.preferences);
        performRequest();

        functie();
    }

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(getActivity());
    }

    @Override
    public void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    private void addPreferencesFromRequest(final Associations assocations) {
        MultiSelectListPreference assoPrefList = (MultiSelectListPreference) this.findPreference("associationPrefList");
        CharSequence[] entries;
        if(assocations!=null) {
            System.out.println("--------"+assocations.size());
            entries = new CharSequence[assocations.size()];

            for (int i = 0; i < assocations.size(); i++) {
                if (assocations.get(i) == null) {
                    entries[i] = "" + i + "is null";
                } else {
                    entries[i] = assocations.get(i).getName()==null?"ik ben null":assocations.get(i).getName();
                }
                //entries[i]="yolo"+i;
            }
        }else {
            entries = new CharSequence[]{"a", "b", "c"};
        }
        assoPrefList.setEntries(entries);
        assoPrefList.setEntryValues(entries);

    }

    //LOL
    private void functie(){
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity());


        boolean sf = sharedPrefs.getBoolean("pref_association_checkbox", false); //this works
        System.out.println("--------------"+sf);
    }



    private void performRequest() {
        System.out.println("------ ok master");
        final AssociationsRequest r = new AssociationsRequest();
        spiceManager.execute(r, r.getCacheKey(), r.getCacheDuration(), new RequestListener<Associations>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                showFailureSnackbar();
            }

            @Override
            public void onRequestSuccess(final Associations assocations) {
                System.out.println("--------- yolooooooooo");
                addPreferencesFromRequest(assocations);
            }
        });
    }

    private void showFailureSnackbar() {
        System.out.println("-------------- fail :(");
        if(getView()!=null) {
            Snackbar
                    .make(getView(), "Oeps! Kon restomenu niet ophalen.", Snackbar.LENGTH_LONG)
                    .setAction("Opnieuw proberen", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            performRequest();
                        }
                    })
                    .show();
        }else{
            //cry
        }
    }


}
