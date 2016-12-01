package be.ugent.zeus.hydra.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.text.util.LinkifyCompat;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.HydraActivity;
import be.ugent.zeus.hydra.models.association.Event;
import be.ugent.zeus.hydra.utils.NetworkUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import org.threeten.bp.format.DateTimeFormatter;

/**
 * Activity to show details of an association's event.
 *
 * @author Niko Strijbol
 */
public class EventDetailActivity extends HydraActivity {

    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static final String PARCEL_EVENT = "eventParcelable";

    private static final DateTimeFormatter formatHour = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter fullFormatter = DateTimeFormatter.ofPattern("E d MMM H:mm");
    private static final String GENT = "51.05,3.72";

    //The data
    private Event event;

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
        final ImageView organisatorImage = $(R.id.event_organisator_image);
        TextView mainName = $(R.id.event_organisator_main);
        TextView smallName = $(R.id.event_organisator_small);

        if(event.getTitle() != null){
            title.setText(event.getTitle());
            getToolbar().setTitle(event.getTitle());
        }

        if(event.getAssociation() != null ) {
            mainName.setText(event.getAssociation().getDisplayName());
            smallName.setText(event.getAssociation().getFullName());
        }

        if(event.getDescription() != null && !event.getDescription().trim().isEmpty()) {
            description.setText(event.getDescription());
            LinkifyCompat.addLinks(description, Linkify.ALL);
        }

        if(event.hasLocation()) {
            location.setText(event.getLocation());
        } else {
            location.setText("Zonder locatie");
        }

        TextView startTime = $(R.id.time_start);
        TextView endTime = $(R.id.time_end);

        startTime.setText(event.getStart().format(format));
        endTime.setText(event.getEnd().format(format));

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
                NetworkUtils.maybeLaunchBrowser(this, event.getUrl());
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
    protected String getScreenName() {
        return "Event > " + event.getTitle();
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
}