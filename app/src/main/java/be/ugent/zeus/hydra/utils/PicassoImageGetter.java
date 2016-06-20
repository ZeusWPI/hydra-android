package be.ugent.zeus.hydra.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.graphics.drawable.DrawableWrapper;
import android.text.Html;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

/**
 * @author Niko Strijbol
 * @version 20/06/2016
 */
public class PicassoImageGetter implements Html.ImageGetter {

    private final Resources resources;
    private final TextView textView;
    private final Context context;

    public PicassoImageGetter(TextView textView, Resources resources, Context context) {
        this.textView = textView;
        this.resources = resources;
        //Prevent memory leaks.
        this.context = context.getApplicationContext();
    }

    @Override
    public Drawable getDrawable(final String source) {
        final DrawableWrapper result = new DrawableWrapper(new ColorDrawable());

        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(final Void... meh) {
                try {
                    return Picasso.with(context).load(source).get();
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

                    textView.setText(textView.getText()); // invalidate() doesn't work correctly...
                } catch (Exception e) {
                /* nom nom nom*/
                }
            }

        }.execute();

        return result;
    }
}