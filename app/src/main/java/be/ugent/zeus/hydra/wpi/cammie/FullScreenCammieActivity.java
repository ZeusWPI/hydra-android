/*
 * Copyright (c) 2022 Niko Strijbol
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

package be.ugent.zeus.hydra.wpi.cammie;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.databinding.ActivityWpiFullScreenCammieBinding;
import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;

/**
 * View cammie from Hydra.
 *
 * @author Niko Strijbol
 */
public class FullScreenCammieActivity extends BaseActivity<ActivityWpiFullScreenCammieBinding> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivityWpiFullScreenCammieBinding::inflate);
        enterFullScreen();

        binding.cammieViewer.setOnClickListener(v -> {
            if (requireToolbar().isShowing()) {
                enterFullScreen();
            } else {
                exitFullScreen();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadMjpeg();
    }

    @Override
    protected void onStop() {
        binding.cammieViewer.stopPlayback();
        super.onStop();
    }

    private void enterFullScreen() {
        if (getWindow() == null) {
            return;
        }

        WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        // Hide both the status bar and the navigation bar
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
        requireToolbar().hide();
    }

    private void exitFullScreen() {
        if (getWindow() == null) {
            return;
        }

        WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.show(WindowInsetsCompat.Type.systemBars());
        requireToolbar().show();
    }

    private void loadMjpeg() {
        Mjpeg.newInstance()
                .open("https://kelder.zeus.ugent.be/camera/cammie", 5)
                .subscribe(inputStream -> {
                    binding.cammieViewer.setSource(inputStream);
                    binding.cammieViewer.setDisplayMode(DisplayMode.BEST_FIT);
                    binding.cammieViewer.showFps(true);
                });
    }
}
