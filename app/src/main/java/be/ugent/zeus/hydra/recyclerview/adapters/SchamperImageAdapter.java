package be.ugent.zeus.hydra.recyclerview.adapters;


import android.view.ViewGroup;
import android.widget.ImageView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.SchamperArticleActivity;
import be.ugent.zeus.hydra.recyclerview.viewholder.ImageViewHolder;

import java.util.ArrayList;

/**
 * @author Niko Strijbol
 */
public class SchamperImageAdapter extends ItemAdapter<SchamperArticleActivity.ArticleImage, ImageViewHolder> {

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int size = (int) parent.getResources().getDimension(R.dimen.schamper_image_size);

        ImageView v = new ImageView(parent.getContext());
        v.setLayoutParams(new ViewGroup.LayoutParams(size, size));
        v.setScaleType(ImageView.ScaleType.CENTER_CROP);

        return new ImageViewHolder(v, this);
    }

    public ArrayList<SchamperArticleActivity.ArticleImage> getItems() {
        if(items.isEmpty()) {
            return new ArrayList<>();
        } else {
            return (ArrayList<SchamperArticleActivity.ArticleImage>) items;
        }
    }
}
