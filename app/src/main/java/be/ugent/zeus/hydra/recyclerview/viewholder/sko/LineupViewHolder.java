package be.ugent.zeus.hydra.recyclerview.viewholder.sko;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.provider.CalendarContract;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.sko.ArtistActivity;
import be.ugent.zeus.hydra.models.sko.Artist;
import be.ugent.zeus.hydra.recyclerview.viewholder.DataViewHolder;
import com.squareup.picasso.Picasso;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Locale;

import static be.ugent.zeus.hydra.activities.sko.ArtistActivity.PARCEL_ARTIST;
import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
public class LineupViewHolder extends DataViewHolder<Artist> implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM - HH:mm", new Locale("nl"));
    private static final DateTimeFormatter hourFormat = DateTimeFormatter.ofPattern("HH:mm", new Locale("nl"));

    private static final int MENU_ID_ADD_TO_CALENDAR = 0;
    private static final String LOCATION = "Sint-Pietersplein, Gent";

    private TextView title;
    private TextView date;
    private ImageView image;
    private Artist artist;

    public LineupViewHolder(View itemView) {
        super(itemView);

        title = $(itemView, R.id.title);
        date = $(itemView, R.id.date);
        image = $(itemView, R.id.card_image);

        itemView.setOnCreateContextMenuListener(this);
    }

    public void populate(final Artist artist) {
        this.artist = artist;
        title.setText(artist.getName());
        date.setText(getDisplayDate(artist));
        Picasso.with(this.itemView.getContext()).load(artist.getBanner()).into(image);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ArtistActivity.class);
                intent.putExtra(PARCEL_ARTIST, (Parcelable) artist);
                v.getContext().startActivity(intent);
            }
        });
    }

    /**
     * Get the display date. The resulting string is of the following format:
     *
     * dd/MM HH:mm tot [dd/MM] HH:mm
     *
     * The second date is only shown of it is not the same date as the first.
     *
     * @param artist The artist.
     *
     * @return The text to display.
     */
    public static String getDisplayDate(Artist artist) {

        LocalDateTime start = artist.getLocalStart();
        LocalDateTime end = artist.getLocalEnd();

        //Add the start date and time
        String date = start.format(format) + " tot ";

        if(start.getDayOfMonth() == end.getDayOfMonth()) {
            date += end.format(hourFormat);
        } else {
            date +=  end.format(format);
        }

        return date;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if(artist != null) {
            menu.add(Menu.NONE, MENU_ID_ADD_TO_CALENDAR, 0, "Toevoegen aan agenda").setOnMenuItemClickListener(this);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        if(artist != null && item.getItemId() == MENU_ID_ADD_TO_CALENDAR) {
            addToCalendar(title.getContext(), artist);
            return true;
        }

        return false;
    }

    public static void addToCalendar(Context context, Artist artist) {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, artist.getStart().toInstant().toEpochMilli())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, artist.getEnd().toInstant().toEpochMilli())
                .putExtra(CalendarContract.Events.TITLE, artist.getName())
                .putExtra(CalendarContract.Events.EVENT_LOCATION, LOCATION)
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);

        context.startActivity(intent);
    }
}