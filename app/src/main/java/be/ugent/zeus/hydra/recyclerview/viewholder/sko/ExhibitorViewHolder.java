package be.ugent.zeus.hydra.recyclerview.viewholder.sko;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.sko.Exhibitor;
import be.ugent.zeus.hydra.recyclerview.viewholder.DataViewHolder;
import be.ugent.zeus.hydra.utils.ViewUtils;
import com.squareup.picasso.Picasso;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
public class ExhibitorViewHolder extends DataViewHolder<Exhibitor> {

    private TextView name;
    private ImageView imageView;
    private TextView content;

    public ExhibitorViewHolder(View itemView) {
        super(itemView);
        name = $(itemView, R.id.name);
        imageView = $(itemView, R.id.logo);
        content = $(itemView, R.id.content);
    }

    @Override
    public void populate(Exhibitor data) {
        name.setText(data.getName());
        int size = ViewUtils.convertDpToPixelInt(80, itemView.getContext());
        Picasso.with(itemView.getContext()).load(data.getLogo()).resize(size, size).onlyScaleDown().centerInside().into(imageView);
        content.setText(data.getContent());
    }
}