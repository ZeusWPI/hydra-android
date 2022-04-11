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

package be.ugent.zeus.hydra;

import be.ugent.zeus.hydra.common.reporting.Manager;
import jonathanfinerty.once.Once;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.setupPicasso;

/**
 * Disable some libraries we don't use during Robolectric tests.
 *
 * @author Niko Strijbol
 */
public class TestApp extends HydraApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        // Manually set the theme on the context, since we use it's attributes but don't use an Activity.
        setTheme(R.style.Hydra_Material);
    }

    @Override
    protected void onCreateInitialise() {
        Once.initialise(this);

        Manager.saveAnalyticsPermission(this, false);
        Manager.saveCrashReportingPermission(this, false);
        Manager.syncPermissions(this);

        setupPicasso();

        // Do not use SSL
        System.setProperty("javax.net.ssl.trustStore", "NONE");
    }
}
