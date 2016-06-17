package be.ugent.zeus.hydra.recyclerviewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.schamper.Article;

/**
 * Created by feliciaan on 17/06/16.
 */
public class SchamperViewHolder extends RecyclerView.ViewHolder {
    private TextView title;

    public SchamperViewHolder(View itemView) {
        super(itemView);

        title = (TextView) itemView.findViewById(R.id.title);
    }

    public void populate(Article article) {
        title.setText(article.getTitle());
    }
}
