package be.ugent.zeus.hydra.feed.cards.implementations.minerva.login;

import android.view.View;
import android.widget.Toast;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.HomeFeedAdapter;
import be.ugent.zeus.hydra.feed.SwipeDismissableViewHolder;
import be.ugent.zeus.hydra.feed.commands.DisableTypeCommand;

/**
 * Created by feliciaan on 29/06/16.
 */
public class MinervaLoginViewHolder extends DataViewHolder<Card> implements SwipeDismissableViewHolder {

    private final HomeFeedAdapter.AdapterCompanion companion;

    public MinervaLoginViewHolder(View itemView, HomeFeedAdapter.AdapterCompanion companion) {
        super(itemView);
        this.companion = companion;
    }

    @Override
    public void populate(Card card) {
        this.itemView.setOnClickListener(view -> Toast.makeText(view.getContext(), R.string.not_done_yet, Toast.LENGTH_LONG).show());
    }

    @Override
    public void onSwiped() {
        companion.executeCommand(new DisableTypeCommand(Card.Type.MINERVA_LOGIN));
    }

    @Override
    public boolean isSwipeEnabled() {
        return true;
    }
}