package be.ugent.zeus.hydra.ui.main.library;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.library.LibraryDetailActivity;
import be.ugent.zeus.hydra.data.models.library.Library;
import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DataViewHolder;

/**
 * @author Niko Strijbol
 */
class LibraryViewHolder extends DataViewHolder<Library> {

    private final TextView title;
    private final TextView subtitle;
    private final ImageView favourite;

    LibraryViewHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        subtitle = itemView.findViewById(R.id.subtitle);
        favourite = itemView.findViewById(R.id.library_favourite_image);
    }

    @Override
    public void populate(Library data) {
        title.setText(data.getName());
        subtitle.setText(data.getCampus());
        itemView.setOnClickListener(v -> LibraryDetailActivity.launchActivity(v.getContext(), data));
        favourite.setVisibility(data.isFavourite() ? View.VISIBLE : View.GONE);
    }
}