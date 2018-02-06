package be.ugent.zeus.hydra.ui.main.info;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.ViewUtils;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.info.InfoItem;

/**
 * View holder for info items.
 *
 * @author Niko Strijbol
 */
class InfoViewHolder extends DataViewHolder<InfoItem> {

    private TextView title;

    InfoViewHolder(View v) {
        super(v);
        title = v.findViewById(R.id.info_name);
    }

    @Override
    public void populate(final InfoItem infoItem) {

        title.setText(infoItem.getTitle());
        itemView.setOnClickListener(v -> infoItem.getType().doOnClick(v.getContext(), infoItem));

        int color = R.color.ugent_blue_dark;
        Context c = itemView.getContext();
        Drawable more = infoItem.getType().getDrawable(c, color);

        //If the item itself has an image.
        if (infoItem.getImage() != null) {
            int resId = c.getResources().getIdentifier(infoItem.getImage(), "drawable", c.getPackageName());
            Drawable icon = ViewUtils.getTintedVectorDrawable(c, resId, color);
            title.setCompoundDrawablesWithIntrinsicBounds(icon, null, more, null);
        } else {
            title.setCompoundDrawablesWithIntrinsicBounds(null, null, more, null);
        }
    }
}