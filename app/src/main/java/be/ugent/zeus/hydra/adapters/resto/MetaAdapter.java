package be.ugent.zeus.hydra.adapters.resto;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.resto.RestoLocation;

import java.util.Collections;
import java.util.List;

/**
 * Adapter that shows the resto locations. When clicked, Google Maps will be opened, and the location will be
 * displayed.
 *
 * @author Niko Strijbol
 */
public class MetaAdapter extends RecyclerView.Adapter<MetaAdapter.MetaHolder> {

    public static class MetaHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView restoName;
        private TextView restoAddress;
        private TextView restoType;
        private RestoLocation location;

        public MetaHolder(View itemView) {
            super(itemView);

            restoName = (TextView) itemView.findViewById(R.id.resto_name);
            restoAddress = (TextView) itemView.findViewById(R.id.resto_address);
            restoType = (TextView) itemView.findViewById(R.id.resto_type);

            itemView.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            Uri uriLocation = Uri.parse("geo:0,0?q=" + location.latitude + "," + location.longitude + "(" + location.name + ")");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, uriLocation);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(v.getContext().getPackageManager()) != null) {
                v.getContext().startActivity(mapIntent);
            }
        }
    }

    private List<RestoLocation> data = Collections.emptyList();

    /**
     * @param data Replace the current data.
     */
    public void replaceData(List<RestoLocation> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public MetaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        //Inflate the layout
        return new MetaHolder(inflater.inflate(R.layout.item_location, parent, false));
    }

    @Override
    public void onBindViewHolder(final MetaHolder holder, final int position) {
        //Get sandwich from data.
        RestoLocation restoLocation = data.get(position);

        //Set the data.
        holder.restoName.setText(restoLocation.name);
        holder.restoAddress.setText(restoLocation.address);
        holder.restoType.setText(restoLocation.type);
        holder.location = restoLocation;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}