package be.ugent.zeus.hydra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.Association.AssociationActivity;

/**
 * Created by Ellen on 16/02/2016.
 */
public class ActivityListAdapter  extends ArrayAdapter<AssociationActivity> {
    private final Context context;
    private final List<AssociationActivity> items;

    //format time
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());


    public ActivityListAdapter(Context context, int resource, List<AssociationActivity> objects) {
        super(context, resource, objects);
        this.context = context;
        this.items = objects;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.activityitem, parent, false);
        TextView textViewName = (TextView) rowView.findViewById(R.id.name);
        TextView textViewAssociation = (TextView) rowView.findViewById(R.id.association);
        TextView textViewStart = (TextView) rowView.findViewById(R.id.starttime);

        //get activity
        AssociationActivity activity = items.get(position);

        //fill in
        textViewName.setText(activity.title);
        textViewAssociation.setText(activity.association.display_name);
        textViewStart.setText(dateFormatter.format(activity.start));

        return rowView;

    }
}
