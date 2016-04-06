package be.ugent.zeus.hydra.recyclerviewholder.home;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.models.HomeCard;
import be.ugent.zeus.hydra.models.resto.RestoMeal;
import be.ugent.zeus.hydra.models.resto.RestoMenu;

/**
 * Created by feliciaan on 06/04/16.
 */
public class RestoCardViewHolder extends AbstractViewHolder {
    private TextView title;
    private View view;

    public RestoCardViewHolder(View v) {
        super(v);
        title = (TextView) v.findViewById(R.id.category_text);

        view = v;

    }

    @Override
    public void populate(HomeCard card) {
        if (card.getCardType() != HomeCardAdapter.HomeType.RESTO) {
            return; //TODO: report errors
        }

        RestoMenu menu = (RestoMenu) card;

        TableLayout tl = (TableLayout) view.findViewById(R.id.cardTableLayout);
        tl.removeAllViews();

        title.setText(menu.getDate().toLocaleString());

        for (RestoMeal meal: menu.getMeals()) {
            TableRow tr = new TableRow(view.getContext());
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            tr.setLayoutParams(lp);

            // set correct image according to kind
            ImageView imageView = new ImageView(view.getContext());
            switch (meal.getKind()) {
                case "soup":
                    imageView.setImageResource(R.drawable.soep);
                    break;
                case "meat":
                    imageView.setImageResource(R.drawable.vlees);
                    break;
                case "fish":
                    imageView.setImageResource(R.drawable.vis);
                    break;
                case "vegetarian":
                    imageView.setImageResource(R.drawable.vegi);
                    break;
                default:
                    imageView.setImageResource(R.drawable.soep);
            }

            TextView tvCenter = new TextView(view.getContext());
            tvCenter.setLayoutParams(lp);
            tvCenter.setText(meal.getName());
            tvCenter.setTextColor(Color.parseColor("#122b44"));
            TextView tvRight = new TextView(view.getContext());
            tvRight.setLayoutParams(lp);
            tvRight.setText(meal.getPrice());
            tvRight.setTextColor(Color.parseColor("#122b44"));

            tr.addView(imageView);
            tr.addView(tvCenter);
            tr.addView(tvRight);

            tl.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        }


    }
}
