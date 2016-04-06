package be.ugent.zeus.hydra.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.info.InfoItem;
import be.ugent.zeus.hydra.models.info.InfoList;
import be.ugent.zeus.hydra.recyclerviewholder.DateHeaderViewHolder;

/**
 * Created by ellen on 8/3/16.
 */
public class CardInfoListAdapter extends RecyclerView.Adapter<CardInfoListAdapter.CardViewHolder> implements StickyRecyclerHeadersAdapter {
    private ArrayList<InfoItem> items;

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        private final View view;
        private TextView title;
        private ImageView imageView;
        private Context context;


        public CardViewHolder(View v,Context context) {
            super(v);
            this.view = v;
            title = (TextView) v.findViewById(R.id.info_name);
            imageView = (ImageView) v.findViewById(R.id.infoImage);
            this.context=context;
        }

        public void populate(final InfoItem infoItem) {
            title.setText(infoItem.getTitle());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //todo
                }
            });

            int resId = context.getResources().getIdentifier(infoItem.getImage(), "drawable", context.getPackageName());
            imageView.setImageResource(resId);
        }
    }



    public CardInfoListAdapter() {
        this.items = new ArrayList<>();
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.info_card, parent, false);
        CardViewHolder vh = new CardViewHolder(v,parent.getContext());
        return vh;
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        final InfoItem category = items.get(position);
        holder.populate(category);
    }

    @Override
    public long getHeaderId(int position) {
        return 0; //no header
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        //no header
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_listitem_header, parent, false);
        return new DateHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        //no header
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(InfoList items) {
        this.items.clear();
        for (InfoItem item : items) {
            this.items.add(item);
        }
    }
}