package be.ugent.zeus.hydra.ui.preferences;

import android.os.Bundle;
import android.support.annotation.Nullable;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.common.BaseActivity;

/**
 * Allow the user to select which home feed things he/she wants.
 *
 * @author Niko Strijbol
 */
public class HomeFeedPrefActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences_homefeed);
    }
}