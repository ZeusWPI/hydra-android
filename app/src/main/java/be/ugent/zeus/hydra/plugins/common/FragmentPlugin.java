package be.ugent.zeus.hydra.plugins.common;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * @author Niko Strijbol
 */
@SuppressWarnings("WeakerAccess")
public abstract class FragmentPlugin extends Plugin {

    protected void onAttach(Context context) {

    }

    protected void onActivityCreated(@Nullable Bundle savedInstanceState) {
    }

    protected void onViewStateRestored(@Nullable Bundle savedInstanceState) {
    }

    protected void onDestroyView() {
    }

    protected void onDetach() {
    }
}