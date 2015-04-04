package be.ugent.zeus.hydra.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;

/**
 * @author Titouan Vervack
 */
public class NavigationDrawerAdapter extends ArrayAdapter<String> {

    private final TypedArray icons;
    private final LayoutInflater inflater;

    public NavigationDrawerAdapter(Context context, TypedArray icons, String[] titles) {
        super(context, R.layout.navigation_drawer_item, titles);
        this.icons = icons;

        inflater = LayoutInflater.from(context);
    }

    private static class ViewHolder {
        ImageView icon;
        TextView title;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.navigation_drawer_item, parent, false);
            holder = new ViewHolder();

            holder.icon = (ImageView) convertView.findViewById(R.id.navigation_drawer_item_icon);
            holder.title = (TextView) convertView.findViewById(R.id.navigation_drawer_item_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.icon.setImageDrawable(icons.getDrawable(position));
        holder.title.setText(getItem(position));

        return convertView;
    }
}
