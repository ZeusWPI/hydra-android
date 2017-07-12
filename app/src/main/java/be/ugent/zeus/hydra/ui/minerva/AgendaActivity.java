package be.ugent.zeus.hydra.ui.minerva;

import android.app.TaskStackBuilder;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.repository.observers.ErrorObserver;
import be.ugent.zeus.hydra.repository.observers.ProgressObserver;
import be.ugent.zeus.hydra.repository.observers.SuccessObserver;
import be.ugent.zeus.hydra.ui.common.BaseActivity;
import be.ugent.zeus.hydra.ui.common.html.Utils;
import be.ugent.zeus.hydra.ui.main.MainActivity;
import be.ugent.zeus.hydra.ui.minerva.overview.CourseActivity;
import be.ugent.zeus.hydra.utils.DateUtils;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Displays an Agenda item from Minerva.
 *
 * To manually start the activity, use the {@link #start(Context, AgendaItem)} method.
 *
 * @author Niko Strijbol
 */
public class AgendaActivity extends BaseActivity {

    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minerva_agenda);

        Intent intent = getIntent();
        Uri uri = Uri.parse(intent.getStringExtra(CalendarContract.EXTRA_CUSTOM_APP_URI));
        int agendaItemId = Integer.valueOf(uri.getLastPathSegment());

        errorView = $(R.id.error_view);
        normalView = $(R.id.normal_view);

        AgendaViewModel model = ViewModelProviders.of(this).get(AgendaViewModel.class);
        model.setId(agendaItemId);
        model.getData().observe(this, ErrorObserver.with(this::onError));
        model.getData().observe(this, SuccessObserver.with(this::onResult));
        model.getData().observe(this, new ProgressObserver<>($(R.id.progress_bar)));
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Provide up navigation if opened from outside Hydra.
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent) || isTaskRoot()) {
                    // Intent for the course activity
                    upIntent.putExtra(CourseActivity.ARG_TAB, CourseActivity.Tab.AGENDA);
                    upIntent.putExtra(CourseActivity.ARG_COURSE, (Parcelable) this.item.getCourse());
                    TaskStackBuilder builder = TaskStackBuilder.create(this)
                            .addNextIntentWithParentStack(upIntent);
                    // The first intent is the home intent, so edit that one.
                    builder.editIntentAt(0)
                            .putExtra(MainActivity.ARG_TAB, R.id.drawer_minerva);
                    builder.startActivities();
                } else {
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}