package be.ugent.zeus.hydra.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.Association.AssociationActivity;

public class AssociationActivityDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_association_activity_detail);

        Intent intent = getIntent();
        AssociationActivity associationActivity = intent.getParcelableExtra("associationActivity");

        TextView title = (TextView) findViewById(R.id.activityTitle);
        TextView association = (TextView) findViewById(R.id.activityAssociation);
        TextView location = (TextView) findViewById(R.id.activityLocation);
        TextView url = (TextView) findViewById(R.id.activityURL);
        TextView facebook_id = (TextView) findViewById(R.id.activityFacebookID);
        TextView description = (TextView) findViewById(R.id.activityDescription);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setCustomView(R.layout.actionbar_centered_hydra);
        //View v = getLayoutInflater().inflate(R.layout.actionbar_centered_hydra, null);
        //actionbar.setCustomView(v);

        //back arrow
        actionbar.setDisplayHomeAsUpEnabled(true);

        if(associationActivity.title != null){ title.setText(associationActivity.title);}
        else {title.setText("no data");}
        if(associationActivity.association != null && associationActivity.association.display_name != null) association.setText(associationActivity.association.display_name);
        else association.setText("no data");
        if(associationActivity.location != null)location.setText(associationActivity.location);
        else location.setText("no data");
        if(associationActivity.url != null) url.setText(associationActivity.url);
        else url.setText("no data + " + associationActivity.url);
        if(associationActivity.facebook_id != null) facebook_id.setText(associationActivity.facebook_id);
        else facebook_id.setText("no data");
        if(associationActivity.description != null) description.setText(associationActivity.description);
        else description.setText("no data");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
