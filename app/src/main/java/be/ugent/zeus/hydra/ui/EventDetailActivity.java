package be.ugent.zeus.hydra.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.text.util.LinkifyCompat;
import android.text.util.Linkify;
import android.transition.Fade;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.association.Event;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.utils.NetworkUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import org.threeten.bp.format.DateTimeFormatter;

/**
 * Activity to show details of an association's event.
 *
 * @author Niko Strijbol
 */
public class EventDetailActivity extends BaseActivity {

    public static final String PARCEL_EVENT = "eventParcelable";
    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String GENT = "51.05,3.72";

    private Event event;

    /**
     * Launch this activity with a transition.
     *
     * @param activity The activity that launches the intent.
     * @param view     The view to transition.
     * @param name     The name of the transition.
     * @param event    The event.
     */
    public static void launchWithAnimation(Activity activity, View view, String name, Parcelable event) {
        Intent intent = new Intent(activity, EventDetailActivity.class);
        intent.putExtra(PARCEL_EVENT, event);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, name);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        customFade();

        //Get data from saved instance, or from intent
        event = getIntent().getParcelableExtra(PARCEL_EVENT);

        TextView title = findViewById(R.id.title);
        TextView location = findViewById(R.id.location);
        TextView description = findViewById(R.id.description);
        final ImageView organisatorImage = findViewById(R.id.event_organisator_image);
        TextView mainName = findViewById(R.id.event_organisator_main);
        TextView smallName = findViewById(R.id.event_organisator_small);
        if (event.getTitle() != null) {
            title.setText(event.getTitle());
            getToolbar().setTitle(event.getTitle());
        }

        if (event.getAssociation() != null) {
            mainName.setText(event.getAssociation().getDisplayName());
            smallName.setText(event.getAssociation().getFullName());
        }

        if (event.getDescription() != null && !event.getDescription().trim().isEmpty()) {
            description.setText(event.getDescription());
            LinkifyCompat.addLinks(description, Linkify.ALL);
        }

        if (event.hasPreciseLocation() || event.hasLocation()) {
            if (event.hasLocation()) {
                location.setText(event.getLocation());
            } else {
                location.setText(getString(R.string.events_unnamed_precise_location, event.getLatitude(), event.getLongitude()));
            }
            // Make location clickable
            findViewById(R.id.location_row).setOnClickListener(view -> NetworkUtils.maybeLaunchIntent(this, getLocationIntent()));
        } else {
            location.setText(R.string.events_no_location);
        }

        TextView startTime = findViewById(R.id.time_start);
        TextView endTime = findViewById(R.id.time_end);

        startTime.setText(event.getLocalStart().format(format));
        endTime.setText(event.getLocalEnd().format(format));

        if (event.getAssociation() != null && event.getAssociation().getImageLink() != null) {
            Picasso.with(this).load(event.getAssociation().getImageLink()).into(organisatorImage, new Callback.EmptyCallback() {
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
            //Up button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
            case R.id.event_link:
                NetworkUtils.maybeLaunchBrowser(this, event.getUrl());
                return true;
            case R.id.event_location:
                NetworkUtils.maybeLaunchIntent(this, getLocationIntent());
                return true;
            case R.id.menu_event_add_to_calendar:
                addToCalendar();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event, menu);

        // We need to manually set the color of this Drawable for some reason.
        tintToolbarIcons(menu, R.id.event_location, R.id.event_link, R.id.menu_event_add_to_calendar);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Add the event to an intent, for adding to the calendar.
     */
    private void addToCalendar() {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.getStart().toInstant().toEpochMilli())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.getEnd().toInstant().toEpochMilli())
                .putExtra(CalendarContract.Events.TITLE, event.getTitle())
                .putExtra(CalendarContract.Events.EVENT_LOCATION, event.getLocation())
                .putExtra(CalendarContract.Events.DESCRIPTION, event.getDescription())
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_TENTATIVE);

        NetworkUtils.maybeLaunchIntent(this, intent);
    }

    /**
     * Remove options if we don't have the data.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (!event.hasUrl()) {
            menu.removeItem(R.id.event_link);
        }

        if (!(event.hasPreciseLocation() || event.hasLocation())) {
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

        Uri uriLocation;

        //If there is a precise location, use that.
        if (event.hasPreciseLocation()) {
            if (event.hasLocation()) {
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
     * Set a custom fade when using transition to prevent white flashing/blinking. This excludes the status bar and
     * navigation bar background from the animation.
     */
    private void customFade() {
        //Only do it on a version that is high enough.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition fade = new Fade();
            fade.excludeTarget(android.R.id.statusBarBackground, true);
            fade.excludeTarget(android.R.id.navigationBarBackground, true);
            getWindow().setExitTransition(fade);
            getWindow().setEnterTransition(fade);
        }
    }
}