package be.ugent.zeus.hydra.ui.main;

import android.view.ViewGroup;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.schamper.Article;
import be.ugent.zeus.hydra.ui.common.recyclerview.ItemAdapter;
import be.ugent.zeus.hydra.ui.common.ViewUtils;
import be.ugent.zeus.hydra.ui.common.customtabs.ActivityHelper;

/**
 * @author Niko Strijbol
 * @author feliciaan
 */
class SchamperListAdapter extends ItemAdapter<Article, SchamperViewHolder> {

    private final ActivityHelper helper;

    SchamperListAdapter(ActivityHelper helper) {
        this.helper = helper;
    }

    @Override
    public SchamperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SchamperViewHolder(ViewUtils.inflate(parent, R.layout.item_schamper), helper);
    }
}