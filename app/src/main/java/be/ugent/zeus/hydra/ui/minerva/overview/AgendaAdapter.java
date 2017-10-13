package be.ugent.zeus.hydra.ui.minerva.overview;

import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.ui.common.ViewUtils;
import be.ugent.zeus.hydra.ui.common.recyclerview.adapters.ItemDiffAdapter;
import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DateHeaderViewHolder;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import org.threeten.bp.format.DateTimeFormatter;

/**
 * @author Niko Strijbol
 */
class AgendaAdapter extends ItemDiffAdapter<AgendaItem, AgendaViewHolder> implements StickyRecyclerHeadersAdapter<DateHeaderViewHolder> {

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