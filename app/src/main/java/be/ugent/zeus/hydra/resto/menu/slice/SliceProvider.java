package be.ugent.zeus.hydra.resto.menu.slice;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.IconCompat;
import android.util.Log;

import androidx.slice.Slice;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;

import be.ugent.zeus.hydra.MainActivity;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.common.ui.widgets.DisplayableMenu;
import be.ugent.zeus.hydra.resto.*;
import be.ugent.zeus.hydra.resto.history.DayRequest;
import be.ugent.zeus.hydra.utils.DateUtils;
import org.threeten.bp.LocalDate;

import static be.ugent.zeus.hydra.resto.Broadcast.REQUEST_CODE;

/**
 * @author Niko Strijbol
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class SliceProvider extends androidx.slice.SliceProvider {

    private static final String BASE_URI = "content://be.ugent.zeus.hydra.resto.menu";
    private static final String PATH = "/single";
    static final String FULL_URI = BASE_URI + PATH;

    private Result<RestoMenu> lastData;
    private boolean fromUpdate = false;

    private SharedPreferences preferences;

    private static final String TAG = "SliceProvider";

    @Override
    public boolean onCreateSliceProvider() {
        if (getContext() == null) {
            return false;
        }
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        return true;
    }

    @Override

    public Slice onBindSlice(Uri sliceUri) {

        if (getContext() == null) {
            return null;
        }

        if (!PATH.equals(sliceUri.getPath())) {
            return null;
        }

        final LocalDate currentDate = DayManager.getDate();
        final RestoChoice restoChoice = getRestoChoice();

        if (!fromUpdate) {
            Log.i(TAG, "onBindSlice: fromUpdate = false, scheduling update");
            scheduleRequest(currentDate, restoChoice);
        } else {
            // TODO: euh?
            //DayManager.setDate(null);
            fromUpdate = false;
        }

        Result<RestoMenu> data = this.lastData;
        this.lastData = null;

        Log.i(TAG, "onBindSlice: data is null ? -> " + (data == null));

        return this.constructSlice(sliceUri, currentDate, restoChoice, data);
    }

    @SuppressLint("StaticFieldLeak")
    private void scheduleRequest(LocalDate currentDate, RestoChoice restoChoice) {
        new AsyncTask<Void, Void, Result<RestoMenu>>() {

            @Override
            protected Result<RestoMenu> doInBackground(Void... voids) {
                DayRequest request = new DayRequest(getContext());
                request.setDate(currentDate);
                request.setChoice(restoChoice);
                return request.execute();
            }

            @Override
            protected void onPostExecute(Result<RestoMenu> restoMenuResult) {
                if (getContext() != null) {
                    lastData = restoMenuResult;
                    fromUpdate = true;
                    Log.i(TAG, "onPostExecute: data is loaded, doing broadcast update");
                    Intent intent = Broadcast.createUpdateIntent(getContext());
                    getContext().sendBroadcast(intent);
                }
            }
        }.execute();
    }

    private SliceAction getOpenRestoAction() {
        assert getContext() != null;
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.putExtra(MainActivity.ARG_TAB, R.id.drawer_resto);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);
        return SliceAction.create(
                pendingIntent,
                IconCompat.createWithResource(getContext(), R.drawable.tabs_resto),
                ListBuilder.ICON_IMAGE,
                getContext().getString(R.string.resto_menu_slice_open_menu)
        );
    }

    /**
     * Create a slice.
     *
     * @param sliceUri The uri of the slice.
     * @param result   The result or null if there is no result yet.
     *
     * @return The slice.
     */
    private Slice constructSlice(Uri sliceUri, LocalDate currentDate, RestoChoice restoChoice, @Nullable Result<RestoMenu> result) {
        assert getContext() != null;

        // Construct the basic actions.
        LocalDate previousDay = currentDate.minusDays(1);
        LocalDate nextDay = currentDate.plusDays(1);
        Log.d(TAG, "constructSlice: day is " + currentDate.toString());
        SliceAction dayNext = SliceAction.create(
                getChangeRestoDayIntent(nextDay),
                IconCompat.createWithResource(getContext(), R.drawable.ic_navigate_next),
                ListBuilder.LARGE_IMAGE,
                getContext().getString(R.string.resto_menu_slice_next));

        SliceAction dayPrevious = SliceAction.create(
                getChangeRestoDayIntent(previousDay),
                IconCompat.createWithResource(getContext(), R.drawable.ic_navigate_before),
                ListBuilder.LARGE_IMAGE,
                getContext().getString(R.string.resto_menu_slice_previous));

        ListBuilder.HeaderBuilder header = new ListBuilder.HeaderBuilder()
                .setPrimaryAction(getOpenRestoAction());

        String date = DateUtils.getFriendlyDate(getContext(), currentDate);
        if (result == null) {
            header.setSubtitle(getContext().getString(R.string.resto_menu_slice_subtitle_loading, date), true);
        } else {
            header.setSubtitle(getContext().getString(R.string.resto_menu_slice_subtitle, date));
        }


        if (result != null && result.hasException()) {
            header.setTitle(getContext().getString(R.string.resto_menu_slice_title_error));
        } else {
            header.setTitle(getContext().getString(R.string.resto_menu_slice_title, restoChoice.getName()));
        }

        ListBuilder body = new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY)
                .setHeader(header)
                .addAction(dayPrevious)
                .addAction(dayNext)
                .setAccentColor(ContextCompat.getColor(getContext(), R.color.hydra_primary_color));

        if (result != null && result.hasData()) {
            RestoMenu menu = result.getData();
            for (RestoMeal meal : menu.getMainDishes()) {
                ListBuilder.RowBuilder row = new ListBuilder.RowBuilder();

                //Set the correct image.
                @DrawableRes int id = DisplayableMenu.getDrawable(meal);
                row.setTitleItem(IconCompat.createWithResource(getContext(), id), ListBuilder.RowBuilder.TYPE_ICON);

                row.setTitle(meal.getName());
                row.setSubtitle(meal.getPrice());

                body.addRow(row);
            }
        }

        return body.build();
    }

    private PendingIntent getChangeRestoDayIntent(LocalDate newDate) {
        assert getContext() != null;
        Intent intent = new Intent(Broadcast.ACTION_RESTO_UPDATE);
        intent.setClass(getContext(), UpdatedMenuBroadcastReceiver.class);
        intent.putExtra(UpdatedMenuBroadcastReceiver.EXTRA_DATE_UPDATE, newDate.toString());
        return PendingIntent.getBroadcast(getContext(), REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private RestoChoice getRestoChoice() {
        assert getContext() != null;
        String key = RestoPreferenceFragment.getRestoEndpoint(getContext(), preferences);
        String defaultName = getContext().getString(R.string.resto_default_name);
        String name = preferences.getString(RestoPreferenceFragment.PREF_RESTO_NAME, defaultName);
        return new RestoChoice(name, key);
    }
}
