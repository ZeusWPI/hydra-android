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

package be.ugent.zeus.hydra.common.barcode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.Nullable;

import java.util.function.Consumer;

import be.ugent.zeus.hydra.common.scanner.BarcodeScanner;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * @author Niko Strijbol
 */
class OpenBarcodeScanner implements BarcodeScanner {

    @Override
    public boolean needsActivity() {
        return true;
    }

    @Override
    public Intent getActivityIntent(Activity activity) {
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES);
        return integrator.createScanIntent();
    }
    
    public int getRequestCode() {
        return IntentIntegrator.REQUEST_CODE;
    }
    
    @Override
    @Nullable
    public String interpretActivityResult(Intent data, int resultCode) {
        IntentResult result = IntentIntegrator.parseActivityResult(resultCode, data);
        return result.getContents();
    }

    @Override
    public void getBarcode(Context context, Consumer<String> onSuccess, Consumer<Exception> onError) {
        throw new UnsupportedOperationException("This Barcode Scanner requires an activity.");
    }
}
