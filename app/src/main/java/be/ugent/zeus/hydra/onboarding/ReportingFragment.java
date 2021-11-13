/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
        return id == R.id.allowAnalytics;
    }

    private boolean allowsCrashReporting() {
        return crashReportingChooser.isChecked();
    }
}
