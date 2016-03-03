package be.ugent.zeus.hydra.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import be.ugent.zeus.hydra.R;

/**
 * Created by michiel on 3/3/16.
 */
public class RestoCardAdapter extends RecyclerView.Adapter<RestoCardAdapter.ViewHolder> implements StickyRecyclerHeadersAdapter {
    private String[] dataSet;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView categoryView;
        public ViewHolder(View v) {
            super(v);
            categoryView = (TextView) v.findViewById(R.id.category_text);
        }

        public void populate(String lol) {
            categoryView.setText(lol);
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView headerText;
        public HeaderViewHolder(View v) {
            super(v);
            headerText = (TextView) v.findViewById(R.id.resto_header_text);
        }

        public void populate(long index) {
            headerText.setText("Date " + index);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RestoCardAdapter(String[] myDataset) {
        dataSet = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RestoCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.resto_card, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.populate(dataSet[position]);

    }

    @Override
    public long getHeaderId(int position) {
        return position / 3;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.resto_day_header, parent, false);
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((HeaderViewHolder) holder).populate(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataSet.length;
    }
}