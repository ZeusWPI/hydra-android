package be.ugent.zeus.hydra.association.preference;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.association.*;
import be.ugent.zeus.hydra.association.list.Filter;
import be.ugent.zeus.hydra.common.arch.observers.PartialErrorObserver;
import be.ugent.zeus.hydra.common.arch.observers.ProgressObserver;
import be.ugent.zeus.hydra.common.arch.observers.SuccessObserver;
import com.google.android.material.snackbar.Snackbar;

import static be.ugent.zeus.hydra.common.utils.FragmentUtils.requireBaseActivity;

/**
 * Display a list of associations for which the user wants to see information.
 *
 * @author Niko Strijbol
 * @see AssociationStore The class responsible for the low level saving of these preferences.
 */
public class AssociationSelectionPreferenceFragment extends Fragment {

    private static final String TAG = "AssociationSelectPrefAc";

    private final SearchableAssociationsAdapter adapter = new SearchableAssociationsAdapter();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preferences_associations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        SearchView searchView = view.findViewById(R.id.search_view);

        recyclerView.requestFocus();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        searchView.setOnQueryTextListener(adapter);

        AssociationsViewModel model = new ViewModelProvider(this).get(AssociationsViewModel.class);
        model.getData().observe(getViewLifecycleOwner(), PartialErrorObserver.with(this::onError));
        model.getData().observe(getViewLifecycleOwner(), SuccessObserver.with(adapter::submitData));
        model.getData().observe(getViewLifecycleOwner(), new ProgressObserver<>(view.findViewById(R.id.progress_bar)));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_pref_selectors, menu);
        requireBaseActivity(this).tintToolbarIcons(menu, R.id.action_select_all, R.id.action_select_none);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_select_all:
                adapter.setAllChecked(true);
                return true;
            case R.id.action_select_none:
                adapter.setAllChecked(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // Save the values.
        Set<String> whitelist = adapter.getItemsAndState()
                .stream()
                .filter(pair -> pair.second)
                .map(pair -> pair.first.getAbbreviation())
                .collect(Collectors.toSet());

        AssociationStore.replace(requireContext(), whitelist);
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(requireView(), getString(R.string.error_network), Snackbar.LENGTH_LONG).show();
    }
}
