package be.ugent.zeus.hydra.activities;

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

import java.util.Locale;

import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.association.Activity;

public class ActivityDetail extends AppCompatActivity {
    private Activity activity;

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
        activity = intent.getParcelableExtra("associationActivity");

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

        if(activity.getTitle() != null){
            title.setText(activity.getTitle());
        }
        if(activity.getAssociation() != null ) {
            association.setText(activity.getAssociation().getName());
        }

        if(activity.getDescription() != null) {
            description.setText(activity.getDescription());
        }

        if(activity.getLocation() != null) {
            location.setText(activity.getLocation());
        }

        if(activity.getStart() != null) {
            DateTimeFormatter startTimeFormatter = DateTimeFormat.forPattern("E d MMM H:mm");

            DateTime start = new DateTime(activity.getStart());
            if (activity.getEnd() != null) {
                DateTime end = new DateTime(activity.getEnd());
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

        Picasso.with(this).load(activity.getAssociation().getImageLink()).into(imageView);

        if (activity.getUrl() != null && !activity.getUrl().isEmpty()) {
            link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(activity.getUrl()));
                    startActivity(browserIntent);
                }
            });
        } else {
            link.setVisibility(View.GONE);
        }

        if ((activity.getLatitude() != 0.0) && (activity.getLongitude() != 0.0)) {
            map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uri = String.format(Locale.ENGLISH, "geo:%f,%f", activity.getLatitude(), activity.getLongitude());
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);
                }
            });
        } else {
            map.setVisibility(View.GONE);
        }

        HydraApplication app = (HydraApplication) getApplication();
        Tracker t = app.getDefaultTracker();
        t.setScreenName("Activity > " + activity.getTitle());
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
