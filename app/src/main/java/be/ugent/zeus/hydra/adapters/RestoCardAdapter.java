package be.ugent.zeus.hydra.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
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
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.Resto.RestoMeal;
import be.ugent.zeus.hydra.models.Resto.RestoMenu;
import be.ugent.zeus.hydra.models.Resto.RestoMenuList;

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

        public String getVegetablesText() {
            // Oh Java, why did I used to love you?
            // Join the lines with newlines.
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (String line : vegetables) {
                if (first) first = false;
                else sb.append("\n");
                sb.append(line);
            }
            return sb.toString();
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
            tl.removeAllViews();

            title.setText(card.getTitle());
            if (card.isMeals()) for (RestoMeal meal : card.getMeals()) {

                TableRow tr = new TableRow(view.getContext());
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                tr.setLayoutParams(lp);

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

                TextView tvCenter = new TextView(view.getContext());
                tvCenter.setLayoutParams(lp);
                tvCenter.setText(meal.getName());
                tvCenter.setTextColor(Color.parseColor("#122b44"));
                TextView tvRight = new TextView(view.getContext());
                tvRight.setLayoutParams(lp);
                tvRight.setText(meal.getPrice());
                tvRight.setTextColor(Color.parseColor("#122b44"));

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

                    TextView tvCenter = new TextView(view.getContext());
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
        private SimpleDateFormat weekFormatter = new SimpleDateFormat("w", new Locale("nl"));
        private SimpleDateFormat dayFormatter = new SimpleDateFormat("cccc", new Locale("nl"));
        private DateFormat dateFormatter = SimpleDateFormat.getDateInstance();

        public HeaderViewHolder(View v) {
            super(v);
            headerText = (TextView) v.findViewById(R.id.resto_header_text);
        }

        public void populate(Date date) {
            headerText.setText(getFriendlyDate(date));
        }

        private String getFriendlyDate(Date date) {
            // TODO: I feel a bit bad about all of this; any good libraries?
            // I couldn't find any that were suitable -- mivdnber
            DateTime today = new DateTime();
            DateTime dateTime = new DateTime(date);
            int thisWeek = Integer.parseInt(weekFormatter.format(today.toDate()));
            int week = Integer.parseInt(weekFormatter.format(date));

            int daysBetween = Days.daysBetween(today.toLocalDate(), dateTime.toLocalDate()).getDays();

            if (daysBetween == 0) {
                return "Vandaag";
            } else if(daysBetween == 1) {
                return "Morgen";
            } else if(daysBetween == 2) {
                return "Overmorgen";
            } else if (week == thisWeek || daysBetween < 7) {
                return dayFormatter.format(date).toLowerCase();
            } else if (week == thisWeek + 1) {
                return "Volgende " + dayFormatter.format(date).toLowerCase();
            } else {
                return dateFormatter.format(date);
            }
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
        for (RestoMenu menu : menuList) {
            // Main meals
            this.menuList.add(new RestoCategory(menu.getDate(), "Hoofdgerechten", menu.getMainDishes()));
            this.menuList.add(new RestoCategory(menu.getDate(), "Bijgerechten", menu.getSideDishes()));

            // Vegetables
            this.menuList.add(new RestoCategory(menu.getDate(), "Groenten", menu.getVegetables()));
        }
    }
}