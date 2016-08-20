package be.ugent.zeus.hydra.recyclerview.adapters.minerva;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.recyclerview.adapters.common.ItemAdapter;
import be.ugent.zeus.hydra.recyclerview.viewholder.DateHeaderViewHolder;
import be.ugent.zeus.hydra.recyclerview.viewholder.minerva.AgendaViewHolder;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Niko Strijbol
 */
public class AgendaAdapter extends ItemAdapter<AgendaItem, AgendaViewHolder> implements StickyRecyclerHeadersAdapter<DateHeaderViewHolder> {

    private static final Locale locale = new Locale("nl");
    private static final DateFormat INT_FORMATTER = new SimpleDateFormat("ddMMyyyy", locale);

    @Override
    public AgendaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_minerva_agenda, parent, false);
        return new AgendaViewHolder(v);
    }

    /**
     * We want to sort per day,
     */
    @Override
    public long getHeaderId(int position) {
        Date date = items.get(position).getStartDate();
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
        holder.populate(items.get(position).getStartDate());
    }
}