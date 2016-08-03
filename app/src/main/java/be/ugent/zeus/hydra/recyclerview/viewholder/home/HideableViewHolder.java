package be.ugent.zeus.hydra.recyclerview.viewholder.home;

import android.support.annotation.CallSuper;
import android.view.View;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.recyclerview.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.recyclerview.viewholder.AbstractViewHolder;
import be.ugent.zeus.hydra.views.NowToolbar;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * View holder for cards that have the Now-like toolbar.
 *
 * @author Niko Strijbol
 */
public abstract class HideableViewHolder extends AbstractViewHolder<HomeCard> {

    protected HomeCardAdapter adapter;
    protected NowToolbar toolbar;

    public HideableViewHolder(View itemView, HomeCardAdapter adapter) {
        super(itemView);
        this.adapter = adapter;
        toolbar = $(itemView, R.id.card_now_toolbar);
    }

    @Override
    @CallSuper
    public void populate(HomeCard card) {
        toolbar.setOnClickListener(adapter.listener(card.getCardType()));
    }
}