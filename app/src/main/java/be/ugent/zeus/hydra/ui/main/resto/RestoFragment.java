package be.ugent.zeus.hydra.ui.main.resto;

import android.support.v4.app.Fragment;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.resto.RestoMenu;
import be.ugent.zeus.hydra.repository.observers.ErrorObserver;
import be.ugent.zeus.hydra.repository.observers.ProgressObserver;
import be.ugent.zeus.hydra.repository.observers.SuccessObserver;
import be.ugent.zeus.hydra.ui.common.ViewUtils;
import be.ugent.zeus.hydra.ui.common.widgets.MenuTable;
import be.ugent.zeus.hydra.ui.resto.RestoLocationActivity;
import be.ugent.zeus.hydra.ui.resto.SandwichActivity;
import be.ugent.zeus.hydra.ui.resto.menu.MenuActivity;
import be.ugent.zeus.hydra.utils.DateUtils;

/**
 * @author Niko Strijbol
 * @author mivdnber
 */
public class RestoFragment extends Fragment {

    private static final String TAG = "RestoFragment";

    private TextView title;
    private MenuTable table;
    private Button viewMenu;
    private Button viewSandwich;
    private Button viewResto;
    private TextView errorView;
    private CardView todayCard;
    private View menuLayout;

    private RestoViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_resto, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        table = view.findViewById(R.id.menu_table);
        viewMenu = view.findViewById(R.id.home_resto_view);
        viewSandwich = view.findViewById(R.id.home_resto_view_sandwich);
        viewResto = view.findViewById(R.id.home_resto_view_resto);
        title = view.findViewById(R.id.menu_today_card_title);
        errorView = view.findViewById(R.id.error_view);
        errorView.setMovementMethod(LinkMovementMethod.getInstance());
        todayCard = view.findViewById(R.id.menu_today_card);
        menuLayout = view.findViewById(R.id.menu_today_card_layout);

        setIcons();

        viewSandwich.setOnClickListener(v -> startActivity(new Intent(getContext(), SandwichActivity.class)));
        viewResto.setOnClickListener(v -> startActivity(new Intent(getContext(), RestoLocationActivity.class)));
        todayCard.setOnClickListener(v -> startActivity(new Intent(getContext(), MenuActivity.class)));

        viewModel = ViewModelProviders.of(this).get(RestoViewModel.class);
        viewModel.getData().observe(this, ErrorObserver.with(this::onError));
        viewModel.getData().observe(this, new ProgressObserver<>(view.findViewById(R.id.progress_bar)));
        viewModel.getData().observe(this, SuccessObserver.with(this::onSuccess));
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
        viewMenu.setEnabled(false);
        errorView.setVisibility(View.VISIBLE);
        todayCard.setClickable(false);
        menuLayout.setVisibility(View.GONE);
        Snackbar.make(getView(), getString(R.string.failure), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.again), v -> viewModel.onRefresh())
                .show();
    }

    private void onSuccess(RestoMenu menu) {
        table.setMenu(menu);
        title.setText(String.format(getString(R.string.resto_menu_title_short), DateUtils.getFriendlyDate(menu.getDate())));
        viewMenu.setEnabled(true);
        menuLayout.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
        todayCard.setClickable(true);
        viewMenu.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MenuActivity.class);
            intent.putExtra(MenuActivity.ARG_DATE, menu.getDate());
            startActivity(intent);
        });
    }
}