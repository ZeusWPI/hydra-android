package be.ugent.zeus.hydra.recyclerview.adapters;

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
import be.ugent.zeus.hydra.recyclerview.viewholder.DateHeaderViewHolder;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Collections;
import java.util.List;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Adapter for the list of activities.
 *
 * @author ellen
 */
public class ActivityListAdapter extends RecyclerView.Adapter<ActivityListAdapter.CardViewHolder> implements StickyRecyclerHeadersAdapter<DateHeaderViewHolder> {

    private static final DateTimeFormatter INTEGER_FORMATTER = DateTimeFormatter.ofPattern("ddMMyyyy");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

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
            start.setText(activity.getLocalStart().format(FORMATTER));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), ActivityDetailActivity.class);
                    intent.putExtra(ActivityDetailActivity.PARCEL_EVENT, (Parcelable) activity);
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
        LocalDateTime date = data.get(position).getLocalStart();
        return Integer.parseInt(date.format(INTEGER_FORMATTER));
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