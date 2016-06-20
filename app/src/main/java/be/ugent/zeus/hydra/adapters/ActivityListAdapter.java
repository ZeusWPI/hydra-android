package be.ugent.zeus.hydra.adapters;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.ActivityDetailActivity;
import be.ugent.zeus.hydra.models.association.Activity;
import be.ugent.zeus.hydra.recyclerviewholder.DateHeaderViewHolder;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Adapter for the list of activities.
 *
 * @author ellen
 */
public class ActivityListAdapter extends RecyclerView.Adapter<ActivityListAdapter.CardViewHolder> implements StickyRecyclerHeadersAdapter<DateHeaderViewHolder> {

    private static final DateFormat INT_FORMATTER = new SimpleDateFormat("ddMMyyyy", Locale.getDefault());
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public static class CardViewHolder extends RecyclerView.ViewHolder {

        private TextView start;
        private TextView title;
        private TextView association;

        private CardViewHolder(View v) {
            super(v);
            title = $(v, R.id.name);
            association = $(v, R.id.association);
            start = $(v, R.id.starttime);
        }

        private void populate(final Activity activity) {
            title.setText(activity.getTitle());
            association.setText(activity.getAssociation().getDisplayName());
            start.setText(FORMATTER.format(activity.getStart()));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), ActivityDetailActivity.class);
                    intent.putExtra("associationActivity", (Parcelable) activity);
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }

    private List<Activity> data = Collections.emptyList();
    private List<Activity> original = Collections.emptyList();


    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activity, parent, false);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        final Activity restoCategory = data.get(position);
        holder.populate(restoCategory);
    }

    /**
     * We want to sort per day,
     */
    @Override
    public long getHeaderId(int position) {
        Date date = data.get(position).getStart();
        return Integer.parseInt(INT_FORMATTER.format(date));
    }

    @Override
    public DateHeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_date_header, parent, false);
        return new DateHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(DateHeaderViewHolder holder, int position) {
        holder.populate(data.get(position).getStart());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<Activity> data) {
        this.data = data;
    }

    public List<Activity> getData() {
        return data;
    }

    public void setOriginal(List<Activity> data) {
        this.original = data;
    }

    public List<Activity> getOriginal() {
        return original;
    }
}