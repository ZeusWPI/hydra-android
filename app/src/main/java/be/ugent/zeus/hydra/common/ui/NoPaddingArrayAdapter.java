package be.ugent.zeus.hydra.common.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Removes padding from the items.
 *
 * @see <a href="https://stackoverflow.com/questions/29164650">StackOverflow question</a>
 *
 * @author Niko Strijbol
 */
public class NoPaddingArrayAdapter<T> extends ArrayAdapter<T> {

    public NoPaddingArrayAdapter(Context context, int resource) {
        super(context, resource);
    }

    public NoPaddingArrayAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public NoPaddingArrayAdapter(Context context, int resource, T[] objects) {
        super(context, resource, objects);
    }

    public NoPaddingArrayAdapter(Context context, int resource, int textViewResourceId, T[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public NoPaddingArrayAdapter(Context context, int resource, List<T> objects) {
        super(context, resource, objects);
    }

    public NoPaddingArrayAdapter(Context context, int resource, int textViewResourceId, List<T> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        view.setPadding(0, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
        return view;
    }
}