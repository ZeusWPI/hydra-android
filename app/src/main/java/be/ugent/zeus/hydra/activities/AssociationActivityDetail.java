package be.ugent.zeus.hydra.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.association.AssociationActivity;

public class AssociationActivityDetail extends AppCompatActivity {
    private AssociationActivity associationActivity;

    private ImageView imageView;
    private TextView title;
    private TextView association;
    private TextView description;
    private Button link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_association_activity_detail);

        Intent intent = getIntent();
        associationActivity = intent.getParcelableExtra("associationActivity");

        title = (TextView) findViewById(R.id.title);
        association = (TextView) findViewById(R.id.association);
        description = (TextView) findViewById(R.id.description);
        imageView = (ImageView) findViewById(R.id.imageView);
        link = (Button) findViewById(R.id.link);
        //TextView location = (TextView) findViewById(R.id.activityLocation);
        //TextView url = (TextView) findViewById(R.id.activityURL);
        //TextView facebook_id = (TextView) findViewById(R.id.activityFacebookID);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setCustomView(R.layout.actionbar_centered_hydra);
        //View v = getLayoutInflater().inflate(R.layout.actionbar_centered_hydra, null);
        //actionbar.setCustomView(v);

        //back arrow
        actionbar.setDisplayHomeAsUpEnabled(true);

        if(associationActivity.title != null){
            title.setText(associationActivity.title);
        }
        if(associationActivity.association != null ) {
            association.setText(associationActivity.association.getName());
        }

        if(associationActivity.description != null) {
            description.setText(associationActivity.description);
        }

        Picasso.with(this).load(associationActivity.association.getImageLink()).into(imageView);

        if (associationActivity.url != null) {
            link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(associationActivity.url));
                    startActivity(browserIntent);
                }
            });
        } else {
            link.setVisibility(View.GONE);
        }
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

    @Override
    public void onResume() {
        super.onResume();

        HydraApplication app = (HydraApplication) getApplication();
        Tracker t = app.getDefaultTracker();
        t.setScreenName("Activity > " + associationActivity.title);
    }
}
