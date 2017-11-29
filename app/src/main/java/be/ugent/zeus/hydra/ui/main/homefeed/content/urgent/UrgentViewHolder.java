package be.ugent.zeus.hydra.ui.main.homefeed.content.urgent;

import android.content.Intent;
import android.view.View;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.main.MainActivity;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedAdapter;
import be.ugent.zeus.hydra.ui.main.homefeed.content.FeedViewHolder;

/**
 * @author Niko Strijbol
 */
public class UrgentViewHolder extends FeedViewHolder {

    public UrgentViewHolder(View itemView, HomeFeedAdapter adapter) {
        super(itemView, adapter);

        itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), MainActivity.class);
            intent.putExtra(MainActivity.ARG_TAB, R.id.drawer_urgent);
            intent.putExtra(MainActivity.ARG_NEW_DEFAULT, false);
            v.getContext().startActivity(intent);
        });
    }
}
