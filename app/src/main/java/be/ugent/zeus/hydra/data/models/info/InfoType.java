package be.ugent.zeus.hydra.data.models.info;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.InfoSubItemActivity;
import be.ugent.zeus.hydra.activities.WebViewActivity;
import be.ugent.zeus.hydra.utils.NetworkUtils;
import be.ugent.zeus.hydra.utils.ViewUtils;

/**
 * The type of information (external url/app, internal url, ...).
 *
 * The different behavior warrants a real enum because if-else/switch is bad.
 *
 * @author Niko Strijbol
 */
public enum InfoType {

    //Opens in the browser
    EXTERNAL_LINK(R.drawable.ic_open_in_browser) {
        @Override
        public void doOnClick(Context context, InfoItem infoItem) {
            NetworkUtils.maybeLaunchBrowser(context, infoItem.getUrl());
        }
    },

    //Opens in another app
    EXTERNAL_APP(R.drawable.ic_open_in_new) {

        private static final String PLAY_STORE = "market://details?id=";
        private static final String PLAY_URL = "https://play.google.com/store/apps/details?id=";

        @Override
        public void doOnClick(Context context, InfoItem infoItem) {

            String androidUrl = infoItem.getUrlAndroid();

            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(PLAY_STORE + androidUrl)));
            } catch (ActivityNotFoundException e) {
                NetworkUtils.maybeLaunchBrowser(context, PLAY_URL + androidUrl);
            }
        }
    },

    //Opens in the app itself (web view or native)
    INTERNAL {

        private final static String HTML_API = "https://zeus.ugent.be/hydra/api/2.0/info/";

        @Override
        public void doOnClick(Context context, InfoItem infoItem) {
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra(WebViewActivity.URL, HTML_API + infoItem.getHtml());
            intent.putExtra(WebViewActivity.TITLE, infoItem.getTitle());
            context.startActivity(intent);
        }
    },

    //Opens a new list of info items.
    SUBLIST(R.drawable.ic_chevron_right) {
        @Override
        public void doOnClick(Context context, InfoItem infoItem) {
            Intent intent = new Intent(context, InfoSubItemActivity.class);
            intent.putParcelableArrayListExtra(InfoSubItemActivity.INFO_ITEMS, infoItem.getSubContent());
            intent.putExtra(InfoSubItemActivity.INFO_TITLE, infoItem.getTitle());
            context.startActivity(intent);
        }
    };

    private final int drawable;
    private static final int NO_DRAWABLE = 0;

    /**
     * @param drawable The ID of the vector drawable.
     */
    InfoType(@DrawableRes int drawable) {
        this.drawable = drawable;
    }

    /**
     * No drawable.
     */
    InfoType() {
        this(NO_DRAWABLE);
    }

    /**
     * Get the drawable for this category.
     *
     * @param context The context.
     * @param color The color to tint the drawable in.
     *
     * @return The drawable or null if there is no drawable.
     */
    @Nullable
    public Drawable getDrawable(Context context, @ColorRes int color) {

        //If there is no drawable, return null.
        if(drawable == NO_DRAWABLE) {
            return null;
        }

        return ViewUtils.getTintedVectorDrawable(context, this.drawable, color);
    }

    /**
     * The intent to be started for this type.
     *
     * @param context The context to launch the intent.
     * @param infoItem The item.
     */
    public abstract void doOnClick(Context context, InfoItem infoItem);
}