package be.ugent.zeus.hydra.common.utils;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import be.ugent.zeus.hydra.common.ui.BaseActivity;

/**
 * @author Niko Strijbol
 */
public class FragmentUtils {

    @NonNull
    public static BaseActivity<?> requireBaseActivity(Fragment fragment) {
        Activity activity = fragment.requireActivity();
        if (activity instanceof BaseActivity) {
            return (BaseActivity<?>) activity;
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
