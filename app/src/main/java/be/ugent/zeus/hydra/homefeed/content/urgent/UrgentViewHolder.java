package be.ugent.zeus.hydra.homefeed.content.urgent;

import android.content.Intent;
import android.view.View;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.MainActivity;
import be.ugent.zeus.hydra.homefeed.HomeFeedAdapter;
import be.ugent.zeus.hydra.homefeed.content.HideableViewHolder;

/**
 * @author Niko Strijbol
 */
public class UrgentViewHolder extends HideableViewHolder {

    public UrgentViewHolder(View itemView, HomeFeedAdapter adapter) {
        super(itemView, adapter);

        itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), MainActivity.class);
            intent.putExtra(MainActivity.ARG_TAB, R.id.drawer_urgent);
            v.getContext().startActivity(intent);
        });
    }
}
