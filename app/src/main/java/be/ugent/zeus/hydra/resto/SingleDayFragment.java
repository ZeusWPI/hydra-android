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

package be.ugent.zeus.hydra.resto;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.reporting.BaseEvents;
import be.ugent.zeus.hydra.common.reporting.Event;
import be.ugent.zeus.hydra.common.reporting.Reporting;
import be.ugent.zeus.hydra.common.utils.NetworkUtils;
import be.ugent.zeus.hydra.databinding.FragmentMenuBinding;

/**
 * Displays the resto menu for one day. This is mostly used in the view pager.
 *
 * @author Niko Strijbol
 */
public class SingleDayFragment extends Fragment {

    private static final String ARG_DATA_MENU = "resto_menu";
    private static final String URL = "https://studentenrestaurants.ugent.be/";

    private RestoMenu data;
    private FragmentMenuBinding binding;

    public static SingleDayFragment newInstance(RestoMenu menu) {
        SingleDayFragment fragment = new SingleDayFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_DATA_MENU, menu);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = requireArguments().getParcelable(ARG_DATA_MENU);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMenuBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.menuTable.setMenu(data);
        binding.orderButton.setOnClickListener(v -> {
            String url = URL + v.getContext().getString(R.string.value_info_endpoint);
            NetworkUtils.maybeLaunchBrowser(v.getContext(), url);
        });
    }

    public RestoMenu getData() {
        return data;
    }

    public void setData(RestoMenu data) {
        this.data = data;
    }

    @Override
    public void onResume() {
        super.onResume();
        Reporting.getTracker(getContext()).log(new MenuEvent(data));
    }

    private static class MenuEvent implements Event {

        private final RestoMenu menu;

        private MenuEvent(RestoMenu menu) {
            this.menu = menu;
        }

        @Nullable
        @Override
        public Bundle getParams() {
            BaseEvents.Params names = Reporting.getEvents().params();
            Bundle params = new Bundle();
            params.putString(names.itemCategory(), RestoMenu.class.getSimpleName());
            params.putString(names.itemId(), menu.getDate().toString());
            params.putString("date", menu.getDate().toString());
            return params;
        }

        @Nullable
        @Override
        public String getEventName() {
            return Reporting.getEvents().viewItem();
        }
    }
}
