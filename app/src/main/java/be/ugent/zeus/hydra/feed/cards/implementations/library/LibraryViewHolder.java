package be.ugent.zeus.hydra.feed.cards.implementations.library;

import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java9.util.Optional;

import be.ugent.zeus.hydra.MainActivity;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.common.ui.ViewUtils;
import be.ugent.zeus.hydra.feed.HomeFeedAdapter;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.CardViewHolder;
import be.ugent.zeus.hydra.feed.commands.DisableTypeCommand;
import be.ugent.zeus.hydra.library.details.OpeningHours;

/**
 * View holder for the list of library opening hours.
 *
 * @author Niko Strijbol
 */
public class LibraryViewHolder extends CardViewHolder {

    private final LinearLayout list;

    private final int hoursTextColour;
    private final float hoursTextSize;
    private final int rowPadding;

    public LibraryViewHolder(View itemView, HomeFeedAdapter adapter) {
        super(itemView, adapter);
        list = itemView.findViewById(R.id.library_list);

        Context c = itemView.getContext();
        hoursTextColour = ViewUtils.getColor(c, android.R.attr.textColorSecondary);
        hoursTextSize = c.getResources().getDimension(R.dimen.material_list_normal_secondary_text_size);
        rowPadding = c.getResources().getDimensionPixelSize(R.dimen.material_baseline_grid_1x);

        itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), MainActivity.class);
            intent.putExtra(MainActivity.ARG_TAB, R.id.drawer_library);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public void populate(Card card) {
        super.populate(card);

        // Clear existing items that might still be in our list.
        list.removeAllViews();

        // Add the new items for the current card.
        LibraryCard libraryCard = card.checkCard(Card.Type.LIBRARY);
        int size = libraryCard.getLibraries().size();
        Context c = list.getContext();
        for (int i = 0; i < size; i++) {
            Pair<String, Result<Optional<OpeningHours>>> pair = libraryCard.getLibraries().get(i);
            addLibraryName(c, pair.first);
            addOpeningHour(c, pair.second, i == size - 1);
        }
    }

    private void addLibraryName(Context context, String name) {
        TextView v = new TextView(context);
        TableRow.LayoutParams textParam =
                new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), rowPadding / 2);
        v.setLayoutParams(textParam);
        v.setText(name);

        list.addView(v);
    }

    private void addOpeningHour(Context context, Result<Optional<OpeningHours>> result, boolean isLast) {
        TextView v = new TextView(context);
        TableRow.LayoutParams textParam =
                new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        if (!isLast) {
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), rowPadding);
        }
        v.setLayoutParams(textParam);
        v.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.hoursTextSize);
        v.setTextColor(this.hoursTextColour);

        if (!result.hasData() || !result.getData().isPresent()) {
            v.setText(R.string.library_list_no_opening_hours);
        } else {
            v.setText(context.getString(R.string.library_list_opening_hours_today, result.getData().get().getHours()));
        }

        list.addView(v);
    }

    @Override
    public void onSwiped() {
        adapter.getCompanion().executeCommand(new DisableTypeCommand(Card.Type.LIBRARY));
    }

    @Override
    public boolean isSwipeEnabled() {
        return true;
    }
}