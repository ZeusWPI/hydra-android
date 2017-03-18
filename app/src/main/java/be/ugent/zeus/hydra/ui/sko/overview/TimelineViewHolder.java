package be.ugent.zeus.hydra.ui.sko.overview;

import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.v4.text.util.LinkifyCompat;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.sko.TimelinePost;
import be.ugent.zeus.hydra.ui.common.recyclerview.DataViewHolder;
import be.ugent.zeus.hydra.ui.widgets.NowToolbar;
import be.ugent.zeus.hydra.utils.DateUtils;
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
    private NowToolbar toolbar;

    public TimelineViewHolder(View itemView, TimelineAdapter adapter) {
        super(itemView);
        this.adapter = adapter;
        poster = $(itemView, R.id.post_poster);
        title = $(itemView, R.id.post_title);
        body = $(itemView, R.id.post_body);
        subtitle = $(itemView, R.id.post_subtitle);
        toolbar = $(itemView, R.id.sko_now_toolbar);
    }

    @DrawableRes
    private static int getOriginIcon(TimelinePost post) {
        switch (post.getOrigin()) {
            case "facebook":
                return R.drawable.ic_social_facebook;
            case "instagram":
                return R.drawable.ic_social_instagram;
            default:
                return R.drawable.logo_sko;
        }
    }

    @Override
    public void populate(final TimelinePost post) {

        Picasso.with(itemView.getContext()).load(post.getCoverMedia()).into(poster);
        if (post.getCoverMedia() != null) {
            int value = (int) itemView.getContext().getResources().getDimension(R.dimen.card_title_padding_top_large_title);
            title.setPadding(title.getPaddingLeft(), value, title.getPaddingRight(), title.getPaddingBottom());
        } else {
            title.setPadding(title.getPaddingLeft(), 0, title.getPaddingRight(), title.getPaddingBottom());
        }

        if (post.getTitle() != null) {
            title.setVisibility(View.VISIBLE);
            subtitle.setVisibility(View.VISIBLE);
            title.setText(post.getTitle());
        } else {
            title.setVisibility(View.GONE);
        }
        CharSequence dateString = DateUtils.relativeDateTimeString(post.getCreatedAt(), itemView.getContext(), true);
        toolbar.setTitle(post.getDisplayType() + " van " + post.getOrigin() + " \u2022 " + dateString);
        toolbar.setIcon(getOriginIcon(post));

        // If there is a body, set it and set the expand button, else hide those.
        if (post.getBody() != null) {
            body.setText(post.getBody());
            LinkifyCompat.addLinks(body, Linkify.WEB_URLS);
            body.setOnClickListener(v -> adapter.getHelper().openCustomTab(Uri.parse(post.getLink())));
        }

        itemView.setOnClickListener(v -> adapter.getHelper().openCustomTab(Uri.parse(post.getLink())));
    }
}