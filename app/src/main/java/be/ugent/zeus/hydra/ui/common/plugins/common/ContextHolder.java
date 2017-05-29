package be.ugent.zeus.hydra.ui.common.plugins.common;

import android.content.Context;
import android.view.View;

/**
 * @author Niko Strijbol
 */
public interface ContextHolder {

    /**
     * @return The context.
     */
    Context getContext();

    /**
     * @return The root view.
     */
    View getRoot();
}
