package be.ugent.zeus.hydra.minerva.calendar.sync;

import android.Manifest;
import android.accounts.Account;
import android.content.ContentResolver;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.minerva.account.AccountUtils;
import be.ugent.zeus.hydra.minerva.account.MinervaConfig;
import be.ugent.zeus.hydra.minerva.common.sync.MinervaAdapter;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import jonathanfinerty.once.Once;

import java.util.List;

/**
 * Activity to ask for the permissions necessary to run the calendar synchronisation.
 *
 * @author Niko Strijbol
 */
public class CalendarPermissionActivity extends AppCompatActivity implements MultiplePermissionsListener {

    private static final String TAG = "CalendarPermissionActiv";

    public static final String MINERVA_CALENDAR_STOP_ASKING = "pref_minerva_stop_asking";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_CALENDAR,
                        Manifest.permission.WRITE_CALENDAR
                ).withListener(this)
                .check();
    }

    @Override
    public void onPermissionsChecked(MultiplePermissionsReport report) {
        // Don't ask again if the user denied the permission.
        if (report.isAnyPermissionPermanentlyDenied()) {
            Once.markDone(MINERVA_CALENDAR_STOP_ASKING);
            return;
        }

        if (!report.areAllPermissionsGranted()) {
            // Fix for a rare bug where the activity is finished before we show the dialog; just give up in that case.
            if (this.isFinishing()) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(R.string.minerva_calendar_permission_title);
            builder.setMessage(R.string.minerva_calendar_permission_text);

            builder.setOnDismissListener(dialogInterface -> finish());
            builder.setNeutralButton(android.R.string.ok, (dialogInterface, i) -> finish());
            builder.show();
        } else {
            // Schedule new sync
            Account account = AccountUtils.getAccount(this);
            Bundle bundle = new Bundle();
            Log.d(TAG, "Requesting calendar sync...");
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            // Don't do the announcements.
            bundle.putBoolean(MinervaAdapter.SYNC_ANNOUNCEMENTS, false);
            ContentResolver.requestSync(account, MinervaConfig.SYNC_AUTHORITY, bundle);
            finish();
        }
    }

    @Override
    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.minerva_calendar_permission_title);
        builder.setMessage(R.string.minerva_calendar_permission_rationale);

        builder.setOnDismissListener(dialogInterface -> token.continuePermissionRequest());
        builder.setNeutralButton(android.R.string.ok, (dialogInterface, i) -> token.continuePermissionRequest());
        builder.show();
        builder.setOnCancelListener(dialog -> token.continuePermissionRequest());
    }
}