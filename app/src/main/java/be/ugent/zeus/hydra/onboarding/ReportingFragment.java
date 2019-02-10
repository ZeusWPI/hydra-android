package be.ugent.zeus.hydra.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.Duration;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.reporting.Reporting;

import com.heinrichreimersoftware.materialintro.app.OnNavigationBlockedListener;
import com.heinrichreimersoftware.materialintro.app.SlideFragment;

import static androidx.core.view.ViewCompat.requireViewById;

/**
 * @author Niko Strijbol
 */
public class ReportingFragment extends SlideFragment {

    private RadioGroup analyticsChooser;
    private Switch crashReportingChooser;

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
    }

    @Override
    public boolean canGoForward() {
        return analyticsChooser.getCheckedRadioButtonId() != -1;
    }

    @Override
    public void onPause() {
        super.onPause();
        // Save settings if present.
        if (canGoForward()) {
            Reporting.saveAnalyticsPermission(requireContext(), allowsAnalytics());
            Reporting.saveCrashReportingPermission(requireContext(), allowsCrashReporting());
            Reporting.syncPermissions(requireContext());
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