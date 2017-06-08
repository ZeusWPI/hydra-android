package be.ugent.zeus.hydra.ui.main.resto;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.resto.RestoMenu;
import be.ugent.zeus.hydra.repository.RefreshBroadcast;
import be.ugent.zeus.hydra.repository.observers.ProgressObserver;
import be.ugent.zeus.hydra.repository.observers.SuccessObserver;
import be.ugent.zeus.hydra.repository.utils.ErrorUtils;
import be.ugent.zeus.hydra.ui.common.ViewUtils;
import be.ugent.zeus.hydra.ui.common.widgets.MenuTable;
import be.ugent.zeus.hydra.ui.resto.RestoLocationActivity;
import be.ugent.zeus.hydra.ui.resto.SandwichActivity;
import be.ugent.zeus.hydra.ui.resto.menu.MenuActivity;
import be.ugent.zeus.hydra.utils.DateUtils;

import static be.ugent.zeus.hydra.ui.common.ViewUtils.$;

/**
 * @author Niko Strijbol
 * @author mivdnber
 */
public class RestoFragment extends LifecycleFragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "RestoFragment";

    private TextView title;
    private MenuTable table;
    private Button viewMenu;
    private Button viewSandwich;
    private Button viewResto;

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
        title = $(view, R.id.menu_today_card_title);

        setIcons();

        viewSandwich.setOnClickListener(v -> startActivity(new Intent(getContext(), SandwichActivity.class)));
        viewResto.setOnClickListener(v -> startActivity(new Intent(getContext(), RestoLocationActivity.class)));
        view.findViewById(R.id.menu_today_card).setOnClickListener(v -> startActivity(new Intent(getContext(), MenuActivity.class)));

        RestoViewModel model = ViewModelProviders.of(this).get(RestoViewModel.class);
        ErrorUtils.filterErrors(model.getData()).observe(this, this::onError);
        model.getData().observe(this, new ProgressObserver<>($(view, R.id.progress_bar)));
        model.getData().observe(this, new SuccessObserver<RestoMenu>() {
            @Override
            protected void onSuccess(RestoMenu data) {
                RestoFragment.this.onSuccess(data);
            }
        });
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

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(getView(), getString(R.string.failure), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.again), v -> onRefresh())
                .show();
    }

    private void onSuccess(RestoMenu menu) {
        table.setMenu(menu);
        title.setText(String.format(getString(R.string.resto_menu_title_short), DateUtils.getFriendlyDate(menu.getDate())));

        viewMenu.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MenuActivity.class);
            intent.putExtra(MenuActivity.ARG_DATE, menu.getDate());
            startActivity(intent);
        });
    }

    @Override
    public void onRefresh() {
        RefreshBroadcast.broadcastRefresh(getContext(), true);
    }
}