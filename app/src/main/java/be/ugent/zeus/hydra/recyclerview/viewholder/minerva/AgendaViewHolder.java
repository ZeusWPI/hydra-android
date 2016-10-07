package be.ugent.zeus.hydra.recyclerview.viewholder.minerva;

import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.minerva.AgendaActivity;
import be.ugent.zeus.hydra.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.recyclerview.viewholder.DataViewHolder;
import be.ugent.zeus.hydra.utils.DateUtils;

import java.util.Locale;

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
        String infoText = String.format(new Locale("nl"), "%s door %s",
                DateUtils.relativeDateTimeString(data.getStartDate(), itemView.getContext()),
                data.getLastEditUser());
        subtitle.setText(infoText);

        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AgendaActivity.start(itemView.getContext(), data);
            }
        });
    }
}
