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

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.network.NetworkState;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.databinding.ActivityWpiCammieBinding;
import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;

/**
 * View cammie from Hydra.
 *
 * @author Niko Strijbol
 */
public class CammieActivity extends BaseActivity<ActivityWpiCammieBinding> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivityWpiCammieBinding::inflate);

        CammieViewModel vm = new ViewModelProvider(this).get(CammieViewModel.class);

        binding.moveNorthWest.setOnClickListener(v -> vm.startRequest(MoveRequest.Command.NORTH_WEST));
        binding.moveNorth.setOnClickListener(v -> vm.startRequest(MoveRequest.Command.NORTH));
        binding.moveNorthEast.setOnClickListener(v -> vm.startRequest(MoveRequest.Command.NORTH_EAST));
        binding.moveWest.setOnClickListener(v -> vm.startRequest(MoveRequest.Command.WEST));
        binding.moveEast.setOnClickListener(v -> vm.startRequest(MoveRequest.Command.EAST));
        binding.moveSouthWest.setOnClickListener(v -> vm.startRequest(MoveRequest.Command.SOUTH_WEST));
        binding.moveSouth.setOnClickListener(v -> vm.startRequest(MoveRequest.Command.SOUTH));
        binding.moveSouthEast.setOnClickListener(v -> vm.startRequest(MoveRequest.Command.SOUTH_EAST));
        binding.bigTable.setOnClickListener(v -> vm.startRequest(MoveRequest.Command.BIG_TABLE));
        binding.smallTable.setOnClickListener(v -> vm.startRequest(MoveRequest.Command.SMALL_TABLE));
        binding.sofa.setOnClickListener(v -> vm.startRequest(MoveRequest.Command.SOFA));
        binding.door.setOnClickListener(v -> vm.startRequest(MoveRequest.Command.DOOR));
        binding.message.setOnClickListener(v -> {
            ChatDialogFragment fragment = new ChatDialogFragment();
            fragment.show(getSupportFragmentManager(), "messageSend");
        });

        vm.getControlNetworkState().observe(this, networkState -> {
            boolean enabled = networkState == null || networkState == NetworkState.IDLE;

            // If the buttons are currently disabled, and we want to re-enable them,
            // we add a delay of about 1 second.
            // In the other case, just do it.
            if (enabled && !binding.moveNorthWest.isEnabled()) {
                new Handler().postDelayed(() -> toggleButtons(true), 1000);
            } else {
                toggleButtons(enabled);
            }
        });
    }

    private void toggleButtons(boolean enabled) {
        binding.moveNorthWest.setEnabled(enabled);
        binding.moveNorth.setEnabled(enabled);
        binding.moveNorthEast.setEnabled(enabled);
        binding.moveWest.setEnabled(enabled);
        binding.moveEast.setEnabled(enabled);
        binding.moveSouthWest.setEnabled(enabled);
        binding.moveSouth.setEnabled(enabled);
        binding.moveSouthEast.setEnabled(enabled);
        binding.bigTable.setEnabled(enabled);
        binding.smallTable.setEnabled(enabled);
        binding.sofa.setEnabled(enabled);
        binding.door.setEnabled(enabled);
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

    private void loadMjpeg() {
        Mjpeg.newInstance()
                .open("https://kelder.zeus.ugent.be/camera/cammie", 5)
                .subscribe(inputStream -> {
                    binding.cammieViewer.setSource(inputStream);
                    binding.cammieViewer.setDisplayMode(DisplayMode.BEST_FIT);
                    binding.cammieViewer.showFps(true);
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_wpi_cammie, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_open_fullscreen) {
            Intent intent = new Intent(this, FullScreenCammieActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
