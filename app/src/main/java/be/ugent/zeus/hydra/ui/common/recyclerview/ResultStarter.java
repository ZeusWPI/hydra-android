package be.ugent.zeus.hydra.ui.common.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Enables other components to call {@link android.app.Activity#startActivityForResult(Intent, int)} or
 * {@link android.app.Fragment#startActivityForResult(Intent, int)}.
 *
 * @author Niko Strijbol
 */
public interface ResultStarter {

    public static final int RESULT_ID = 252525;
    public static final String RESULT_ACTION = "be.ugent.zeus.hydra.result";

    Context getContext();

    void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options);

    void startActivityForResult(Intent intent, int requestCode);

    int getRequestCode();
}