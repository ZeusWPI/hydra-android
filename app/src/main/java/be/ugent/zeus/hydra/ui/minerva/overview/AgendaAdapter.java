package be.ugent.zeus.hydra.ui.minerva.overview;

import android.view.ViewGroup;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.ui.common.recyclerview.EmptyItemAdapter;
import be.ugent.zeus.hydra.ui.common.recyclerview.DateHeaderViewHolder;
import be.ugent.zeus.hydra.utils.ViewUtils;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import org.threeten.bp.format.DateTimeFormatter;

/**
 * @author Niko Strijbol
 */
public class AgendaAdapter extends EmptyItemAdapter<AgendaItem, AgendaViewHolder> implements StickyRecyclerHeadersAdapter<DateHeaderViewHolder> {

    private static final DateTimeFormatter INT_FORMATTER = DateTimeFormatter.ofPattern("ddMMyyyy");

    public AgendaAdapter() {
        super(R.layout.item_no_data);
    }

    @Override
    protected AgendaViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new AgendaViewHolder(ViewUtils.inflate(parent, R.layout.item_minerva_agenda));
    }

    /**
     * We want to sort per day,
     */
    @Override
    public long getHeaderId(int position) {
        if (getItemViewType(position) == EMPTY_TYPE) {
            return -1; //No header
        } else {
            return Integer.parseInt(items.get(position).getStartDate().format(INT_FORMATTER));
        }
    }

    @Override
    public DateHeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return new DateHeaderViewHolder(ViewUtils.inflate(parent, R.layout.item_date_header));
    }

    @Override
    public void onBindHeaderViewHolder(DateHeaderViewHolder holder, int position) {
        if (getItemViewType(position) != EMPTY_TYPE) {
            holder.populate(items.get(position).getStartDate());
        }
    }
}