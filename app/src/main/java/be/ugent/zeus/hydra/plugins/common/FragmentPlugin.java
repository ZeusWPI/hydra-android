package be.ugent.zeus.hydra.plugins.common;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Plugin with additional lifecycle callbacks for fragments. If added to an Activity, it will not receive the
 * callbacks. It is generally discouraged from doing this.
 *
 * @author Niko Strijbol
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class FragmentPlugin extends Plugin {

    protected void onAttach(Context context) {}

    protected void onActivityCreated(@Nullable Bundle savedInstanceState) {}

    protected void onViewStateRestored(@Nullable Bundle savedInstanceState) {}

    protected void onDestroyView() {}

    protected void onDetach() {}
}