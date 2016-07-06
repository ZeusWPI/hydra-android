package be.ugent.zeus.hydra.fragments.resto;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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
import be.ugent.zeus.hydra.loader.cache.CacheRequest;
import be.ugent.zeus.hydra.models.resto.RestoMenu;
import be.ugent.zeus.hydra.models.resto.RestoOverview;
import be.ugent.zeus.hydra.requests.resto.RestoMenuOverviewRequest;
import be.ugent.zeus.hydra.utils.DateUtils;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * @author Niko Strijbol
 * @author mivdnber
 */
public class RestoFragment extends LoaderFragment<RestoOverview> {

    private static final String FRAGMENT_TAG = "menu_today_fragment";

    private TextView title;
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
        Drawable menuIcon;
        Drawable sandwichIcon;
        Drawable restoIcon;

        int darkColor = ContextCompat.getColor(getContext(), R.color.ugent_blue_dark);

        //For older API, we do something different
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            VectorDrawableCompat menuCompat = VectorDrawableCompat.create(getResources(), R.drawable.ic_restaurant_menu_40dp, getContext().getTheme());
            VectorDrawableCompat sandwichCompat = VectorDrawableCompat.create(getResources(), R.drawable.ic_sandwich_40dp, getContext().getTheme());
            VectorDrawableCompat restoCompat = VectorDrawableCompat.create(getResources(), R.drawable.ic_explore_40dp, getContext().getTheme());
            //Tint the icon
            menuCompat.setTint(darkColor);
            sandwichCompat.setTint(darkColor);
            restoCompat.setTint(darkColor);
            menuIcon = menuCompat;
            sandwichIcon = sandwichCompat;
            restoIcon = restoCompat;
        } else {
            menuIcon = getContext().getDrawable(R.drawable.ic_restaurant_menu_40dp);
            sandwichIcon = getContext().getDrawable(R.drawable.ic_sandwich_40dp);
            restoIcon = getContext().getDrawable(R.drawable.ic_explore_40dp);
            menuIcon.setTint(darkColor);
            sandwichIcon.setTint(darkColor);
            restoIcon.setTint(darkColor);
        }

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

        FragmentManager m = getChildFragmentManager();

        RestoMenu today = data.get(0);
        if(m.findFragmentByTag(FRAGMENT_TAG) == null) {
            MenuFragment fragment = MenuFragment.newInstance(today);
            FragmentTransaction fragmentTransaction = m.beginTransaction();
            fragmentTransaction.add(R.id.menu_today_card_layout, fragment, FRAGMENT_TAG);
            fragmentTransaction.commit();
        }
        title.setText(String.format(getString(R.string.menu_today_title), DateUtils.getFriendlyDate(today.getDate())));
    }

    /**
     * @return The request that will be executed.
     */
    @Override
    public CacheRequest<RestoOverview> getRequest() {
        return new RestoMenuOverviewRequest();
    }
}