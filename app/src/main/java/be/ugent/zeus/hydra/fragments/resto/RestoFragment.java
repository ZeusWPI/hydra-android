package be.ugent.zeus.hydra.fragments.resto;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.resto.MenuActivity;
import be.ugent.zeus.hydra.activities.resto.MetaActivity;
import be.ugent.zeus.hydra.activities.resto.SandwichActivity;
import be.ugent.zeus.hydra.fragments.common.LoaderFragment;
import be.ugent.zeus.hydra.loaders.RequestAsyncTaskLoader;
import be.ugent.zeus.hydra.loaders.ThrowableEither;
import be.ugent.zeus.hydra.models.resto.RestoMenu;
import be.ugent.zeus.hydra.models.resto.RestoOverview;
import be.ugent.zeus.hydra.requests.common.ProcessableCacheRequest;
import be.ugent.zeus.hydra.requests.resto.RestoMenuOverviewRequest;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.utils.ViewUtils;
import be.ugent.zeus.hydra.views.MenuTable;

import java.util.ArrayList;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * @author Niko Strijbol
 * @author mivdnber
 */
public class RestoFragment extends LoaderFragment<ArrayList<RestoMenu>> {

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

        setIcons();

        viewSandwich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SandwichActivity.class));
            }
        });

        viewResto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MetaActivity.class));
            }
        });

        title = $(view, R.id.menu_today_card_title);

        view.findViewById(R.id.menu_today_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MenuActivity.class));
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

        Drawable menuIcon = ViewUtils.getTintedVectorDrawable(c, R.drawable.ic_restaurant_menu_40dp, color);
        Drawable sandwichIcon = ViewUtils.getTintedVectorDrawable(c, R.drawable.ic_sandwich_40dp, color);
        Drawable restoIcon = ViewUtils.getTintedVectorDrawable(c, R.drawable.ic_explore_40dp, color);

        viewMenu.setCompoundDrawablesWithIntrinsicBounds(null, menuIcon, null, null);
        viewSandwich.setCompoundDrawablesWithIntrinsicBounds(null, sandwichIcon, null, null);
        viewResto.setCompoundDrawablesWithIntrinsicBounds(null, restoIcon, null, null);
    }

    @Override
    public void receiveData(@NonNull ArrayList<RestoMenu> data) {

        //Check that we have at least one menu
        //TODO: show error
        if(data.size() < 1) {
            return;
        }

        final RestoMenu menu = data.get(0);

        table.setMenu(menu);
        title.setText(String.format(getString(R.string.resto_menu_title), DateUtils.getFriendlyDate(menu.getDate())));

        viewMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MenuActivity.class);
                intent.putExtra(MenuActivity.ARG_DATE, menu.getDate());
                startActivity(intent);
            }
        });
    }

    /**
     * Do the filtering of the menu's in the background as well.
     */
    @Override
    public Loader<ThrowableEither<ArrayList<RestoMenu>>> getLoader() {
        return new RequestAsyncTaskLoader<>(
                new ProcessableCacheRequest<RestoOverview, ArrayList<RestoMenu>>(getContext(), new RestoMenuOverviewRequest(), shouldRenew) {
                    @NonNull
                    @Override
                    protected ArrayList<RestoMenu> transform(@NonNull RestoOverview data) {
                        return RestoOverview.filter(data, getContext());
                    }
                }, getContext()
        );
    }
}