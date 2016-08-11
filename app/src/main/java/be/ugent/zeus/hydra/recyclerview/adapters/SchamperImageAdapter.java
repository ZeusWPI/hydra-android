package be.ugent.zeus.hydra.recyclerview.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.schamper.ArticleImage;
import be.ugent.zeus.hydra.recyclerview.viewholder.ImageViewHolder;
import be.ugent.zeus.hydra.utils.ViewUtils;

import java.util.ArrayList;

/**
 * Adapter for the vertically scrollable recycler with images in the Schamper articles.
 *
 * @author Niko Strijbol
 */
public class SchamperImageAdapter extends ItemAdapter<ArticleImage, ImageViewHolder> {

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        Resources resources = parent.getResources();
        int size = (int) resources.getDimension(R.dimen.schamper_image_size);

        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(size, size));

        //Image view for the actual image.
        ImageView v = new ImageView(context);
        v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        v.setScaleType(ImageView.ScaleType.CENTER_CROP);

        //For the zoom image, we simulate a fab. It's not really one, but nobody knows that :D
        //Size of a mini fab
        int miniFabSize = (int) resources.getDimension(R.dimen.mini_fab_size);
        int miniFabMargin = (int) resources.getDimension(R.dimen.mini_fab_margin);

        //The zoom overlay icon
        ImageView overlay = new ImageView(context);
        FrameLayout.LayoutParams overP = new FrameLayout.LayoutParams(miniFabSize, miniFabSize);
        overP.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        overP.rightMargin = miniFabMargin;
        overP.bottomMargin = miniFabMargin;
        overlay.setLayoutParams(overP);
        overlay.setScaleType(ImageView.ScaleType.CENTER);

        //Set the drawable
        overlay.setImageDrawable(ViewUtils.getTintedVectorDrawable(context, R.drawable.ic_zoom_in_24dp, R.color.white));

        //Set tint
        View tintView = new View(context);
        FrameLayout.LayoutParams tintP = new FrameLayout.LayoutParams(miniFabSize, miniFabSize);
        tintP.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        tintP.rightMargin = miniFabMargin;
        tintP.bottomMargin = miniFabMargin;
        tintView.setLayoutParams(tintP);
        tintView.setBackgroundResource(R.drawable.gradient_full_circle);

        //First add the image, then the 'fab' background, then the icon
        frameLayout.addView(v);
        frameLayout.addView(tintView);
        frameLayout.addView(overlay);

        return new ImageViewHolder(frameLayout, v, this);
    }

    /**
     * @return The images.
     */
    @NonNull
    public ArrayList<ArticleImage> getItems() {
        if(items.isEmpty()) {
            //If the list is empty, it is an empty list, so we give a new empty one.
            //Caching this is not necessary, as this should not occur (that frequently).
            return new ArrayList<>();
        } else {
            //If the list is not empty, implementation details guarantee that it's an ArrayList.
            return (ArrayList<ArticleImage>) items;
        }
    }
}