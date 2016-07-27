package be.ugent.zeus.hydra.recyclerview.viewholder.home;

import android.app.Activity;
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
public class SchamperViewHolder extends HideableViewHolder {

    private TextView title;
    private TextView date;
    private TextView author;
    private ImageView image;

    public SchamperViewHolder(View v, HomeCardAdapter adapter) {
        super(v, adapter);

        title = $(v, R.id.title);
        date = $(v, R.id.date);
        author = $(v, R.id.author);
        image = $(v, R.id.image);
    }

    @Override
    public void populate(HomeCard card) {

        final Article article = card.<SchamperCard>checkCard(HomeCard.CardType.SCHAMPER).getArticle();

        title.setText(article.getTitle());
        date.setText(DateUtils.relativeDateString(article.getPubDate(), itemView.getContext()));
        author.setText(article.getAuthor());

        Picasso.with(this.itemView.getContext()).load(article.getImage()).into(image);

        this.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SchamperArticleActivity.launchWithAnimation((Activity) itemView.getContext(), image, "hero", article);
            }
        });

        super.populate(card);
    }
}