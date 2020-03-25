package be.ugent.zeus.hydra.onboarding;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.reporting.Manager;
import be.ugent.zeus.hydra.common.ui.customtabs.CustomTabsHelper;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.heinrichreimersoftware.materialintro.app.SlideFragment;

import static androidx.core.view.ViewCompat.requireViewById;

/**
 * @author Niko Strijbol
 */
public class ReportingFragment extends SlideFragment {

    private static final String PRIVACY_POLICY = "https://hydra.ugent.be/privacy-policy.html";

    private RadioGroup analyticsChooser;
    private SwitchMaterial crashReportingChooser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_onboarding_reporting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        analyticsChooser = requireViewById(view, R.id.analyticsChooser);
        crashReportingChooser = requireViewById(view, R.id.allowCrashReporting);
        addOnNavigationBlockedListener((position, direction) ->
                Toast.makeText(requireContext().getApplicationContext(), R.string.onboarding_reporting_required, Toast.LENGTH_SHORT)
                        .show());
        requireViewById(view, R.id.read_policy).setOnClickListener(v -> CustomTabsHelper.openUri(v.getContext(), Uri.parse(PRIVACY_POLICY)));
        // In debug mode, analytics & crash reporting doesn't do anything.
        if (BuildConfig.DEBUG) {
            crashReportingChooser.setChecked(false);
            crashReportingChooser.setEnabled(false);
            MaterialRadioButton denyAnalyticsButton = requireViewById(view, R.id.disallowAnalytics);
            MaterialRadioButton allowAnalyticsButton = requireViewById(view, R.id.allowAnalytics);
            denyAnalyticsButton.setChecked(true);
            denyAnalyticsButton.setEnabled(false);
            allowAnalyticsButton.setEnabled(false);
        }
    }

    @Override
    public boolean canGoForward() {
        return analyticsChooser != null && analyticsChooser.getCheckedRadioButtonId() != -1;
    }

    @Override
    public void onPause() {
        super.onPause();
        // Save settings if present.
        if (canGoForward()) {
            Manager.saveAnalyticsPermission(requireContext(), allowsAnalytics());
            Manager.saveCrashReportingPermission(requireContext(), allowsCrashReporting());
            Manager.syncPermissions(requireContext());
        }
    }

    private boolean allowsAnalytics() {
        int id = analyticsChooser.getCheckedRadioButtonId();
        switch (id) {
            case R.id.allowAnalytics:
                return true;
            case -1: // Nothing is selected.
            case R.id.disallowAnalytics:
            default:
                return false;
        }
    }

    private boolean allowsCrashReporting() {
        return crashReportingChooser.isChecked();
    }
}
