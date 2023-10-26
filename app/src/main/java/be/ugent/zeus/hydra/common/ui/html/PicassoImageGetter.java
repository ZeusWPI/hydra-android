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

package be.ugent.zeus.hydra.common.ui.html;

import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import be.ugent.zeus.hydra.common.utils.ThreadingUtils;
import com.squareup.picasso.Picasso;

/**
 * Use {@link Picasso} to load and display images when using {@link Html}.
 *
 * @author Niko Strijbol
 */
public class PicassoImageGetter implements Html.ImageGetter {

    private static final String TAG = "PicassoImageGetter";

    private final Resources resources;
    private final TextView view;

    public PicassoImageGetter(TextView textView, Resources resources) {
        this.view = textView;
        this.resources = resources;
    }

    @Override
    public Drawable getDrawable(final String source) {
        final DrawableWrapper result = new DrawableWrapper(new ColorDrawable());
        ThreadingUtils.executeWithResult(() -> {
            try {
                return Picasso.get().load(source).get();
            } catch (Exception e) {
                return null;
            }
        }, bitmap -> {
            try {
                final BitmapDrawable drawable = new BitmapDrawable(resources, bitmap);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                result.setWrappedDrawable(drawable);
                result.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                result.invalidateSelf();

                view.setText(view.getText());
            } catch (Exception e) {
                Log.w(TAG, "Error while setting image", e);
            }
        });

        return result;
    }
}
