package be.ugent.zeus.hydra.feed.cards.implementations.urgent;

import android.content.Intent;
import android.view.View;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.CardViewHolder;
import be.ugent.zeus.hydra.common.main.MainActivity;
import be.ugent.zeus.hydra.feed.HomeFeedAdapter;
import be.ugent.zeus.hydra.feed.commands.DisableTypeCommand;

/**
 * @author Niko Strijbol
 */
public class UrgentViewHolder extends CardViewHolder {

    public UrgentViewHolder(View itemView, HomeFeedAdapter adapter) {
        super(itemView, adapter);

        itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), MainActivity.class);
            intent.putExtra(MainActivity.ARG_TAB, R.id.drawer_urgent);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public void onSwiped() {
        adapter.getCompanion().executeCommand(new DisableTypeCommand(Card.Type.URGENT_FM));
    }

    @Override
    public boolean isSwipeEnabled() {
        return true;
    }
}