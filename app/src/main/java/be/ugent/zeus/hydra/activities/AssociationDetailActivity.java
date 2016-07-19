package be.ugent.zeus.hydra.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.ToolbarActivity;
import be.ugent.zeus.hydra.models.association.Association;
import com.squareup.picasso.Picasso;

/**
 * Activity to show details of an association's event.
 */
public class AssociationDetailActivity extends ToolbarActivity {

    public static final String PARCEL_ASSOCIATION = "associationParcelable";

    private static final String DSA_URL = "http://student.ugent.be/";

    //The data
    private Association association;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_association_detail);

        customFade();

        Intent intent = getIntent();
        association = intent.getParcelableExtra(PARCEL_ASSOCIATION);

        TextView name = $(R.id.association_name);
        TextView full = $(R.id.association_name_full);
        ImageView image = $(R.id.association_image);

        name.setText(association.getDisplayName());
        full.setText(association.getFullName());

        Picasso.with(this).load(association.getImageLink()).into(image);
    }

    @Override
    protected void sendScreen(HydraApplication application) {
        application.sendScreenName("Association > " + association.getInternalName());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            supportFinishAfterTransition();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}