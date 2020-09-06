package be.ugent.zeus.hydra.association.preference;

import android.app.Application;
import android.util.Pair;
import androidx.annotation.NonNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.association.AssociationListRequest;
import be.ugent.zeus.hydra.association.AssociationStore;
import be.ugent.zeus.hydra.association.list.Filter;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.ui.RequestViewModel;

/**
 * @author Niko Strijbol
 */
public class AssociationsViewModel extends RequestViewModel<List<Pair<Association, Boolean>>> {

    public AssociationsViewModel(Application application) {
        super(application);
    }

    @NonNull
    @Override
    protected Request<List<Pair<Association, Boolean>>> getRequest() {
        return AssociationListRequest.create(getApplication()).map(m -> {
            Set<String> whitelist = AssociationStore.read(getApplication(), m);
            return m.associations()
                    .map(association -> new Pair<>(association, whitelist.contains(association.getAbbreviation())))
                    .sorted(Filter.selectionComparator())
                    .collect(Collectors.toList());
        });
    }
}
