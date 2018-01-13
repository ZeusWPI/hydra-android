package be.ugent.zeus.hydra.ui.main.schamper;

import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.schamper.Article;
import be.ugent.zeus.hydra.ui.common.ViewUtils;
import be.ugent.zeus.hydra.ui.common.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.ui.common.recyclerview.adapters.ItemDiffAdapter;

/**
 * @author Niko Strijbol
 * @author feliciaan
 */
class SchamperListAdapter extends ItemDiffAdapter<Article, SchamperViewHolder> {

    private final ActivityHelper helper;

    SchamperListAdapter(ActivityHelper helper) {
        super();
        this.helper = helper;
    }

    @Override
    public SchamperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SchamperViewHolder(ViewUtils.inflate(parent, R.layout.item_schamper), helper);
    }
}