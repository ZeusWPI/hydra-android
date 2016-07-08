package be.ugent.zeus.hydra.recyclerview.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.schamper.Article;
import be.ugent.zeus.hydra.recyclerview.viewholder.SchamperViewHolder;

/**
 * Created by feliciaan on 17/06/16.
 */
public class SchamperListAdapter extends ItemAdapter<Article, SchamperViewHolder> {

    @Override
    public SchamperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schamper, parent, false);
        return new SchamperViewHolder(v);
    }
}