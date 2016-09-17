package be.ugent.zeus.hydra.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.ToolbarActivity;
import be.ugent.zeus.hydra.models.association.Activity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Activity to show details of an association's event.
 */
public class ActivityDetailActivity extends ToolbarActivity implements View.OnClickListener {

    public static final String PARCEL_EVENT = "eventParcelable";

    private static final String GENT = "51.3,3.44";

    //The data
    private Activity event;

    private ImageView organisatorImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        //Get data from saved instance, or from intent
        event = getIntent().getParcelableExtra(PARCEL_EVENT);

        TextView title = $(R.id.title);
        TextView date = $(R.id.date);
        TextView location = $(R.id.location);
        TextView description = $(R.id.description);
        organisatorImage = $(R.id.event_organisator_image);
        TextView mainName = $(R.id.event_organisator_main);
        TextView smallName = $(R.id.event_organisator_small);

        if(event.getTitle() != null){
            title.setText(event.getTitle());
            getToolBar().setTitle(event.getTitle());
        }

        if(event.getAssociation() != null ) {
            mainName.setText(event.getAssociation().getDisplayName());
            smallName.setText(event.getAssociation().getFullName());
        }

        if(event.getDescription() != null && !event.getDescription().trim().isEmpty()) {
            description.setText(event.getDescription());
        }

        if(event.hasLocation()) {
            location.setText(event.getLocation());
        } else {
            location.setText("Zonder locatie");
        }

        if(event.getStart() != null) {
            DateTimeFormatter startTimeFormatter = DateTimeFormat.forPattern("E d MMM H:mm");

            DateTime start = new DateTime(event.getStart());
            if (event.getEnd() != null) {
                DateTime end = new DateTime(event.getEnd());
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

        if(event.getAssociation() != null && event.getAssociation().getImageLink() != null) {
            Picasso.with(this).load(event.getAssociation().getImageLink()).into(organisatorImage, new Callback() {
                @Override
                public void onSuccess() {
                    //OK
                }

                @Override
                public void onError() {
                    organisatorImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
                }
            });
        } else {
            organisatorImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.event_link:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.getUrl()));
                startActivity(browserIntent);
                return true;
            case R.id.event_location:
                startActivity(getLocationIntent());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event, menu);

        // We need to manually set the color of this Drawable for some reason.
        tintToolbarIcons(menu, R.id.event_location, R.id.event_link);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Remove options if we don't have the data.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if(!event.hasUrl()) {
            menu.removeItem(R.id.event_link);
        }

        if(!event.hasPreciseLocation() && !event.hasLocation()) {
            menu.removeItem(R.id.event_location);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void sendScreen(HydraApplication application) {
        application.sendScreenName("Activity > " + event.getTitle());
    }

    /**
     * Get the intent for a location. If the precise location is available, that will be used. Otherwise, we just search
     * for the location. One location must be present.
     *
     * @return The intent.
     */
    private Intent getLocationIntent() {

        assert event.hasPreciseLocation() || event.hasLocation();

        Uri uriLocation;

        //If there is a precise location, use that.
        if(event.hasPreciseLocation()) {
            if(event.hasLocation()) {
                uriLocation = Uri.parse("geo:0,0?q=" + event.getLatitude() + "," + event.getLongitude() + "(" + event.getLocation() + ")");
            } else {
                uriLocation = Uri.parse("geo:0,0?q=" + event.getLatitude() + "," + event.getLongitude());
            }
        } else {
            uriLocation = Uri.parse("geo:" + GENT + "?q=" + event.getLocation());
        }


        Intent intent = new Intent(Intent.ACTION_VIEW, uriLocation);
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(getPackageManager()) == null) {
            Toast.makeText(getApplicationContext(), "Google Maps is niet geïnstalleerd.", Toast.LENGTH_LONG).show();
        }

        return intent;
    }

    /**
     * On click handler for the association.
     *
     * @param view The clicked view.
     */
    @Override
    public void onClick(View view) {

        Intent start = new Intent(this, AssociationDetailActivity.class);
        start.putExtra(AssociationDetailActivity.PARCEL_ASSOCIATION, (Parcelable) event.getAssociation());

        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(this, organisatorImage, "logo");

        ActivityCompat.startActivity(ActivityDetailActivity.this, start, options.toBundle());
    }
}