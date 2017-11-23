package be.ugent.zeus.hydra.ui.minerva.overview;

import android.view.View;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.minerva.AgendaActivity;
import be.ugent.zeus.hydra.domain.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.utils.DateUtils;

/**
 * @author Niko Strijbol
 */
class AgendaViewHolder extends DataViewHolder<AgendaItem> {

    private TextView title;
    private TextView subtitle;
    private View parent;

    AgendaViewHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        parent = itemView.findViewById(R.id.parent_layout);
        subtitle = itemView.findViewById(R.id.subtitle);
    }

    @Override
    public void populate(final AgendaItem data) {
        title.setText(data.getTitle());
        subtitle.setText(DateUtils.relativeTimeSpan(itemView.getContext(), data.getStartDate(), data.getEndDate()));
        parent.setOnClickListener(view -> AgendaActivity.start(view.getContext(), data));
    }
}