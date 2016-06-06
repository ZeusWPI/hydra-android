package be.ugent.zeus.hydra.recyclerviewholder.home;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.Hydra;
import be.ugent.zeus.hydra.fragments.resto.MenuFragment;
import be.ugent.zeus.hydra.models.CardModel;
import be.ugent.zeus.hydra.models.resto.RestoMenu;
import be.ugent.zeus.hydra.utils.DateUtils;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

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
    public void populate(CardModel card) {
        if (card.getCardType() != CardModel.CardType.RESTO) {
            return; //TODO: report errors
        }

        RestoMenu menu = (RestoMenu) card;

        title.setText(DateUtils.getFriendlyDate(menu.getDate()));

        if (menu.isOpen()) {
            populateOpen(menu);
        } else {
            populateClosed();
        }

        // click listener
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: open resto fragment
                if ( v.getContext() instanceof Hydra) {
                    Hydra activity = (Hydra) v.getContext();
                    activity.changeFragment(2); // TODO: replace this by more robust way!
                }
            }
        });
    }

    private void populateOpen(RestoMenu menu) {
        LinearLayout container = $(view, R.id.home_cards_resto_container);
        container.removeAllViews();
        container.addView(title);
        container.addView(MenuFragment.makeTableDishes(container, menu.getMainDishes()));
    }

    private void populateClosed() {
        LinearLayout container = $(view, R.id.home_cards_resto_container);
        container.removeAllViews();

        LinearLayout.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);

        TextView textView = new TextView(view.getContext());
        textView.setPadding(25, 0, 0, 0);
        textView.setLayoutParams(lp);
        textView.setText("sorry, gesloten");
        textView.setTextColor(Color.parseColor("#122b44"));
        textView.setTextSize(25);
        textView.setGravity(Gravity.CENTER);

        container.removeAllViews();
        container.addView(title);
        container.addView(textView);
    }
}
