package be.ugent.zeus.hydra.recyclerview.viewholder;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import be.ugent.zeus.hydra.activities.ImageGalleryActivity;
import be.ugent.zeus.hydra.models.schamper.ArticleImage;
import be.ugent.zeus.hydra.recyclerview.adapters.SchamperImageAdapter;
import com.squareup.picasso.Picasso;

/**
 * View holder for a Drawable.
 *
 * @author Niko Strijbol
 */
public class ImageViewHolder extends RecyclerView.ViewHolder implements DataViewHolder<ArticleImage> {

    private ImageView view;
    private SchamperImageAdapter adapter;

    /**
     * @param itemView The view to display in the recycler view.
     * @param imageView The view in which the image will be displayed. It is a child of itemView.
     * @param adapter The adapter.
     */
    public ImageViewHolder(FrameLayout itemView, ImageView imageView, SchamperImageAdapter adapter) {
        super(itemView);
        view = imageView;
        this.adapter = adapter;
    }

    /**
     * Populate with the data.
     *
     * @param data The data.
     */
    @Override
    public void populateData(ArticleImage data) {
        Picasso.with(view.getContext()).load(data.getLargeUrl()).into(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ImageGalleryActivity.class);
                intent.putParcelableArrayListExtra(ImageGalleryActivity.PARCEL_IMAGES, adapter.getItems());
                intent.putExtra(ImageGalleryActivity.PARCEL_POSITION, getAdapterPosition());
                view.getContext().startActivity(intent);
            }
        });
    }
}