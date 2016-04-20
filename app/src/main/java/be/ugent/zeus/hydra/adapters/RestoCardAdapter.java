package be.ugent.zeus.hydra.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.resto.RestoMeal;
import be.ugent.zeus.hydra.models.resto.RestoMenu;
import be.ugent.zeus.hydra.models.resto.RestoMenuList;
import be.ugent.zeus.hydra.utils.DateUtils;

/**
 * Created by mivdnber on 3/3/16.
 */
public class RestoCardAdapter extends RecyclerView.Adapter<RestoCardAdapter.CardViewHolder> implements StickyRecyclerHeadersAdapter {
    private ArrayList<RestoCategory> menuList;

    public static class RestoCategory {
        private final Date date;
        private final String title;
        private final List<String> vegetables;
        private final List<RestoMeal> meals;

        public RestoCategory(Date date, String title, List<String> vegetables) {
            this.date = date;
            this.title = title;
            this.vegetables = vegetables;
            this.meals = null;
        }

        public RestoCategory(Date date, String title, ArrayList<RestoMeal> meals) {
            this.date = date;
            this.title = title;
            this.meals = meals;
            this.vegetables = null;
        }

        public String getTitle() {
            return title;
        }

        public Date getDate() {
            return date;
        }

        public List<RestoMeal> getMeals() {
            return meals;
        }

        public List<String> getVegetables() {
            return vegetables;
        }

        public boolean isMeals() {
            return meals != null;
        }
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private View view;

        public CardViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.category_text);
            view = v;
        }

        public void populate(RestoCategory card) {
            TableLayout tl = (TableLayout) view.findViewById(R.id.cardTableLayout);
            tl.setColumnStretchable(2, true);
            tl.removeAllViews();

            title.setText(card.getTitle());
            if (card.isMeals()) for (RestoMeal meal : card.getMeals()) {

                TableRow tr = new TableRow(view.getContext());
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                tr.setPadding(0,4,0,4);
                tr.setLayoutParams(lp);

                //TODO: add more/better padding

                // set correct image according to kind
                ImageView imageView = new ImageView(view.getContext());
                switch (meal.getKind()) {
                    case "soup":
                        imageView.setImageResource(R.drawable.soep);
                        break;
                    case "meat":
                        imageView.setImageResource(R.drawable.vlees);
                        break;
                    case "fish":
                        imageView.setImageResource(R.drawable.vis);
                        break;
                    case "vegetarian":
                        imageView.setImageResource(R.drawable.vegi);
                        break;
                    default:
                        imageView.setImageResource(R.drawable.soep);

                }

                imageView.setPadding(0,5,0,0);

                TextView tvCenter = new TextView(view.getContext());
                tvCenter.setPadding(25,0,0,0);
                tvCenter.setLayoutParams(lp);
                tvCenter.setText(meal.getName());
                tvCenter.setTextColor(Color.parseColor("#122b44"));
                TextView tvRight = new TextView(view.getContext());
                tvRight.setLayoutParams(lp);
                tvRight.setText(meal.getPrice());
                tvRight.setTextColor(Color.parseColor("#122b44"));
                tvRight.setGravity(Gravity.RIGHT);


                tr.addView(imageView);
                tr.addView(tvCenter);
                tr.addView(tvRight);

                tl.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            }
            else {
                for (String veg: card.getVegetables()) {

                    TableRow tr = new TableRow(view.getContext());
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                    tr.setLayoutParams(lp);

                    ImageView imageView = new ImageView(view.getContext());

                    imageView.setImageResource(R.drawable.groenten);

                    imageView.setPadding(0,6,0,0);

                    TextView tvCenter = new TextView(view.getContext());
                    tvCenter.setPadding(25,0,0,0);
                    tvCenter.setLayoutParams(lp);
                    tvCenter.setText(veg);
                    tvCenter.setTextColor(Color.parseColor("#122b44"));

                    tr.addView(imageView);
                    tr.addView(tvCenter);

                    tl.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                }
            }

        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView headerText;

        public HeaderViewHolder(View v) {
            super(v);
            headerText = (TextView) v.findViewById(R.id.resto_header_text);
        }

        public void populate(Date date) {
            headerText.setText(DateUtils.getFriendlyDate(date));
        }
    }

    public RestoCardAdapter() {
        this.menuList = new ArrayList<RestoCategory>();
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.resto_card, parent, false);
        CardViewHolder vh = new CardViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        final RestoCategory restoCategory = menuList.get(position);
        holder.populate(restoCategory);
    }

    @Override
    public long getHeaderId(int position) {
        return menuList.get(position).getDate().getTime();
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.resto_day_header, parent, false);
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((HeaderViewHolder) holder).populate(menuList.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    public void setMenuList(RestoMenuList menuList) {
        this.menuList.clear();

        Date currentDate = org.apache.commons.lang3.time.DateUtils.truncate(new Date(), Calendar.DATE); // Date at start of day
        for (RestoMenu menu : menuList) {
            // check if menu is today or later
            if (menu.getDate().before(currentDate)) {
                continue;
            }

            // see if resto is open (in case of holiday)
            if(! menu.isOpen()) {
                this.menuList.add(new RestoCategory(menu.getDate(), "Gesloten", menu.getMainDishes()));
                continue;
            }

            // Main meals
            this.menuList.add(new RestoCategory(menu.getDate(), "Hoofdgerechten", menu.getMainDishes()));
            this.menuList.add(new RestoCategory(menu.getDate(), "Bijgerechten", menu.getSideDishes()));

            // Vegetables
            this.menuList.add(new RestoCategory(menu.getDate(), "Groenten", menu.getVegetables()));
        }
    }
}