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

package be.ugent.zeus.hydra.wpi.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import androidx.annotation.Nullable;

import java.util.Objects;

import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.databinding.ActivityWpiApiKeyManagementBinding;

/**
 * Activity that allows you to manage your API key.
 * <p> 
 * This is a temporary solution; at some point, we'll need to implement
 * a proper login solution (or do we? it is Zeus after all).
 * 
 * @author Niko Strijbol
 */
public class ApiKeyManagementActivity extends BaseActivity<ActivityWpiApiKeyManagementBinding> implements TextWatcher {
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivityWpiApiKeyManagementBinding::inflate);
        
        binding.apiTab.setText(AccountManager.getTabKey(this));
        binding.apiTap.setText(AccountManager.getTapKey(this));
        binding.apiUsername.setText(AccountManager.getUsername(this));
        binding.doorApiKey.setText(AccountManager.getDoorKey(this));
        setResult(RESULT_OK, new Intent());
    }

    @Override
    protected void onStart() {
        super.onStart();
        binding.apiTab.addTextChangedListener(this);
        binding.apiTap.addTextChangedListener(this);
        binding.apiUsername.addTextChangedListener(this);
        binding.doorApiKey.addTextChangedListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        binding.apiTab.removeTextChangedListener(this);
        binding.apiTap.removeTextChangedListener(this);
        binding.apiUsername.removeTextChangedListener(this);
        binding.doorApiKey.removeTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Do nothing.
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String tab = Objects.requireNonNull(binding.apiTab.getText()).toString();
        String tap = Objects.requireNonNull(binding.apiTap.getText()).toString();
        String user = Objects.requireNonNull(binding.apiUsername.getText()).toString();
        String door = Objects.requireNonNull(binding.doorApiKey.getText()).toString();
        AccountManager.saveData(this, tab, tap, user, door);
    }

    @Override
    public void afterTextChanged(Editable s) {
        // Do nothing.
    }
}
