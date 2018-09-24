package be.ugent.zeus.hydra.sko.lineup;

import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;

/**
 * View holder for a title.
 *
 * This class implements the base class with {@link ArtistOrTitle}, since it would be a lot of work to allow us to
 * have heterogeneous view holders in the adapters for little gain.
 *
 * @author Niko Strijbol
 */
class TitleViewHolder extends DataViewHolder<ArtistOrTitle> {

    private final TextView title;

    TitleViewHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.text_header);
    }

    @Override
    public void populate(ArtistOrTitle data) {
        this.title.setText(data.getTitle());
    }
}