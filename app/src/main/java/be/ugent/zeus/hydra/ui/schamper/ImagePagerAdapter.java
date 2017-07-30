package be.ugent.zeus.hydra.ui.schamper;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import uk.co.senab.photoview.PhotoViewAttacher;

import java.util.List;

/**
 * A PagerAdapter that works with images. This adapter will use {@link PhotoViewAttacher}
 * as views and will use Picasso to get the images. The images will not be kept in memory, since they might be rather
 * large. Picasso will cache them on disk however, so they are not reloaded every time.
 *
 * Note: below are implementation details. While useful to know, they are no guarantees about them at all. They may
 * change at any time.
 *
 * The viewpager uses a {@link android.widget.FrameLayout} to display a spinner, and when the image is loaded, it
 * is hidden.
 *
 * @author Niko Strijbol
 */
public class ImagePagerAdapter extends PagerAdapter {

    private static final String TAG = "ImagePagerAdapter";

    private Context context;
    private List<String> images;
    private PhotoViewAttacher.OnPhotoTapListener listener;
    private SparseArray<PhotoViewAttacher> attachers = new SparseArray<>();

    /**
     * @param context The activity that uses the adapter.
     * @param images The image URL's.
     * @param listener A listener for the photo.
     */
    public ImagePagerAdapter(Context context, List<String> images, PhotoViewAttacher.OnPhotoTapListener listener) {
        this.context = context;
        this.images = images;
        this.listener = listener;
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return images.size();
    }

    /**
     * Determines whether a page View is associated with a specific key object
     * as returned by {@link #instantiateItem(ViewGroup, int)}. This method is
     * required for a PagerAdapter to function properly.
     *
     * @param view   Page View to check for association with {@code object}
     * @param object Object to check for association with {@code view}
     *
     * @return true if {@code view} is associated with the key object {@code object}
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /**
     * Create the page for the given position.  The adapter is responsible
     * for adding the view to the container given here, although it only
     * must ensure this is done by the time it returns from
     * {@link #finishUpdate(ViewGroup)}.
     *
     * @param container The containing View in which the page will be shown.
     * @param position The page position to be instantiated.
     * @return Returns an Object representing the new page.  This does not
     * need to be a View, but can be some other container of the page.
     */
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        FrameLayout layout = new FrameLayout(this.context);
        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        //Progress bar
        final ProgressBar progressBar = new ProgressBar(this.context);
        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.gravity = Gravity.CENTER_HORIZONTAL;
        progressBar.setLayoutParams(p);

        //Image view
        final ImageView imageView = new ImageView(this.context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        final PhotoViewAttacher attacher = new PhotoViewAttacher(imageView);
        attachers.put(position, attacher);
        if(listener != null) {
            attacher.setOnPhotoTapListener(listener);
        }

        layout.addView(imageView);
        layout.addView(progressBar);
        imageView.setVisibility(View.GONE);

        Picasso.with(this.context).load(images.get(position)).tag(position).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "Loaded image #" + position);
                imageView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                attacher.update();
            }

            @Override
            public void onError() {
                Log.i(TAG, "Image error");
                progressBar.setVisibility(View.GONE);
            }
        });

        container.addView(layout);
        return layout;
    }

    /**
     * Remove a page for the given position.  The adapter is responsible
     * for removing the view from its container, although it only must ensure
     * this is done by the time it returns from {@link #finishUpdate(ViewGroup)}.
     *
     * @param container The containing View from which the page will be removed.
     * @param position The page position to be removed.
     * @param object The same object that was returned by
     * {@link #instantiateItem(View, int)}.
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        attachers.get(position).cleanup();
        attachers.remove(position);
        //Stop picasso
        Picasso.with(context).cancelTag(position);
        //Remove view
        container.removeView((View) object);
    }
}