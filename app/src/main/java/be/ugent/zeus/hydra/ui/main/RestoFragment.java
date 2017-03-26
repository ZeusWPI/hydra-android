package be.ugent.zeus.hydra.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.resto.RestoMenu;
import be.ugent.zeus.hydra.data.network.CachedRequest;
import be.ugent.zeus.hydra.data.network.ListRequest;
import be.ugent.zeus.hydra.data.network.requests.resto.FilteredMenuRequest;
import be.ugent.zeus.hydra.data.network.requests.resto.MenuRequest;
import be.ugent.zeus.hydra.ui.common.plugins.RequestPlugin;
import be.ugent.zeus.hydra.ui.common.plugins.common.Plugin;
import be.ugent.zeus.hydra.ui.common.plugins.common.PluginFragment;
import be.ugent.zeus.hydra.ui.preferences.RestoPreferenceFragment;
import be.ugent.zeus.hydra.ui.resto.RestoLocationActivity;
import be.ugent.zeus.hydra.ui.resto.SandwichActivity;
import be.ugent.zeus.hydra.ui.resto.menu.MenuActivity;
import be.ugent.zeus.hydra.ui.common.widgets.MenuTable;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.ui.common.ViewUtils;

import java.util.List;

import static be.ugent.zeus.hydra.ui.common.ViewUtils.$;

/**
 * @author Niko Strijbol
 * @author mivdnber
 */
public class RestoFragment extends PluginFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "RestoFragment";

    private TextView title;
    private MenuTable table;
    private Button viewMenu;
    private Button viewSandwich;
    private Button viewResto;

    private boolean preferencesUpdated;

    private RequestPlugin<List<RestoMenu>> plugin = new RequestPlugin<>((c, r) -> new FilteredMenuRequest(
            c,
            new ListRequest<>(new CachedRequest<>(c, new MenuRequest(c), r
            ))
    ));

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
        plugin.defaultError().enableProgress().setSuccessCallback(this::receiveData);
        plugins.add(plugin);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_resto, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        table = $(view, R.id.menu_table);
        viewMenu = $(view, R.id.home_resto_view);
        viewSandwich = $(view, R.id.home_resto_view_sandwich);
        viewResto = $(view, R.id.home_resto_view_resto);

        setIcons();

        viewSandwich.setOnClickListener(v -> startActivity(new Intent(getContext(), SandwichActivity.class)));

        viewResto.setOnClickListener(v -> startActivity(new Intent(getContext(), RestoLocationActivity.class)));

        title = $(view, R.id.menu_today_card_title);

        view.findViewById(R.id.menu_today_card).setOnClickListener(v -> startActivity(new Intent(getContext(), MenuActivity.class)));
        PreferenceManager.getDefaultSharedPreferences(getContext()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (preferencesUpdated) {
            plugin.refresh();
            preferencesUpdated = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        preferencesUpdated = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(getContext()).unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * Once the minimumSdk is over 21, we can use xml instead. Or the support library should re-enable the vector
     * support everywhere.
     *
     * @see <a href="https://goo.gl/IfpPYW">issue 205236</a>
     */
    private void setIcons() {

        Context c = getContext();
        int color = R.color.ugent_blue_dark;

        Drawable menuIcon = ViewUtils.getTintedVectorDrawable(c, R.drawable.btn_restaurant_menu, color);
        Drawable sandwichIcon = ViewUtils.getTintedVectorDrawable(c, R.drawable.btn_sandwich, color);
        Drawable restoIcon = ViewUtils.getTintedVectorDrawable(c, R.drawable.btn_explore, color);

        viewMenu.setCompoundDrawablesWithIntrinsicBounds(null, menuIcon, null, null);
        viewSandwich.setCompoundDrawablesWithIntrinsicBounds(null, sandwichIcon, null, null);
        viewResto.setCompoundDrawablesWithIntrinsicBounds(null, restoIcon, null, null);
    }

    private void receiveData(List<RestoMenu> data) {
        //Check that we have at least one menu
        //TODO: show error
        if (data.size() < 1) {
            return;
        }

        final RestoMenu menu = data.get(0);

        table.setMenu(menu);
        title.setText(String.format(getString(R.string.resto_menu_title_short), DateUtils.getFriendlyDate(menu.getDate())));

        viewMenu.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MenuActivity.class);
            intent.putExtra(MenuActivity.ARG_DATE, menu.getDate());
            startActivity(intent);
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(RestoPreferenceFragment.PREF_RESTO)) {
            preferencesUpdated = true;
        }
    }
}