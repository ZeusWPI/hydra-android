package be.ugent.zeus.hydra.recyclerview.adapters.minerva;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.recyclerview.adapters.common.EmptyItemLoader;
import be.ugent.zeus.hydra.recyclerview.viewholder.DateHeaderViewHolder;
import be.ugent.zeus.hydra.recyclerview.viewholder.minerva.AgendaViewHolder;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import org.threeten.bp.format.DateTimeFormatter;

/**
 * @author Niko Strijbol
 */
public class AgendaAdapter extends EmptyItemLoader<AgendaItem, AgendaViewHolder> implements StickyRecyclerHeadersAdapter<DateHeaderViewHolder> {

    private static final DateTimeFormatter INT_FORMATTER = DateTimeFormatter.ofPattern("ddMMyyyy");

    public AgendaAdapter() {
        super(R.layout.item_no_data);
    }

    @Override
    protected AgendaViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_minerva_agenda, parent, false);
        return new AgendaViewHolder(v);
    }

    /**
     * We want to sort per day,
     */
    @Override
    public long getHeaderId(int position) {
        if(getItemViewType(position) == EMPTY_VIEW) {
            return -1; //No header
        } else {
            return Integer.parseInt(items.get(position).getStartDate().format(INT_FORMATTER));
        }
    }

    @Override
    public DateHeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date_header, parent, false);
        return new DateHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(DateHeaderViewHolder holder, int position) {
        if(getItemViewType(position) != EMPTY_VIEW) {
            holder.populate(items.get(position).getStartDate());
        }
    }
}