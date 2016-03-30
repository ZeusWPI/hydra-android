package be.ugent.zeus.hydra.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.AssociationActivityDetail;
import be.ugent.zeus.hydra.models.association.AssociationActivities;
import be.ugent.zeus.hydra.models.association.AssociationActivity;
import be.ugent.zeus.hydra.recyclerviewholder.DateHeaderViewHolder;

/**
 * Created by ellen on 8/3/16.
 */
public class ActivityListAdapter extends RecyclerView.Adapter<ActivityListAdapter.CardViewHolder> implements StickyRecyclerHeadersAdapter {
    private ArrayList<AssociationActivity> items;

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        private final View view;
        private TextView start;
        private TextView title;
        private TextView association;
        private final SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());


        public CardViewHolder(View v) {
            super(v);
            this.view = v;
            title = (TextView) v.findViewById(R.id.name);
            association = (TextView) v.findViewById(R.id.association);
            start = (TextView) v.findViewById(R.id.starttime);
        }

        public void populate(final AssociationActivity activity) {
            title.setText(activity.title);
            association.setText(activity.association.display_name);
            start.setText(dateFormatter.format(activity.start));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(view.getContext(), AssociationActivityDetail.class);
                    intent.putExtra("associationActivity", activity);
                    view.getContext().startActivity(intent);
                }
            });
        }
    }



    public ActivityListAdapter() {
        this.items = new ArrayList<>();
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_listitem, parent, false);
        CardViewHolder vh = new CardViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        final AssociationActivity restoCategory = items.get(position);
        holder.populate(restoCategory);
    }

    @Override
    public long getHeaderId(int position) {
        Date date = items.get(position).start;
        return date.getMonth()*100+date.getDay(); //todo
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_listitem_header, parent, false);
        return new DateHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((DateHeaderViewHolder) holder).populate(items.get(position).start);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(AssociationActivities items) {
        this.items.clear();
        for (AssociationActivity item : items) {
            this.items.add(item);

        }


    }
}