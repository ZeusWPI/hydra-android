package be.ugent.zeus.hydra.ui.minerva;

import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.data.database.minerva.AgendaDao;
import be.ugent.zeus.hydra.ui.plugins.ProgressBarPlugin;
import be.ugent.zeus.hydra.ui.plugins.common.Plugin;
import be.ugent.zeus.hydra.ui.plugins.loader.LoaderCallback;
import be.ugent.zeus.hydra.ui.plugins.loader.LoaderPlugin;
import be.ugent.zeus.hydra.ui.common.BaseActivity;
import be.ugent.zeus.hydra.ui.common.loaders.LoaderResult;
import be.ugent.zeus.hydra.ui.main.MainActivity;
import be.ugent.zeus.hydra.ui.minerva.overview.CourseActivity;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.utils.html.Utils;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Displays an Agenda item from Minerva.
 *
 * To manually start the activity, use the {@link #start(Context, AgendaItem)} method.
 *
 * @author Niko Strijbol
 */
public class AgendaActivity extends BaseActivity implements LoaderCallback<AgendaItem> {

    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private LoaderPlugin<AgendaItem> plugin = new LoaderPlugin<>(this);
    private ProgressBarPlugin barPlugin = new ProgressBarPlugin();

    private int agendaItemId;
    private AgendaItem item;

    private View errorView;
    private View normalView;

    /**
     * Start this activity.
     *
     * @param context The context to start it with.
     * @param agenda The item to show.
     */
    public static void start(Context context, AgendaItem agenda) {
        Intent intent = new Intent(context, AgendaActivity.class);
        intent.putExtra(CalendarContract.Events.CUSTOM_APP_URI, agenda.getUri());
        context.startActivity(intent);
    }

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
        plugin.setSuccessCallback(this::onResult);
        plugin.addErrorCallback(this::onError);
        plugin.addResetCallback(this::onReset);
        barPlugin.register(plugin);
        plugins.add(plugin);
        plugins.add(barPlugin);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minerva_agenda);

        Intent intent = getIntent();
        Uri uri = Uri.parse(intent.getStringExtra(CalendarContract.EXTRA_CUSTOM_APP_URI));
        agendaItemId = Integer.valueOf(uri.getLastPathSegment());

        errorView = $(R.id.error_view);
        normalView = $(R.id.normal_view);

        plugin.startLoader();
    }

    private void onResult(AgendaItem result) {
        setResult(RESULT_OK);
        errorView.setVisibility(GONE);
        normalView.setVisibility(VISIBLE);
        invalidateOptionsMenu();
        item = result;

        TextView title = $(R.id.title);
        title.setText(item.getTitle());
        getToolbar().setTitle(item.getTitle());

        //Description
        if (!TextUtils.isEmpty(item.getContent())) {
            TextView description = $(R.id.agenda_description);
            description.setText(Utils.fromHtml(item.getContent()));
        }

        if (TextUtils.isEmpty(item.getLocation())) {
            $(R.id.agenda_location_row).setVisibility(GONE);
            $(R.id.divider_below_location).setVisibility(GONE);
        } else {
            //TODO: onclick?
            TextView location = $(R.id.agenda_location);
            location.setText(item.getLocation());
        }

        TextView dayTime = $(R.id.agenda_time_day);
        TextView hourTime = $(R.id.agenda_time_hour);

        LocalDateTime localStart = DateUtils.toLocalDateTime(item.getStartDate());
        LocalDateTime localEnd = DateUtils.toLocalDateTime(item.getEndDate());

        // If they are on the same day, don't display the day on the end date.
        if (localStart.toLocalDate().equals(localEnd.toLocalDate())) {
            if (DateUtils.isFriendly(localStart.toLocalDate())) {
                dayTime.setText(getString(
                        R.string.minerva_calendar_friendly_date,
                        DateUtils.getFriendlyDate(localStart.toLocalDate()),
                        localStart.format(DAY_FORMATTER)
                ));
            } else {
                dayTime.setText(localStart.format(DAY_FORMATTER));
            }
        } else {
            dayTime.setText(getString(
                    R.string.minerva_calendar_duration,
                    localStart.format(DAY_FORMATTER),
                    localEnd.format(DAY_FORMATTER)
            ));
        }

        hourTime.setText(getString(
                R.string.minerva_calendar_duration,
                localStart.format(HOUR_FORMATTER),
                localEnd.format(HOUR_FORMATTER)
        ));

        TextView course = $(R.id.agenda_course);
        if (TextUtils.isEmpty(item.getCourse().getTitle())) {
            course.setText(item.getCourse().getCode());
        } else {
            course.setText(item.getCourse().getTitle());
        }

        TextView edit = $(R.id.agenda_organiser);
        edit.setText(item.getLastEditUser());
    }

    private void onError(Throwable error) {
        invalidateOptionsMenu();
        errorView.setVisibility(VISIBLE);
        normalView.setVisibility(GONE);
    }

    private void onReset() {
        this.item = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Provide up navigation if opened from outside Hydra-
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent) && this.item != null) {
                    // Intent for the course activity
                    Intent courseIntent = new Intent(this, CourseActivity.class);
                    courseIntent.putExtra(CourseActivity.ARG_TAB, CourseActivity.Tab.AGENDA);
                    courseIntent.putExtra(CourseActivity.ARG_COURSE, (Parcelable) this.item.getCourse());
                    // Intent for the main activity
                    Intent mainIntent = new Intent(this, MainActivity.class);
                    mainIntent.putExtra(MainActivity.ARG_TAB, R.id.drawer_minerva);
                    TaskStackBuilder.create(this)
                            .addNextIntent(mainIntent)
                            .addNextIntent(courseIntent)
                            .startActivities();
                } else {
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<LoaderResult<AgendaItem>> getLoader(Bundle args) {
        return new AgendaItemLoader(this, new AgendaDao(this), agendaItemId);
    }
}