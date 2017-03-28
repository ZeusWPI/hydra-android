package be.ugent.zeus.hydra.ui.minerva;

import android.content.Context;
import android.content.IntentFilter;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.data.database.minerva.AgendaDao;
import be.ugent.zeus.hydra.data.sync.SyncBroadcast;
import be.ugent.zeus.hydra.ui.common.loaders.LoaderException;
import be.ugent.zeus.hydra.ui.common.loaders.BroadcastLoader;

/**
 * Load one {@link AgendaItem}.
 *
 * @author Niko Strijbol
 */
class AgendaItemLoader extends BroadcastLoader<AgendaItem> {

    private final AgendaDao dao;
    private final int id;

    /**
     * This loader has the option to ignore the cache.
     *
     * @param context The context.
     */
    AgendaItemLoader(Context context, AgendaDao dao, int id) {
        super(context, new IntentFilter(SyncBroadcast.SYNC_AGENDA));
        this.dao = dao;
        this.id = id;
    }

    /**
     * Provide the data for the loader.
     *
     * @return The data.
     *
     * @throws LoaderException If the data could not be provided.
     */
    @NonNull
    @Override
    protected AgendaItem loadData() throws LoaderException {

        AgendaItem item = dao.getItem(id);

        if (item == null) {
            throw new LoaderException("No agenda item!");
        } else {
            return item;
        }
    }
}