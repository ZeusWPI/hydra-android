package be.ugent.zeus.hydra.association.preference;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.*;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.common.arch.observers.PartialErrorObserver;
import be.ugent.zeus.hydra.common.arch.observers.ProgressObserver;
import be.ugent.zeus.hydra.common.arch.observers.SuccessObserver;
import com.google.android.material.snackbar.Snackbar;

import static be.ugent.zeus.hydra.common.utils.FragmentUtils.requireBaseActivity;

/**
 * Allow the user to select preferences.
 *
 * @author Niko Strijbol
 */
public class AssociationSelectionPreferenceFragment extends Fragment {

    /**
     * Key for the preference that contains which associations should be shown.
     */
    public static final String PREF_ASSOCIATIONS_SHOWING = "pref_associations_showing_v3";

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
        model.getData().observe(getViewLifecycleOwner(), SuccessObserver.with(this::receiveData));
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

    private void receiveData(@NonNull List<Association> data) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        Set<String> enabled = preferences.getStringSet(PREF_ASSOCIATIONS_SHOWING, Collections.emptySet());
        List<Pair<Association, Boolean>> values = new ArrayList<>();

        for (Association association : data) {
            values.add(new Pair<>(association, enabled.contains(association.getAbbreviation())));
        }

        adapter.submitData(values);
    }

    @Override
    public void onPause() {
        super.onPause();

        //Save the values.
        Set<String> enabled = new HashSet<>();
        for (Pair<Association, Boolean> pair : adapter.getItemsAndState()) {
            if (pair.second) {
                enabled.add(pair.first.getAbbreviation());
            }
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        preferences.edit().putStringSet(PREF_ASSOCIATIONS_SHOWING, enabled).apply();
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(requireView(), getString(R.string.error_network), Snackbar.LENGTH_LONG).show();
    }
}
