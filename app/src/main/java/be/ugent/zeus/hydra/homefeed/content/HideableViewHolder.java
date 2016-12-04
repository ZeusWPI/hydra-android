package be.ugent.zeus.hydra.homefeed.content;

import android.view.View;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.homefeed.HomeFeedAdapter;
import be.ugent.zeus.hydra.recyclerview.viewholder.DataViewHolder;
import be.ugent.zeus.hydra.views.NowToolbar;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * View holder for cards that are hideable, using the {@link NowToolbar}.
 *
 * @author Niko Strijbol
 */
public abstract class HideableViewHolder extends DataViewHolder<HomeCard> {

    protected HomeFeedAdapter adapter;
    protected NowToolbar toolbar;

    public HideableViewHolder(View itemView, HomeFeedAdapter adapter) {
        super(itemView);
        this.adapter = adapter;
        toolbar = $(itemView, R.id.card_now_toolbar);
    }

    @Override
    public void populate(HomeCard card) {
        toolbar.setOnClickListener(adapter.listener(card.getCardType()));
    }
}