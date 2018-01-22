package be.ugent.zeus.hydra.sko.lineup;

import android.content.Intent;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DataViewHolder;
import com.squareup.picasso.Picasso;

/**
 * @author Niko Strijbol
 */
class LineupViewHolder extends DataViewHolder<Artist> implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

    private static final int MENU_ID_ADD_TO_CALENDAR = 0;

    private TextView title;
    private TextView date;
    private ImageView image;
    private Artist artist;

    LineupViewHolder(View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.title);
        date = itemView.findViewById(R.id.date);
        image = itemView.findViewById(R.id.card_image);

        itemView.setOnCreateContextMenuListener(this);
    }

    public void populate(final Artist artist) {
        this.artist = artist;
        title.setText(artist.getName());
        date.setText(artist.getDisplayDate(date.getContext()));
        Picasso.with(this.itemView.getContext()).load(artist.getBanner()).into(image);
        itemView.setOnClickListener(v -> {
            Intent intent = ArtistDetailsActivity.start(v.getContext(), artist);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (artist != null) {
            String string = itemView.getContext().getString(R.string.action_add_to_menu);
            menu.add(Menu.NONE, MENU_ID_ADD_TO_CALENDAR, 0, string).setOnMenuItemClickListener(this);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        if (artist != null && item.getItemId() == MENU_ID_ADD_TO_CALENDAR) {
            Intent calendarIntent = artist.addToCalendarIntent();
            title.getContext().startActivity(calendarIntent);
            return true;
        }

        return false;
    }
}