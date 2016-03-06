package be.ugent.zeus.hydra.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

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
        private final List<String> menuLines;

        public RestoCategory(Date date, String title, List<String> menuLines) {
            this.date = date;
            this.title = title;
            this.menuLines = menuLines;
        }

        public String getTitle() {
            return title;
        }

        public Date getDate() {
            return date;
        }

        public String getMenuLinesText() {
            // Oh Java, why did I used to love you?
            // Join the lines with newlines.
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (String line : menuLines) {
                if (first) first = false;
                else sb.append("\n");
                sb.append(line);
            }
            return sb.toString();
        }
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView menuLines;

        public CardViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.category_text);
            menuLines = (TextView) v.findViewById(R.id.menu_lines);
        }

        public void populate(RestoCategory card) {
            title.setText(card.getTitle());
            menuLines.setText(card.getMenuLinesText());
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
            Date today = new Date();
            int thisWeek = Integer.parseInt(weekFormatter.format(today));
            int week = Integer.parseInt(weekFormatter.format(date));

            if (date.equals(today)) {
                return "Vandaag";
            } else if(date.getTime() - today.getTime() <= 25 * 60 * 60 * 1000) {
                return "Morgen";
            } else if(date.getTime() - today.getTime() <= 2.1 * 24 * 60 * 60 * 1000) {
                return "Overmorgen";
            } else if (week == thisWeek) {
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
            List<String> mealStrings = new ArrayList<>();
            for (RestoMeal meal: menu.getMeals()) {
                mealStrings.add(meal.getName() + " " + meal.getPrice());
            }
            this.menuList.add(new RestoCategory(menu.getDate(), "Hoofdgerechten", mealStrings));

            // Vegetables
            this.menuList.add(new RestoCategory(menu.getDate(), "Groenten", menu.getVegetables()));
        }
    }
}