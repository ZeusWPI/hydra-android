package be.ugent.zeus.hydra.common.ui.html;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * TODO: revisit this code
 *
 * @author Niko Strijbol
 * @version 20/06/2016
 */
public class PicassoImageGetter implements Html.ImageGetter {

    private static final String TAG = "PicassoImageGetter";

    private final Resources resources;
    private final TextView view;
    private final Context context;

    public PicassoImageGetter(TextView textView, Resources resources, Context context) {
        this.view = textView;
        this.resources = resources;
        // Prevent memory leaks.
        this.context = context.getApplicationContext();
    }

    @Override
    public Drawable getDrawable(final String source) {
        final DrawableWrapper result = new DrawableWrapper(new ColorDrawable());

        // TODO: move out of async task?
        @SuppressLint("StaticFieldLeak") // There is no leak
        AsyncTask<Void, Void, Bitmap> t = new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(final Void... meh) {
                try {
                    return Picasso.get().load(source).get();
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(final Bitmap bitmap) {
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
            }

        };
        t.execute();

        return result;
    }
}