package be.ugent.zeus.hydra.minerva.calendar.upcominglist;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.ViewUtils;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.DiffAdapter;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.EqualsItemCallback;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DateHeaderViewHolder;
import be.ugent.zeus.hydra.minerva.calendar.AgendaItem;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import org.threeten.bp.format.DateTimeFormatter;

/**
 * @author Niko Strijbol
 */
class AgendaAdapter extends DiffAdapter<AgendaItem, AgendaViewHolder> implements StickyRecyclerHeadersAdapter<DateHeaderViewHolder> {

    AgendaAdapter() {
        super(new EqualsItemCallback<AgendaItem>() {
            @Override
            public boolean areContentsTheSame(AgendaItem oldItem, AgendaItem newItem) {
                return oldItem.isMerged() == newItem.isMerged();
            }
        });
    }

    private static final DateTimeFormatter INT_FORMATTER = DateTimeFormatter.ofPattern("ddMMyyyy");

    @NonNull
    @Override
    public AgendaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AgendaViewHolder(ViewUtils.inflate(parent, R.layout.item_minerva_agenda));
    }

    /**
     * We want to sort per day,
     */
    @Override
    public long getHeaderId(int position) {
        return Integer.parseInt(getItem(position).getStartDate().format(INT_FORMATTER));
    }

    @Override
    public DateHeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return new DateHeaderViewHolder(ViewUtils.inflate(parent, R.layout.item_date_header));
    }

    @Override
    public void onBindHeaderViewHolder(DateHeaderViewHolder holder, int position) {
        holder.populate(getItem(position).getStartDate());
    }
}