package be.ugent.zeus.hydra.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import be.ugent.zeus.hydra.fragments.InfoFragment;
import be.ugent.zeus.hydra.models.info.InfoItem;

import java.util.ArrayList;

public class InfoSubItemActivity extends AppCompatActivity {

    public static final String INFOTITLE = "be.ugent.zeus.hydra.infoTitle";
    public static final String INFOITEMS = "be.ugent.zeus.hydra.infoItems";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.HydraActionBar);
        super.onCreate(savedInstanceState);
        // Display the fragment as the main content.
        InfoFragment fragment = new InfoFragment();

        Intent intent = getIntent();

        // Set title
        String title = intent.getStringExtra(INFOTITLE);
        setTitle(title);

        // Create bundle for fragment
        ArrayList<InfoItem> infoList = intent.getParcelableArrayListExtra(INFOITEMS);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(InfoFragment.INFOLIST, infoList);
        fragment.setArguments(bundle);


        getSupportFragmentManager().beginTransaction().
                replace(android.R.id.content, fragment).commit();
    }

}
