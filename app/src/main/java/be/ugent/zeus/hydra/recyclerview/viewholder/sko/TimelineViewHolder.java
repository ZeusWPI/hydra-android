package be.ugent.zeus.hydra.recyclerview.viewholder.sko;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.sko.TimelinePost;
import be.ugent.zeus.hydra.recyclerview.adapters.sko.TimelineAdapter;
import be.ugent.zeus.hydra.recyclerview.viewholder.DataViewHolder;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.utils.ViewUtils;
import com.squareup.picasso.Picasso;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
public class TimelineViewHolder extends DataViewHolder<TimelinePost> {

    private TimelineAdapter adapter;

    private ImageView poster;
    private TextView title;
    private TextView subtitle;
    private TextView body;
    private Button linkButton;
    private ImageButton expandButton;
    private CardView cardView;

    public TimelineViewHolder(View itemView, TimelineAdapter adapter) {
        super(itemView);
        this.adapter = adapter;
        poster = $(itemView, R.id.post_poster);
        title = $(itemView, R.id.post_title);
        linkButton = $(itemView, R.id.post_button_link);
        expandButton = $(itemView, R.id.post_expand_button);
        body = $(itemView, R.id.post_body);
        cardView = $(itemView, R.id.post_card_view);
        subtitle = $(itemView, R.id.post_subtitle);
    }

    @Override
    public void populate(final TimelinePost post) {

        if(post.getPoster() != null) {
            Picasso.with(itemView.getContext()).load(post.getPoster()).into(poster);
        }

        if(post.getTitle() != null) {
            title.setVisibility(View.VISIBLE);
            subtitle.setVisibility(View.VISIBLE);
            title.setText(post.getTitle());
            CharSequence dateString = DateUtils.relativeDateTimeString(post.getCreatedAt(), itemView.getContext(), true);
            subtitle.setText(dateString + " | " + post.getDisplayType() + " van " + post.getOrigin());
        } else {
            title.setVisibility(View.GONE);
            subtitle.setVisibility(View.GONE);
        }

        //If there is a body, set it and set the expand button, else hide those.
        if(post.getBody() != null) {
            body.setText(post.getBody());
            setButton(adapter.isExpanded(post));

            //Use the same listener for both the button and the card.
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimelineViewHolder.this.onClick(post);
                }
            };
            expandButton.setOnClickListener(listener);
            cardView.setOnClickListener(listener);
        } else {
            expandButton.setVisibility(View.GONE);
        }

        if(post.getLink() != null) {
            linkButton.setVisibility(View.VISIBLE);
            linkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.getHelper().openCustomTab(Uri.parse(post.getLink()));
                }
            });
        } else {
            linkButton.setVisibility(View.GONE);
        }
    }

    private void onClick(TimelinePost post) {

        boolean isExpanded = adapter.isExpanded(post);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && !isExpanded) {
            TransitionManager.beginDelayedTransition(cardView);
        }
        if(isExpanded) {
            body.setVisibility(View.GONE);
            adapter.setNotExpanded(post);
            setButton(false);
        } else {
            body.setVisibility(View.VISIBLE);
            adapter.setExpanded(post);
            setButton(true);
        }
    }

    private void setButton(boolean expanded) {
        Context c = itemView.getContext();
        Drawable d;
        if(expanded) {
            d = ViewUtils.getTintedVectorDrawable(c, R.drawable.ic_keyboard_arrow_up_24dp, R.color.ugent_blue_dark);
            body.setVisibility(View.VISIBLE);
        } else {
            d = ViewUtils.getTintedVectorDrawable(c, R.drawable.ic_keyboard_arrow_down_24dp, R.color.ugent_blue_dark);
            body.setVisibility(View.GONE);
        }
        expandButton.setImageDrawable(d);
    }
}
