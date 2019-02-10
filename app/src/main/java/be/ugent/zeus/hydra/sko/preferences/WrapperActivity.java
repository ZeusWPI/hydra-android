package be.ugent.zeus.hydra.sko.preferences;

import android.os.Bundle;
import androidx.annotation.Nullable;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.BaseActivity;

/**
 * Show SKO preferences. This activity is a wrapper around {@link SkoPreferencesFragment}.
 *
 * @author Niko Strijbol
 */
public class WrapperActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sko_pref);
    }
}