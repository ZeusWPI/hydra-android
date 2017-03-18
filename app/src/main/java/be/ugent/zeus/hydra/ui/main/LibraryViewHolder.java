package be.ugent.zeus.hydra.ui.main;

import android.view.View;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.LibraryDetailActivity;
import be.ugent.zeus.hydra.data.models.library.Library;
import be.ugent.zeus.hydra.ui.common.recyclerview.DataViewHolder;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
class LibraryViewHolder extends DataViewHolder<Library> {

    private TextView title;
    private TextView subtitle;

    LibraryViewHolder(View itemView) {
        super(itemView);
        title = $(itemView, R.id.title);
        subtitle = $(itemView, R.id.subtitle);
    }

    @Override
    public void populate(Library data) {
        title.setText(data.getName());
        subtitle.setText(data.getCampus());

        itemView.setOnClickListener(v -> LibraryDetailActivity.launchActivity(v.getContext(), data));
    }
}