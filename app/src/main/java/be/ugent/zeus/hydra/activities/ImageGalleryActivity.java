package be.ugent.zeus.hydra.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.ToolbarActivity;
import be.ugent.zeus.hydra.models.schamper.Article;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import eu.fiskur.simpleviewpager.ImageURLLoader;
import eu.fiskur.simpleviewpager.SimpleViewPager;
import uk.co.senab.photoview.PhotoViewAttacher;

import java.util.List;

/**
 * TODO: make our own ViewPager for the images, since this one is not that flexible, nor that stable.
 * @author Niko Strijbol
 */
public class ImageGalleryActivity extends ToolbarActivity {

    public static final String PARCEL_IMAGES = "parcelImages";
    public static final String PARCEL_POSITION = "parcelPosition";

    private static final String TAG = "ImageGalleryActivity";

    private SimpleViewPager simpleViewPager;
    private ProgressBar progressBar;
    private List<SchamperArticleActivity.ArticleImage> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        progressBar = $(R.id.progress_bar);

        //Get the image data
        Intent intent = getIntent();
        images = intent.getParcelableArrayListExtra(PARCEL_IMAGES);
        int startPosition = intent.getIntExtra(PARCEL_POSITION, 0);

        Log.d(TAG, "Got images");

        //Set close icon
        assert getSupportActionBar() != null;
        Drawable icon = DrawableCompat.wrap(ActivityCompat.getDrawable(this, R.drawable.ic_close_24dp));
        DrawableCompat.setTint(icon, ActivityCompat.getColor(this, R.color.white));
        getSupportActionBar().setHomeAsUpIndicator(icon);

        simpleViewPager = $(R.id.simple_view_pager);

        //Get the urls
        String[] urls = new String[images.size()];
        for(int i = 0; i < urls.length; i++) {
            urls[i] = images.get(i).getUrl();
        }

        View bottom = $(R.id.bottom_sheet);
        final BottomSheetBehavior<View> b = BottomSheetBehavior.from(bottom);
        b.setState(BottomSheetBehavior.STATE_EXPANDED);

        simpleViewPager.setImageUrls(urls, new ImageURLLoader() {
            @Override
            public void loadImage(final ImageView imageView, String s) {
                Picasso.with(ImageGalleryActivity.this).load(Article.getLargeImage(s)).into(new Target() {

                    private PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(imageView);

                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageView.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
                        progressBar.setVisibility(View.GONE);
                        photoViewAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                            @Override
                            public void onPhotoTap(View view, float v, float v1) {
                                if(b.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                                    b.setState(BottomSheetBehavior.STATE_EXPANDED);
                                } else {
                                    b.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                }
                            }

                            @Override
                            public void onOutsidePhotoTap() {
                                b.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            }
                        });
                        photoViewAttacher.update();
                        simpleViewPager.invalidate();
                        simpleViewPager.requestLayout();
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        simpleViewPager.setCurrentItem(startPosition, false);

        setTitle((startPosition + 1) + " / " + images.size());
        final TextView caption = $(R.id.caption);
        caption.setText(images.get(startPosition).getCaption());
        simpleViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setTitle((position + 1) + " / " + images.size());
                caption.setText(images.get(position).getCaption());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Say NO to memory leaks.
        if(simpleViewPager != null) {
            simpleViewPager.clearListeners();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Close button
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
