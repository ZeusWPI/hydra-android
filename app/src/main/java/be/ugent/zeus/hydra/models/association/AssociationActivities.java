package be.ugent.zeus.hydra.models.association;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;

/**
 * Created by feliciaan on 27/01/16.
 */
public class AssociationActivities extends ArrayList<AssociationActivity> {
    private static final long serialVersionUID = 32432552425423423L;

    public AssociationActivities getPreferedActivities(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        boolean filter = sharedPrefs.getBoolean("pref_association_checkbox", false);
        if(filter) {
            AssociationActivities prefered = new AssociationActivities();

            for (int i = 0; i < this.size(); i++) {
                AssociationActivity asso = this.get(i);
                boolean sf = sharedPrefs.getBoolean(asso.association.getName(), false);
                if (sf) {
                    prefered.add(asso);
                }

            }

            return prefered;
        }else{
            return this;
        }

    }
}
