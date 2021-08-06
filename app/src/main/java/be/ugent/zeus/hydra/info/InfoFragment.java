/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.ugent.zeus.hydra.info;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.arch.observers.AdapterObserver;
import be.ugent.zeus.hydra.common.arch.observers.PartialErrorObserver;
import be.ugent.zeus.hydra.common.arch.observers.ProgressObserver;
import be.ugent.zeus.hydra.common.ui.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.common.ui.customtabs.CustomTabsHelper;
import com.google.android.material.snackbar.Snackbar;

import static be.ugent.zeus.hydra.common.utils.FragmentUtils.requireBaseActivity;

/**
 * Display info items.
 *
 * @author Niko Strijbol
 */
public class InfoFragment extends Fragment {

    private static final String TAG = "InfoFragment";

    private ActivityHelper helper;
    @Nullable
    private InfoViewModel model;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_infos, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = CustomTabsHelper.initHelper(getActivity(), null);
        helper.setShareMenu();
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        helper.bindCustomTabsService(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        helper.unbindCustomTabsService(getActivity());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        InfoListAdapter adapter = new InfoListAdapter(helper);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        ProgressBar progressBar = view.findViewById(R.id.progress_bar);

        Bundle bundle = getArguments();
        // If we receive a list as argument, just show that list. No need to load anything.
        if (bundle != null && bundle.getParcelableArrayList(InfoSubItemActivity.INFO_ITEMS) != null) {
            adapter.submitData(bundle.getParcelableArrayList(InfoSubItemActivity.INFO_ITEMS));
            progressBar.setVisibility(View.GONE);
        } else {
            model = new ViewModelProvider(this).get(InfoViewModel.class);
            model.getData().observe(getViewLifecycleOwner(), PartialErrorObserver.with(this::onError));
            model.getData().observe(getViewLifecycleOwner(), new ProgressObserver<>(progressBar));
            model.getData().observe(getViewLifecycleOwner(), new AdapterObserver<>(adapter));
        }
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(requireView(), getString(R.string.error_network), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_refresh, menu);
        requireBaseActivity(this).tintToolbarIcons(menu, R.id.action_refresh);
        if (model == null) {
            menu.findItem(R.id.action_refresh).setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_refresh && model != null) {
            model.requestRefresh();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
