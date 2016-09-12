package be.ugent.zeus.hydra.recyclerview.viewholder.sko;

import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.sko.Exhibitor;
import be.ugent.zeus.hydra.recyclerview.viewholder.DataViewHolder;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
public class ExhibitorViewHolder extends DataViewHolder<Exhibitor> {

    private TextView name;

    public ExhibitorViewHolder(View itemView) {
        super(itemView);
        name = $(itemView, R.id.exhibitor_name);
    }

    @Override
    public void populate(Exhibitor data) {
        name.setText(data.getName());
    }
}