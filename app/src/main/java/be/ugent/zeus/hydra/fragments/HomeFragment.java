package be.ugent.zeus.hydra.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.RestoItem;
import be.ugent.zeus.hydra.models.RestoMenu;

/**
 * Created by silox on 17/10/15.
 */

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.homefragment_view, container, false);


        // resto items for debug purposes
        RestoItem[] meat = new RestoItem[3];
        meat[0] = new RestoItem("spaghetti", "3.00", true);
        meat[1] = new RestoItem("kaaskroket", "3.30", true);
        meat[2] = new RestoItem("vegetarisch", "4.00", true);
        String[] vegetables = new String[2];
        vegetables[0] = "wortel";
        vegetables[1] = "prei";
        RestoMenu menu = new RestoMenu();
        menu.setMeat(meat);
        menu.setOpen(true);
        menu.setVegetables(vegetables);




        return view;
    }}
