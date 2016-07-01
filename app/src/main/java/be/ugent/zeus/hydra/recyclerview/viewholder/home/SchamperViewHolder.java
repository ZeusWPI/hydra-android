package be.ugent.zeus.hydra.recyclerview.viewholder.home;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.SchamperArticleActivity;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.models.cards.SchamperCard;
import be.ugent.zeus.hydra.models.schamper.Article;
import be.ugent.zeus.hydra.recyclerview.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.utils.DateUtils;
import com.squareup.picasso.Picasso;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Created by feliciaan on 17/06/16.
 */
public class SchamperViewHolder extends AbstractViewHolder {

    private TextView title;
    private TextView date;
    private TextView author;
    private ImageView image;
    private ImageView cardPopup;
    private HomeCardAdapter adapter;

    public SchamperViewHolder(View itemView, HomeCardAdapter adapter) {
        super(itemView);

        title = $(itemView, R.id.title);
        date = $(itemView, R.id.date);
        author = $(itemView, R.id.author);
        image = $(itemView, R.id.image);
        cardPopup       = $(itemView, R.id.card_description_popup);
        this.adapter = adapter;
    }

    @Override
    public void populate(HomeCard card) {
        if (card.getCardType() != HomeCard.CardType.SCHAMPER) {
            return; //TODO: report errors
        }

        final SchamperCard schamperCard = (SchamperCard) card;
        final Article article = schamperCard.getArticle();
        title.setText(article.getTitle());
        date.setText(DateUtils.relativeDateString(article.getPubDate(), itemView.getContext()));
        author.setText(article.getAuthor());

        Picasso.with(this.itemView.getContext()).load(article.getImage()).into(image);

        this.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(itemView.getContext(), SchamperArticleActivity.class);
                intent.putExtra("article", (Parcelable) article);
                itemView.getContext().startActivity(intent);
            }
        });

        cardPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardPopup(view);
            }
        });
    }

    private void cardPopup(View v) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.inflate(R.menu.menu_home_popup);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.menu_hide) {
                    adapter.disableCardType(HomeCard.CardType.SCHAMPER);
                    return true;
                }
                return false;
            }
        });
        popup.show();
    }
}
