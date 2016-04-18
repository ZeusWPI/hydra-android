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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.Text;

import java.util.Date;
import java.util.Locale;

import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.association.AssociationActivity;
import be.ugent.zeus.hydra.utils.DateUtils;

public class AssociationActivityDetail extends AppCompatActivity {
    private AssociationActivity associationActivity;

    private ImageView imageView;
    private TextView title;
    private TextView association;
    private TextView date;
    private TextView location;
    private TextView description;
    private Button link;
    private Button map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_association_activity_detail);

        Intent intent = getIntent();
        associationActivity = intent.getParcelableExtra("associationActivity");

        title = (TextView) findViewById(R.id.title);
        association = (TextView) findViewById(R.id.association);
        date = (TextView) findViewById(R.id.date);
        location = (TextView) findViewById(R.id.location);
        description = (TextView) findViewById(R.id.description);
        imageView = (ImageView) findViewById(R.id.imageView);
        link = (Button) findViewById(R.id.link);
        map = (Button) findViewById(R.id.map);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setCustomView(R.layout.actionbar_centered_hydra);

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

        if(associationActivity.location != null) {
            location.setText(associationActivity.location);
        }

        if(associationActivity.start != null) {
            DateTimeFormatter startTimeFormatter = DateTimeFormat.forPattern("E d MMM H:mm");

            DateTime start = new DateTime(associationActivity.start);
            if (associationActivity.end != null) {
                DateTime end = new DateTime(associationActivity.end);
                if (start.dayOfYear() == end.dayOfYear() || start.plusHours(12).isAfter(end)) {
                    // Use format day month start time - end time
                    DateTimeFormatter endTimeFormatter = DateTimeFormat.forPattern("H:mm");
                    date.setText(String.format("%s - %s", startTimeFormatter.print(start), endTimeFormatter.print(end)));
                } else {
                    // Use format with two dates
                    date.setText(String.format("%s - %s",startTimeFormatter.print(start), startTimeFormatter.print(end)));
                }
            } else {
                date.setText(startTimeFormatter.print(start));
            }
        }

        Picasso.with(this).load(associationActivity.association.getImageLink()).into(imageView);

        if (associationActivity.url != null && !associationActivity.url.isEmpty()) {
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

        if ((associationActivity.latitude != 0.0) && (associationActivity.longitude != 0.0)) {
            map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uri = String.format(Locale.ENGLISH, "geo:%f,%f", associationActivity.latitude, associationActivity.longitude);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);
                }
            });
        } else {
            map.setVisibility(View.GONE);
        }

        HydraApplication app = (HydraApplication) getApplication();
        Tracker t = app.getDefaultTracker();
        t.setScreenName("Activity > " + associationActivity.title);
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
