package be.ugent.zeus.hydra.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.association.AssociationNews;
import be.ugent.zeus.hydra.models.association.AssociationNewsItem;
import be.ugent.zeus.hydra.recyclerviewholder.DateHeaderViewHolder;

/**
 * Created by ellen on 8/3/16.
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.CardViewHolder> implements StickyRecyclerHeadersAdapter {
    private ArrayList<AssociationNewsItem> items;

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        private final View view;
        private TextView info;
        private TextView title;
        private TextView summary;
        private ImageView star;
        private final SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE dd MMM yyyy", Locale.getDefault());
        private boolean big;
        private LinearLayout head;

        public CardViewHolder(View v) {
            super(v);
            this.view = v;
            title = (TextView) v.findViewById(R.id.name);
            summary = (TextView) v.findViewById(R.id.summary);
            info = (TextView) v.findViewById(R.id.info);
            star = (ImageView) v.findViewById(R.id.star);
            head = (LinearLayout) v.findViewById(R.id.head);
        }


        public void populate(final AssociationNewsItem newsItem) {
            title.setText(newsItem.title);

            info.setText(dateFormatter.format(newsItem.date) + " by "+ "TODO");//// TODO: 07/04/2016 after merge do getName
            if(!newsItem.highlighted){
                star.setVisibility(View.INVISIBLE);
            }else{
                star.setVisibility(View.VISIBLE);
            }

            big = false;
            summary.setText("");
            head.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   //// TODO: 07/04/2016
                    if(big) {
                        summary.setText("");
                    }else{
                        summary.setText(Html.fromHtml(newsItem.content));
                    }
                    big=!big;
                }
            });



        }
    }



    public NewsAdapter() {
        this.items = new ArrayList<>();
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_item, parent, false);
        CardViewHolder vh = new CardViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        final AssociationNewsItem restoCategory = items.get(position);
        holder.populate(restoCategory);
    }

    @Override
    public long getHeaderId(int position) {
        //no header
        return 0;
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

    public void setItems(AssociationNews items) {
        this.items.clear();
        for (AssociationNewsItem item : items) {
            this.items.add(item);

        }


    }
}