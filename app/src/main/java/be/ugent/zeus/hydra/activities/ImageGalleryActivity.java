package be.ugent.zeus.hydra.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.ToolbarActivity;
import be.ugent.zeus.hydra.models.schamper.Article;
import be.ugent.zeus.hydra.models.schamper.ArticleImage;
import be.ugent.zeus.hydra.utils.ViewUtils;
import be.ugent.zeus.hydra.viewpager.ImagePagerAdapter;
import uk.co.senab.photoview.PhotoViewAttacher;

import java.util.ArrayList;
import java.util.List;

/**
 * Show images.
 *
 * Note that this uses {@link be.ugent.zeus.hydra.views.HackyViewPager}, since there still is a framework issue.
 *
 * @author Niko Strijbol
 */
public class ImageGalleryActivity extends ToolbarActivity {

    public static final String PARCEL_IMAGES = "parcelImages";
    public static final String PARCEL_POSITION = "parcelPosition";

    private static final String TAG = "ImageGalleryActivity";

    private ViewPager pager;
    private List<ArticleImage> images;
    private BottomSheetBehavior<CardView> bottomBehavior;
    private TextView bottomCaption;
    private ImageView previous;
    private ImageView next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        bottomCaption = $(R.id.caption);
        bottomBehavior = BottomSheetBehavior.from(this.<CardView>$(R.id.bottom_sheet));
        previous = $(R.id.image_previous);
        next = $(R.id.image_next);

        //Get the image data
        Intent intent = getIntent();
        images = intent.getParcelableArrayListExtra(PARCEL_IMAGES);
        int startPosition = intent.getIntExtra(PARCEL_POSITION, 0);
        Log.d(TAG, "Got images");

        //Set close icon
        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeAsUpIndicator(ViewUtils.getTintedVectorDrawable(this, R.drawable.ic_close, R.color.white));

        //The pager.
        pager = $(R.id.view_pager);

        //Get the urls
        List<String> urls = new ArrayList<>(images.size());
        for(ArticleImage articleImage: images) {
            urls.add(Article.getLargeImage(articleImage.getUrl()));
        }

        //Initialise the bottom sheet state
        setBottomSheetText(startPosition);

        PhotoViewAttacher.OnPhotoTapListener listener = new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float v, float v1) {
                hideOrShowBottomSheet(pager.getCurrentItem());
            }

            @Override
            public void onOutsidePhotoTap() {
                bottomBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        };

        pager.setAdapter(new ImagePagerAdapter(this, urls, listener));

        pager.setCurrentItem(startPosition, false);

        setTitle((startPosition + 1) + " / " + images.size());

        configureNavigation(startPosition);

        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setTitle((position + 1) + " / " + images.size());
                setBottomSheetText(position);
                configureNavigation(position);
            }
        });

        //Do not check, make sure this not fireable when there is nothing.
        next.setOnClickListener(view -> pager.setCurrentItem(pager.getCurrentItem() + 1));

        previous.setOnClickListener(view -> pager.setCurrentItem(pager.getCurrentItem() - 1));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(pager != null) {
            pager.clearOnPageChangeListeners();
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

    /**
     * Configure the previous/next button of the BottomSheet.
     *
     * @param position The current position.
     */
    private void configureNavigation(int position) {
        if (position == 0) {
            previous.setVisibility(View.GONE);
        } else {
            previous.setVisibility(View.VISIBLE);
        }

        if (position == images.size() - 1) {
            next.setVisibility(View.GONE);
        } else {
            next.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Toggle the bottom sheet.
     *
     * @param position The position.
     */
    private void hideOrShowBottomSheet(int position) {

        String caption = images.get(position).getCaption();

        //If there is no caption, hide the bottom sheet.
        if(caption == null || caption.isEmpty()) {
            bottomBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            //Toggle the sheet depending on the current state.
            if(bottomBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                bottomBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }
    }

    /**
     * Set the text of the bottom sheet.
     *
     * @param position Position.
     */
    private void setBottomSheetText(int position) {
        String caption = images.get(position).getCaption();
        bottomCaption.setText(caption);

        //If there is no caption, hide the bottom sheet.
        if(caption == null || caption.isEmpty()) {
            bottomBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            bottomBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }
}