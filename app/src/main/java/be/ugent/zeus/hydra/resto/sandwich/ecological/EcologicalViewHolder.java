package be.ugent.zeus.hydra.resto.sandwich.ecological;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.content.res.AppCompatResources;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.MultiSelectAdapter;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.common.utils.DateUtils;
import net.cachapa.expandablelayout.ExpandableLayout;

/**
 * @author Niko Strijbol
 */
class EcologicalViewHolder extends DataViewHolder<EcologicalSandwich> {

    private final TextView name;
    private final TextView dates;
    private final ExpandableLayout expandableLayout;
    private final TextView ingredients;
    private final MultiSelectAdapter<EcologicalSandwich> adapter;

    EcologicalViewHolder(View itemView, MultiSelectAdapter<EcologicalSandwich> adapter) {
        super(itemView);

        name = itemView.findViewById(R.id.name);
        dates = itemView.findViewById(R.id.dates);
        expandableLayout = itemView.findViewById(R.id.expandable_layout);
        ingredients = itemView.findViewById(R.id.sandwich_ingredients);
        this.adapter = adapter;
    }

    @Override
    public void populate(EcologicalSandwich sandwich) {
        Context c = itemView.getContext();
        name.setText(sandwich.getName());
        String start = DateUtils.getFriendlyDate(c, sandwich.getStart());
        String end = DateUtils.getFriendlyDate(c, sandwich.getEnd());
        if (sandwich.isVegan()) {
            Drawable image = AppCompatResources.getDrawable(c, R.drawable.resto_vegan);
            name.setCompoundDrawablesWithIntrinsicBounds(null, null, image, null);
        } else {
            name.setCompoundDrawables(null, null, null, null);
        }
        dates.setText(String.format(c.getString(R.string.date_between), start, end));
        String ingredientsString = TextUtils.join(", ", sandwich.getIngredients());
        ingredients.setText(String.format(c.getString(R.string.resto_sandwich_ingredients), ingredientsString));
        expandableLayout.setExpanded(adapter.isChecked(getAdapterPosition()), false);
        itemView.setOnClickListener(v -> {
            adapter.setChecked(getAdapterPosition());
            expandableLayout.toggle();
        });
    }
}
