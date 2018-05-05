package be.ugent.zeus.hydra.minerva.calendar.upcominglist;

import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.minerva.calendar.AgendaItem;
import be.ugent.zeus.hydra.minerva.calendar.itemdetails.AgendaActivity;
import be.ugent.zeus.hydra.utils.DateUtils;

/**
 * @author Niko Strijbol
 */
class AgendaViewHolder extends DataViewHolder<AgendaItem> {

    private final TextView title;
    private final TextView subtitle;
    private final View parent;

    AgendaViewHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        parent = itemView.findViewById(R.id.parent_layout);
        subtitle = itemView.findViewById(R.id.subtitle);
    }

    @Override
    public void populate(AgendaItem data) {
        title.setText(data.getTitle());
        subtitle.setText(DateUtils.relativeTimeSpan(itemView.getContext(), data.getStartDate(), data.getEndDate()));
        parent.setOnClickListener(view -> AgendaActivity.start(view.getContext(), data));
    }
}