package be.ugent.zeus.hydra.sko;

import androidx.annotation.NonNull;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.utils.ViewUtils;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.DiffAdapter;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;

/**
 * @author Niko Strijbol
 */
class LineupAdapter extends DiffAdapter<ArtistOrTitle, DataViewHolder<ArtistOrTitle>> {

    private static final int VIEW_TYPE_ARTIST = 0;
    private static final int VIEW_TYPE_TITLE = 1;

    @NonNull
    @Override
    public DataViewHolder<ArtistOrTitle> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ARTIST:
                return new ArtistViewHolder(ViewUtils.inflate(parent, R.layout.item_sko_lineup_artist));
            case VIEW_TYPE_TITLE:
                return new TitleViewHolder(ViewUtils.inflate(parent, R.layout.item_title));
            default:
                throw new IllegalStateException("Unknown view type.");
        }

    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).isArtist() ? VIEW_TYPE_ARTIST : VIEW_TYPE_TITLE;
    }
}
