package be.ugent.zeus.hydra.adapters;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.HydraWebViewActivity;
import be.ugent.zeus.hydra.activities.InfoSubItemActivity;
import be.ugent.zeus.hydra.models.info.InfoItem;
import be.ugent.zeus.hydra.models.info.InfoList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ellen on 8/3/16.
 */
public class InfoListAdapter extends RecyclerView.Adapter<InfoListAdapter.CardViewHolder> {
    private ArrayList<InfoItem> items;
    protected final static String HTML_API = "https://zeus.ugent.be/hydra/api/2.0/info/";

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        private final View view;
        private TextView title;
        private ImageView imageView;
        private ImageView linkView;
        private Context context;


        public CardViewHolder(View v,Context context) {
            super(v);
            this.view = v;
            title = (TextView) v.findViewById(R.id.info_name);
            imageView = (ImageView) v.findViewById(R.id.infoImage);
            linkView = (ImageView) v.findViewById(R.id.linkImage);
            this.context=context;
        }

        public void populate(final InfoItem infoItem) {
            title.setText(infoItem.getTitle());
            //// TODO: 06/04/2016 set correct linkview

            linkView.setImageResource(R.drawable.arrow_right);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String androidUrl = infoItem.getUrlAndroid();
                    String url = infoItem.getUrl();
                    String html = infoItem.getHtml();
                    InfoList infolist = infoItem.getSubContent();
                    if (androidUrl != null) {
                        Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + androidUrl));
                        boolean marketFound = false;

                        // find all applications able to handle our rateIntent
                        final List<ResolveInfo> otherApps = context.getPackageManager().queryIntentActivities(rateIntent, 0);
                        for (ResolveInfo otherApp: otherApps) {
                            // look for Google Play application
                            if (otherApp.activityInfo.applicationInfo.packageName.equals("com.android.vending")) {

                                ActivityInfo otherAppActivity = otherApp.activityInfo;
                                ComponentName componentName = new ComponentName(
                                        otherAppActivity.applicationInfo.packageName,
                                        otherAppActivity.name
                                );
                                rateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                                rateIntent.setComponent(componentName);
                                context.startActivity(rateIntent);
                                marketFound = true;
                                break;
                            }
                        }

                        // if GP not present on device, open web browser
                        if (!marketFound) {
                            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+androidUrl));
                            context.startActivity(webIntent);
                        }
                    } else if (url != null) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        context.startActivity(browserIntent);
                    } else if (html != null ){
                        Intent intent = new Intent(v.getContext(), HydraWebViewActivity.class);
                        intent.putExtra(HydraWebViewActivity.URL, HTML_API + html);
                        intent.putExtra(HydraWebViewActivity.TITLE, infoItem.getTitle());
                        context.startActivity(intent);
                    } else if (infolist != null) {
                        Intent intent = new Intent(v.getContext(), InfoSubItemActivity.class);
                        intent.putParcelableArrayListExtra(InfoSubItemActivity.INFOITEMS, infolist);
                        intent.putExtra(InfoSubItemActivity.INFOTITLE, infoItem.getTitle());
                        context.startActivity(intent);
                    }
                }
            });
            if (infoItem.getImage() != null) {
                int resId = context.getResources().getIdentifier(infoItem.getImage(), "drawable", context.getPackageName());
                imageView.setImageResource(resId);
                imageView.setVisibility(View.VISIBLE);
            } else {
                imageView.setVisibility(View.GONE);
            }
        }
    }



    public InfoListAdapter() {
        this.items = new ArrayList<>();
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.info_card, parent, false);
        return new CardViewHolder(v,parent.getContext());
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        final InfoItem category = items.get(position);
        holder.populate(category);
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(ArrayList<InfoItem> items) {
        this.items.clear();
        for (InfoItem item : items) {
            this.items.add(item);
        }
    }
}