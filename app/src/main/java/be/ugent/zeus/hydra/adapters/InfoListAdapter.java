package be.ugent.zeus.hydra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.Info.InfoItem;

/**
 * Created by Juta on 03/03/2016.
 */
public class InfoListAdapter   extends ArrayAdapter<InfoItem> {
    private final Context context;
    private final List<InfoItem> items;

    public InfoListAdapter(Context context, int resource, List<InfoItem> objects) {
        super(context, resource, objects);
        this.context = context;
        this.items = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.info_item, parent, false);
        TextView textViewTitle = (TextView) rowView.findViewById(R.id.title);

        //get activity
        InfoItem infoItem = items.get(position);

        //fill in
        textViewTitle.setText(infoItem.getTitle());

        return rowView;
    }
}
