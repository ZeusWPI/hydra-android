package be.ugent.zeus.hydra.common.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import be.ugent.zeus.hydra.R;

/**
 * Activity to show a dialog.
 *
 * @author Niko Strijbol
 */
public class ExceptionDialogActivity extends AppCompatActivity {

    private static final String ARG_TITLE = "argTitle";
    private static final String ARG_CONTENT = "argContent";
    private static final String ARG_ICON = "argIcon";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setFinishOnTouchOutside(true);
        Intent intent = getIntent();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(intent.getStringExtra(ARG_TITLE));
        builder.setMessage(intent.getStringExtra(ARG_CONTENT));

        if (intent.hasExtra(ARG_ICON)) {
            builder.setIcon(intent.getIntExtra(ARG_ICON, 0));
        }

        builder.setOnDismissListener(dialogInterface -> ExceptionDialogActivity.this.finish());
        builder.setNeutralButton(R.string.ok, (dialogInterface, i) -> ExceptionDialogActivity.this.finish());
        builder.show();
    }
}