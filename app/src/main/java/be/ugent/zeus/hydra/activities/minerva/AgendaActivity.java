package be.ugent.zeus.hydra.activities.minerva;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.HydraActivity;
import be.ugent.zeus.hydra.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.utils.html.Utils;
import org.threeten.bp.format.DateTimeFormatter;

/**
 * @author Niko Strijbol
 */
public class AgendaActivity extends HydraActivity {

    private static final String ARG_AGENDA_ITEM = "argAgendaItem";
    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private AgendaItem agendaItem;

    public static void start(Context context, AgendaItem agendaItem) {
        Intent intent = new Intent(context, AgendaActivity.class);
        intent.putExtra(ARG_AGENDA_ITEM, (Parcelable) agendaItem);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minerva_agenda);

        Intent intent = getIntent();
        agendaItem = intent.getParcelableExtra(ARG_AGENDA_ITEM);

        TextView title = $(R.id.title);
        title.setText(agendaItem.getTitle());
        getToolbar().setTitle(agendaItem.getTitle());

        //Description
        if (!TextUtils.isEmpty(agendaItem.getContent())) {
            TextView description = $(R.id.agenda_description);
            description.setText(Utils.fromHtml(agendaItem.getContent()));
        }

        if (TextUtils.isEmpty(agendaItem.getLocation())) {
            $(R.id.agenda_location_row).setVisibility(View.GONE);
            $(R.id.divider_below_location).setVisibility(View.GONE);
        } else {
            //TODO: onclick?
            TextView location = $(R.id.agenda_location);
            location.setText(agendaItem.getLocation());
        }

        TextView startTime = $(R.id.agenda_time_start);
        TextView endTime = $(R.id.agenda_time_end);

        startTime.setText(agendaItem.getStartDate().format(format));
        endTime.setText(agendaItem.getEndDate().format(format));

        TextView course = $(R.id.agenda_course);
        if (TextUtils.isEmpty(agendaItem.getCourse().getTitle())) {
            course.setText(agendaItem.getCourse().getCode());
        } else {
            course.setText(agendaItem.getCourse().getTitle());
        }

        TextView edit = $(R.id.agenda_organiser);
        edit.setText(agendaItem.getLastEditUser());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.minerva_agenda_add:
                addToCalendar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_minerva_agenda, menu);
        tintToolbarIcons(menu, R.id.minerva_agenda_add);
        return super.onCreateOptionsMenu(menu);
    }

    private void addToCalendar() {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, agendaItem.getStartDate().toInstant().toEpochMilli())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, agendaItem.getEndDate().toInstant().toEpochMilli())
                .putExtra(CalendarContract.Events.TITLE, agendaItem.getTitle())
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);

        if (!TextUtils.isEmpty(agendaItem.getContent())) {
            intent.putExtra(CalendarContract.Events.DESCRIPTION, agendaItem.getContent());
        }

        if (!TextUtils.isEmpty(agendaItem.getLocation())) {
            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, agendaItem.getLocation());
        }
        startActivity(intent);
    }
}