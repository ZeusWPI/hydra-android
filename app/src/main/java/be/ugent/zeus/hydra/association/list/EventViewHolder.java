/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.ugent.zeus.hydra.association.list;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.time.format.DateTimeFormatter;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.association.common.AssociationMap;
import be.ugent.zeus.hydra.association.common.EventItem;
import be.ugent.zeus.hydra.association.event.Event;
import be.ugent.zeus.hydra.association.event.EventDetailsActivity;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.common.utils.ViewUtils;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.shape.RoundedCornerTreatment;
import com.google.android.material.shape.ShapeAppearanceModel;

/**
 * View holder for an event in the event tab.
 *
 * @author Niko Strijbol
 */
class EventViewHolder extends DataViewHolder<EventItem> {

    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final TextView start;
    private final TextView title;
    private final TextView association;
    private final MaterialCardView cardView;
    private final View divider;
    private final AssociationMap map;

    EventViewHolder(View v, AssociationMap map) {
        super(v);
        title = v.findViewById(R.id.name);
        association = v.findViewById(R.id.association);
        start = v.findViewById(R.id.starttime);
        cardView = v.findViewById(R.id.card_view);
        divider = v.findViewById(R.id.item_event_divider);
        this.map = map;
    }

    @Override
    public void populate(final EventItem eventItem) {
        Event event = eventItem.getItem();
        Association assoc = map.get(event.getAssociation());
        title.setText(event.getTitle());
        association.setText(assoc.getName());
        start.setText(event.getLocalStart().format(HOUR_FORMATTER));
        cardView.setOnClickListener(v -> {
            Intent intent = EventDetailsActivity.start(v.getContext(), event, assoc);
            v.getContext().startActivity(intent);
        });

        float size = ViewUtils.convertDpToPixel(4, cardView.getContext());

        // If this is the last event in it's section, we enable rounded corners.
        boolean isLast = eventItem.isLastOfSection();
        ShapeAppearanceModel.Builder builder = new ShapeAppearanceModel.Builder()
                .setAllCorners(new RoundedCornerTreatment())
                .setTopLeftCornerSize(0)
                .setTopRightCornerSize(0)
                .setBottomLeftCornerSize(isLast ? size : 0)
                .setBottomRightCornerSize(isLast ? size : 0);
        cardView.setShapeAppearanceModel(builder.build());

        // Add some margin if there is a shadow. Otherwise the shadow is hidden. The margin is 4 DP, which together with
        // the 4 DP margin of the header of the next section results in the correct spacing of 8 DP between cards.
        // The RecyclerView also has a top and bottom padding of 4 DP (combined with clipToPadding=false) for the very first
        // and last element.
        int bottomMarginInPx = isLast ? itemView.getContext().getResources().getDimensionPixelSize(R.dimen.card_margin_half) : 0;
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();
        params.bottomMargin = bottomMarginInPx;
        cardView.setLayoutParams(params);

        // Hide the divider in the last case.
        divider.setVisibility(eventItem.isLastOfSection() ? View.GONE : View.VISIBLE);
    }
}
