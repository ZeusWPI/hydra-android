package be.ugent.zeus.hydra.minerva.calendar.upcominglist;

import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.ViewUtils;
import be.ugent.zeus.hydra.common.ui.recyclerview.EqualsCallback;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.ItemDiffAdapter;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DateHeaderViewHolder;
import be.ugent.zeus.hydra.minerva.calendar.AgendaItem;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import org.threeten.bp.format.DateTimeFormatter;

/**
 * @author Niko Strijbol
 */
class AgendaAdapter extends ItemDiffAdapter<AgendaItem, AgendaViewHolder> implements StickyRecyclerHeadersAdapter<DateHeaderViewHolder> {

    AgendaAdapter() {
        super((agendaItems, agendaItems2) -> new EqualsCallback<AgendaItem>(agendaItems, agendaItems2) {
            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return oldItems.get(oldItemPosition).isMerged() == newItems.get(newItemPosition).isMerged();
            }
        });
    }

    private static final DateTimeFormatter INT_FORMATTER = DateTimeFormatter.ofPattern("ddMMyyyy");

    @Override
    public AgendaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AgendaViewHolder(ViewUtils.inflate(parent, R.layout.item_minerva_agenda));
    }

    /**
     * We want to sort per day,
     */
    @Override
    public long getHeaderId(int position) {
        return Integer.parseInt(items.get(position).getStartDate().format(INT_FORMATTER));
    }

    @Override
    public DateHeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return new DateHeaderViewHolder(ViewUtils.inflate(parent, R.layout.item_date_header));
    }

    @Override
    public void onBindHeaderViewHolder(DateHeaderViewHolder holder, int position) {
        holder.populate(items.get(position).getStartDate());
    }
}