package be.ugent.zeus.hydra.library.list;

import android.content.Context;
import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.library.Library;
import be.ugent.zeus.hydra.requests.common.ProcessableCacheRequest;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;
import java8.util.Comparators;
import java8.util.Lists;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class SortedLibraryRequest extends ProcessableCacheRequest<LibraryList, List<Library>> {

    /**
     * Create a request.
     *
     * @param context       A context. Can be any context, as the application context is taken.
     * @param shouldRefresh Should fresh data be used or not.
     */
    public SortedLibraryRequest(Context context, boolean shouldRefresh) {
        super(context, new LibraryListRequest(), shouldRefresh);
    }

    @NonNull
    @Override
    protected List<Library> transform(@NonNull LibraryList data) throws RequestFailureException {
        Lists.sort(data.getLibraries(), Comparators.comparing(Library::getName));
        return data.getLibraries();
    }
}