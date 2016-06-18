package be.ugent.zeus.hydra.recyclerviewholder;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.schamper.Article;

/**
 * Created by feliciaan on 17/06/16.
 */
public class SchamperViewHolder extends RecyclerView.ViewHolder {
    private TextView title;
    private TextView date;
    private TextView author;
    private ImageView image;

    public SchamperViewHolder(View itemView) {
        super(itemView);

        title = (TextView) itemView.findViewById(R.id.title);
        date = (TextView) itemView.findViewById(R.id.date);
        author = (TextView) itemView.findViewById(R.id.author);
        image = (ImageView) itemView.findViewById(R.id.image);
    }

    public void populate(final Article article) {
        title.setText(article.getTitle());
        date.setText(article.getPubDate().toLocaleString());
        author.setText(article.getAuthor());

        Picasso.with(this.itemView.getContext()).load(article.getImage()).into(image);

        this.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.getLink()));
                itemView.getContext().startActivity(browserIntent);
            }
        });
    }
}
