package be.ugent.zeus.hydra.ui.minerva.overview;

import android.view.View;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.minerva.AgendaActivity;
import be.ugent.zeus.hydra.data.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.ui.common.recyclerview.DataViewHolder;
import be.ugent.zeus.hydra.utils.DateUtils;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
public class AgendaViewHolder extends DataViewHolder<AgendaItem> {

    private TextView title;
    private TextView subtitle;
    private View parent;

    public AgendaViewHolder(View itemView) {
        super(itemView);
        title = $(itemView, R.id.title);
        parent = $(itemView, R.id.parent_layout);
        subtitle = $(itemView, R.id.subtitle);
    }

    @Override
    public void populate(final AgendaItem data) {
        title.setText(data.getTitle());
        subtitle.setText(DateUtils.relativeTimeSpan(itemView.getContext(), data.getStartDate(), data.getEndDate()));
        parent.setOnClickListener(view -> AgendaActivity.start(view.getContext(), data));
    }
}