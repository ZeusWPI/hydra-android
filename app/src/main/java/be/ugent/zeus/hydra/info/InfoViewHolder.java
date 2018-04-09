package be.ugent.zeus.hydra.info;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.ViewUtils;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;

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
        Context c = itemView.getContext();
        title.setText(infoItem.getTitle());
        itemView.setOnClickListener(v -> infoItem.getType().doOnClick(v.getContext(), infoItem));

        // Get the primary colour of the app.
        TypedValue typedValue = new TypedValue();
        c.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);

        int color = ContextCompat.getColor(c, typedValue.resourceId);
        Drawable more = infoItem.getType().getDrawable(itemView.getContext(), typedValue.resourceId);

        //If the item itself has an image.
        if (infoItem.getImage() != null) {
            int resId = c.getResources().getIdentifier(infoItem.getImage(), "drawable", c.getPackageName());
            Drawable icon = ViewUtils.getTintedVectorDrawable(c, resId, typedValue.resourceId);
            title.setCompoundDrawablesWithIntrinsicBounds(icon, null, more, null);
        } else {
            title.setCompoundDrawablesWithIntrinsicBounds(null, null, more, null);
        }
    }
}