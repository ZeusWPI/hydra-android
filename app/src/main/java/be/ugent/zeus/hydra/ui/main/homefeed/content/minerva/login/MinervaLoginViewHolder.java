package be.ugent.zeus.hydra.ui.main.homefeed.content.minerva.login;

import android.view.View;
import android.widget.Toast;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.main.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DataViewHolder;

/**
 * Created by feliciaan on 29/06/16.
 */
public class MinervaLoginViewHolder extends DataViewHolder<HomeCard> {

    public MinervaLoginViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void populate(HomeCard card) {
        this.itemView.setOnClickListener(view -> Toast.makeText(view.getContext(), R.string.not_done_yet, Toast.LENGTH_LONG).show());
    }
}