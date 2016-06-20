package be.ugent.zeus.hydra.recyclerviewholder.home;

import android.text.Html;
import android.view.View;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.association.NewsItem;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.models.cards.NewsItemCard;
import be.ugent.zeus.hydra.utils.DateUtils;

import java.util.Locale;

/**
 * Created by feliciaan on 18/06/16.
 */
public class NewsItemViewHolder extends AbstractViewHolder{
    private TextView info;
    private TextView title;
    private TextView summary;

    public NewsItemViewHolder(View v) {
        super(v);
        title = (TextView) v.findViewById(R.id.name);
        summary = (TextView) v.findViewById(R.id.summary);
        info = (TextView) v.findViewById(R.id.info);
    }

    public void populate(HomeCard card) {
        if (card.getCardType() != HomeCard.CardType.NEWS_ITEM) {
            return; // TODO: generate error
        }

        NewsItemCard newsItemCard = (NewsItemCard) card;
        final NewsItem newsItem = newsItemCard.getNewsItem();

        title.setText(newsItem.getTitle());

        String infoText = String.format(new Locale("nl"), "%s door %s",
                DateUtils.relativeDateString(newsItem.getDate(), itemView.getContext()),
                newsItem.getAssociation().getName());
        info.setText(infoText);
        if(!newsItem.isHighlighted()){
            title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.star, 0);
        }else{
            title.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        summary.setText(""); // do not set text here, because not al newsItems are opened
        summary.setVisibility(View.GONE);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(summary.getVisibility() != View.VISIBLE) {
                    if (summary.getText().length() == 0) {
                        summary.setText(Html.fromHtml(newsItem.getContent()));
                    }
                    summary.setVisibility(View.VISIBLE);
                } else{
                    summary.setVisibility(View.GONE);
                }
            }
        });
    }
}
