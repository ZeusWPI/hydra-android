package be.ugent.zeus.hydra.ui.sko;

import android.os.Bundle;
import android.support.annotation.Nullable;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.common.BaseActivity;
import be.ugent.zeus.hydra.ui.preferences.SkoFragment;

/**
 * Show SKO preferences.
 *
 * @author Niko Strijbol
 */
public class PreferenceActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sko_pref);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(R.id.content, new SkoFragment())
                .commit();
    }
}