package be.ugent.zeus.hydra.feed.preferences;

import android.os.Bundle;
import androidx.annotation.Nullable;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.BaseActivity;

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