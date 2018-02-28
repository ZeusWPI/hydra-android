package be.ugent.zeus.hydra.utils;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;

import be.ugent.zeus.hydra.common.ui.BaseActivity;

/**
 * @author Niko Strijbol
 */
public class FragmentUtils {

    @NonNull
    public static View requireView(Fragment fragment) {
        View view = fragment.getView();
        if (view == null) {
            throw new IllegalStateException("The view is null. Method called when not allowed.");
        } else {
            return view;
        }
    }

    @NonNull
    public static BaseActivity requireBaseActivity(Fragment fragment) {
        Activity activity = fragment.requireActivity();
        if (activity instanceof BaseActivity) {
            return (BaseActivity) activity;
        } else {
            throw new IllegalStateException("This method can only be used if the Fragment is attached to a BaseActivity.");
        }
    }

    @NonNull
    public static Bundle requireArguments(Fragment fragment) {
        Bundle arguments = fragment.getArguments();
        if (arguments == null) {
            throw new IllegalStateException("The Fragment was not properly initialised and misses arguments.");
        } else {
            return arguments;
        }
    }
}