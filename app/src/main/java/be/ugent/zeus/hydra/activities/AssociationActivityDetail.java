package be.ugent.zeus.hydra.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.association.AssociationActivity;

public class AssociationActivityDetail extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_association_activity_detail);

        Intent intent = getIntent();
        AssociationActivity associationActivity = intent.getParcelableExtra("associationActivity");

        TextView title = (TextView) findViewById(R.id.activitytitle);
        TextView association = (TextView) findViewById(R.id.activityAssociation);
        TextView location = (TextView) findViewById(R.id.activityLocation);
        TextView url = (TextView) findViewById(R.id.activityURL);
        TextView facebook_id = (TextView) findViewById(R.id.activityFacebookID);
        TextView description = (TextView) findViewById(R.id.activityDescription);

        if (associationActivity.title != null) title.setText(associationActivity.title);
        if (associationActivity.association != null && associationActivity.association.full_name != null)
            association.setText(associationActivity.association.full_name);
        if (associationActivity.location != null) location.setText(associationActivity.location);
        if (associationActivity.url != null) url.setText(associationActivity.url);
        if (associationActivity.facebook_id != null)
            facebook_id.setText(associationActivity.facebook_id);
        if (associationActivity.description != null)
            description.setText(associationActivity.description);
    }

}
