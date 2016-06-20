package be.ugent.zeus.hydra.models.association;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;

/**
 * Created by feliciaan on 27/01/16.
 */
public class Activities extends ArrayList<Activity> {
    private static final long serialVersionUID = 32432552425423423L;

    public Activities getPreferedActivities(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        boolean filter = sharedPrefs.getBoolean("pref_association_checkbox", false);
        if(filter) {
            Activities prefered = new Activities();

            for (int i = 0; i < this.size(); i++) {
                Activity asso = this.get(i);
                boolean sf = sharedPrefs.getBoolean(asso.getAssociation().getName(), false);
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
