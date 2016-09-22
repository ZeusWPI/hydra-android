package be.ugent.zeus.hydra.fragments.resto;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.resto.MenuActivity;
import be.ugent.zeus.hydra.activities.resto.MetaActivity;
import be.ugent.zeus.hydra.activities.resto.SandwichActivity;
import be.ugent.zeus.hydra.fragments.common.CachedLoaderFragment;
import be.ugent.zeus.hydra.models.resto.RestoMenu;
import be.ugent.zeus.hydra.models.resto.RestoOverview;
import be.ugent.zeus.hydra.requests.resto.RestoMenuOverviewRequest;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.utils.ViewUtils;
import be.ugent.zeus.hydra.views.MenuTable;
import org.threeten.bp.LocalDateTime;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * @author Niko Strijbol
 * @author mivdnber
 */
public class RestoFragment extends CachedLoaderFragment<RestoOverview> {

    //The hour after which every resto is closed.
    public static final int CLOSING_HOUR = 20;

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

        viewMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MenuActivity.class));
            }
        });

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

    /**
     * This must be called when data is received that has no errors.
     *
     * @param data The data.
     */
    @Override
    public void receiveData(@NonNull RestoOverview data) {

        //FragmentManager m = getChildFragmentManager();

        //We can't do anything without data.
        if(data.size() < 2) {
            return;
        }

        RestoMenu menu = data.get(0);
        LocalDateTime now = LocalDateTime.now();
        if(now.isAfter(now.withHour(CLOSING_HOUR)) || now.isAfter(menu.getDate().atStartOfDay())) {
            menu = data.get(1);
        }

        table.setMenu(menu);

        title.setText(String.format(getString(R.string.resto_menu_title), DateUtils.getFriendlyDate(menu.getDate())));
    }

    /**
     * @return The request that will be executed.
     */
    @Override
    public RestoMenuOverviewRequest getRequest() {
        return new RestoMenuOverviewRequest();
    }
}