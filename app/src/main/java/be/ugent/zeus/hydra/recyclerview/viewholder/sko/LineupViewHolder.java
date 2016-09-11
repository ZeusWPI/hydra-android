package be.ugent.zeus.hydra.recyclerview.viewholder.sko;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.sko.Artist;
import be.ugent.zeus.hydra.recyclerview.viewholder.DataViewHolder;
import be.ugent.zeus.hydra.utils.DateUtils;
import com.squareup.picasso.Picasso;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
public class LineupViewHolder extends DataViewHolder<Artist> {

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
        date.setText(DateUtils.getFriendlyDate(artist.getStart().toDate()));

        Picasso.with(this.itemView.getContext()).load(artist.getPicture()).into(image);
    }
}