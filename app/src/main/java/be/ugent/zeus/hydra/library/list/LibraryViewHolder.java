package be.ugent.zeus.hydra.library.list;

import androidx.lifecycle.Observer;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.RecycleViewHolder;
import be.ugent.zeus.hydra.library.Library;
import be.ugent.zeus.hydra.library.details.LibraryDetailActivity;
import be.ugent.zeus.hydra.library.details.OpeningHours;
import java9.util.Optional;

/**
 * @author Niko Strijbol
 */
class LibraryViewHolder extends DataViewHolder<Library> implements RecycleViewHolder, Observer<Result<Optional<OpeningHours>>> {

    private final LibraryListAdapter adapter;

    private final TextView title;
    private final TextView subtitle;
    private final ImageView favourite;
    private final TextView openingHours;

    private boolean visible;

    LibraryViewHolder(View itemView, LibraryListAdapter adapter) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        subtitle = itemView.findViewById(R.id.subtitle);
        favourite = itemView.findViewById(R.id.library_favourite_image);
        openingHours = itemView.findViewById(R.id.opening_hours);
        this.adapter = adapter;
    }

    @Override
    public void populate(Library data) {
        visible = data.isFavourite();
        openingHours.setVisibility(visible ? View.VISIBLE : View.GONE);
        title.setText(data.getName());
        subtitle.setText(data.getCampus());
        itemView.setOnClickListener(v -> LibraryDetailActivity.launchActivity(v.getContext(), data));
        favourite.setVisibility(data.isFavourite() ? View.VISIBLE : View.GONE);
        adapter.registerListener(data, this);
    }

    @Override
    public void onViewRecycled() {
        visible = false;
        adapter.unregisterListener(this);
    }

    @Override
    public void onChanged(@Nullable Result<Optional<OpeningHours>> result) {
        if (result == null || !result.hasData() || !result.getData().isPresent()) {
            openingHours.setText(R.string.library_list_no_opening_hours);
        } else {
            OpeningHours hours = result.getData().get();
            if (visible) {
                openingHours.setText(openingHours.getContext().getString(R.string.library_list_opening_hours_today, hours.getHours()));
            }
        }
    }
}