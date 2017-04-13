package be.ugent.zeus.hydra.ui;

import android.content.Intent;
import android.os.Bundle;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.common.BaseActivity;
import be.ugent.zeus.hydra.ui.main.InfoFragment;
import be.ugent.zeus.hydra.data.models.info.InfoItem;

import java.util.ArrayList;

public class InfoSubItemActivity extends BaseActivity {

    public static final String INFO_TITLE = "be.ugent.zeus.hydra.infoTitle";
    public static final String INFO_ITEMS = "be.ugent.zeus.hydra.infoItems";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_sub_item);
        // Display the fragment as the main content.
        InfoFragment fragment = new InfoFragment();

        Intent intent = getIntent();

        // Set title
        String title = intent.getStringExtra(INFO_TITLE);
        getToolbar().setTitle(title);

        // Create bundle for fragment
        ArrayList<InfoItem> infoList = intent.getParcelableArrayListExtra(INFO_ITEMS);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(INFO_ITEMS, infoList);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().add(R.id.info_sub_item, fragment).commit();
    }
}