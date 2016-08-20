package be.ugent.zeus.hydra.recyclerview.viewholder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.info.InfoItem;
import be.ugent.zeus.hydra.utils.ViewUtils;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * View holder for info items.
 *
 * @author Niko Strijbol
 */
public class InfoViewHolder extends DataViewHolder<InfoItem> {

    private TextView title;

    public InfoViewHolder(View v) {
        super(v);
        title = $(v, R.id.info_name);
    }

    @Override
    public void populate(final InfoItem infoItem) {

        title.setText(infoItem.getTitle());

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoItem.getType().doOnClick(v.getContext(), infoItem);
            }
        });

        int color = R.color.ugent_blue_dark;
        Context c = itemView.getContext();
        Drawable more = infoItem.getType().getDrawable(c, color);

        //If the item itself has an image.
        if (infoItem.getImage() != null) {
            int resId = c.getResources().getIdentifier("ic_" + infoItem.getImage(), "drawable", itemView.getContext().getPackageName());
            Drawable icon = ViewUtils.getTintedVectorDrawable(c, resId, color);
            title.setCompoundDrawablesWithIntrinsicBounds(icon, null, more, null);
        } else {
            title.setCompoundDrawablesWithIntrinsicBounds(null, null, more, null);
        }
    }
}