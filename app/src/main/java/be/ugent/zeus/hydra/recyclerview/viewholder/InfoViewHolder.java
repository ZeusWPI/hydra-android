package be.ugent.zeus.hydra.recyclerview.viewholder;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.WebViewActivity;
import be.ugent.zeus.hydra.activities.InfoSubItemActivity;
import be.ugent.zeus.hydra.models.info.InfoItem;
import be.ugent.zeus.hydra.models.info.InfoList;

import java.util.List;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
public class InfoViewHolder extends RecyclerView.ViewHolder implements DataViewHolder<InfoItem> {

    private final static String HTML_API = "https://zeus.ugent.be/hydra/api/2.0/info/";

    private TextView title;

    public InfoViewHolder(View v) {
        super(v);
        title = $(v, R.id.info_name);
    }

    public void populateData(final InfoItem infoItem) {
        title.setText(infoItem.getTitle());
        //// TODO: 06/04/2016 set correct linkview

        final String androidUrl = infoItem.getUrlAndroid();
        final String url = infoItem.getUrl();
        final String html = infoItem.getHtml();
        final InfoList infolist = infoItem.getSubContent();

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (androidUrl != null) {
                    Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + androidUrl));
                    boolean marketFound = false;

                    // find all applications able to handle our rateIntent
                    final List<ResolveInfo> otherApps = itemView.getContext().getPackageManager().queryIntentActivities(rateIntent, 0);
                    for (ResolveInfo otherApp : otherApps) {
                        // look for Google Play application
                        if (otherApp.activityInfo.applicationInfo.packageName.equals("com.android.vending")) {

                            ActivityInfo otherAppActivity = otherApp.activityInfo;
                            ComponentName componentName = new ComponentName(
                                    otherAppActivity.applicationInfo.packageName,
                                    otherAppActivity.name
                            );
                            rateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                            rateIntent.setComponent(componentName);
                            itemView.getContext().startActivity(rateIntent);
                            marketFound = true;
                            break;
                        }
                    }

                    // if GP not present on device, open web browser
                    if (!marketFound) {
                        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + androidUrl));
                        itemView.getContext().startActivity(webIntent);
                    }
                } else if (url != null) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    itemView.getContext().startActivity(browserIntent);
                } else if (html != null) {
                    Intent intent = new Intent(v.getContext(), WebViewActivity.class);
                    intent.putExtra(WebViewActivity.URL, HTML_API + html);
                    intent.putExtra(WebViewActivity.TITLE, infoItem.getTitle());
                    itemView.getContext().startActivity(intent);
                } else if (infolist != null) {
                    Intent intent = new Intent(v.getContext(), InfoSubItemActivity.class);
                    intent.putParcelableArrayListExtra(InfoSubItemActivity.INFO_ITEMS, infolist);
                    intent.putExtra(InfoSubItemActivity.INFO_TITLE, infoItem.getTitle());
                    itemView.getContext().startActivity(intent);
                }
            }
        });

        if (infoItem.getImage() != null) {

            //The drawable id
            int resId = itemView.getContext().getResources().getIdentifier("ic_" + infoItem.getImage(), "drawable", itemView.getContext().getPackageName());

            Drawable icon;
            Drawable more;

            int darkColor = ContextCompat.getColor(itemView.getContext(), R.color.ugent_blue_dark);

            //For older API, we do something different
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                VectorDrawableCompat compat = VectorDrawableCompat.create(itemView.getContext().getResources(), resId, itemView.getContext().getTheme());
                VectorDrawableCompat moreCompat = VectorDrawableCompat.create(itemView.getContext().getResources(), R.drawable.ic_chevron_right_24dp, itemView.getContext().getTheme());
                //Tint the icon
                compat.setTint(darkColor);
                moreCompat.setTint(darkColor);
                icon = compat;
                more = moreCompat;
            } else {
                icon = itemView.getContext().getDrawable(resId);
                more = itemView.getContext().getDrawable(R.drawable.ic_chevron_right_24dp);
                icon.setTint(darkColor);
                more.setTint(darkColor);
            }

            //Remove arrow if internal link
            if(androidUrl == null && url == null && (html != null || infolist != null)) {
                title.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
            } else {
                title.setCompoundDrawablesWithIntrinsicBounds(icon, null, more, null);
            }

        }
    }
}
