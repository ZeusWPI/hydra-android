package be.ugent.zeus.hydra.recyclerview.viewholder.sko;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.sko.Artist;
import be.ugent.zeus.hydra.recyclerview.viewholder.DataViewHolder;
import com.squareup.picasso.Picasso;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Locale;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
public class LineupViewHolder extends DataViewHolder<Artist> {

    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM - HH:mm", new Locale("nl"));
    private static final DateTimeFormatter hourFormat = DateTimeFormatter.ofPattern("HH:mm", new Locale("nl"));

    private TextView title;
    private TextView date;
    private ImageView image;

    public LineupViewHolder(View itemView) {
        super(itemView);

        title = $(itemView, R.id.title);
        date = $(itemView, R.id.date);
        image = $(itemView, R.id.card_image);
    }

    public void populate(final Artist artist) {
        title.setText(artist.getName());
        date.setText(getDisplayDate(artist));
        Picasso.with(this.itemView.getContext()).load(artist.getPicture()).into(image);
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
    private String getDisplayDate(Artist artist) {

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
}