package be.ugent.zeus.hydra.activities.minerva.agenda;

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
import be.ugent.zeus.hydra.activities.MainActivity;
import be.ugent.zeus.hydra.activities.common.HydraActivity;
import be.ugent.zeus.hydra.activities.minerva.CourseActivity;
import be.ugent.zeus.hydra.loaders.LoaderPlugin;
import be.ugent.zeus.hydra.loaders.LoaderProvider;
import be.ugent.zeus.hydra.loaders.LoaderResult;
import be.ugent.zeus.hydra.minerva.agenda.AgendaDao;
import be.ugent.zeus.hydra.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.plugins.ProgressBarPlugin;
import be.ugent.zeus.hydra.plugins.common.Plugin;
import be.ugent.zeus.hydra.utils.html.Utils;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * @author Niko Strijbol
 */
public class AgendaActivity extends HydraActivity implements LoaderProvider<AgendaItem> {

    private static final String ARG_AGENDA_ITEM = "argAgendaItem";
    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private LoaderPlugin<AgendaItem> plugin = new LoaderPlugin<>(this);
    private ProgressBarPlugin barPlugin = new ProgressBarPlugin();

    private int agendaItemId;
    private AgendaItem item;

    private View errorView;
    private View normalView;

    public static void start(Context context, int agendaId) {
        Intent intent = new Intent(context, AgendaActivity.class);
        intent.putExtra(CalendarContract.Events.CUSTOM_APP_URI, getUri(agendaId));
        context.startActivity(intent);
    }

    public static String getUri(int id) {
        return "hydra://minerva/calendar/" + id;
    }

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
        plugin.setDataCallback(this::onResult);
        plugin.addErrorListener(this::onError);
        plugin.addResetListener(this::onReset);
        plugin.addResultListener(barPlugin.getFinishedCallback());
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

        TextView startTime = $(R.id.agenda_time_start);
        TextView endTime = $(R.id.agenda_time_end);

        startTime.setText(item.getStartDate().format(format));
        endTime.setText(item.getEndDate().format(format));

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

    private void onReset(Loader<LoaderResult<AgendaItem>> loader) {
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
                            .addNextIntent(courseIntent)
                            .addNextIntent(mainIntent)
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
    public Loader<LoaderResult<AgendaItem>> getLoader(Context context) {
        return new AgendaItemLoader(context, new AgendaDao(context), agendaItemId);
    }
}