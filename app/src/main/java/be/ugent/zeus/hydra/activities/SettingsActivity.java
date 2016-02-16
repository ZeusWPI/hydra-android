package be.ugent.zeus.hydra.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * @author Rien Maertens
 * @since 16/02/2016.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment()).commit();

    }
}
