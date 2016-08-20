package be.ugent.zeus.hydra.activities.minerva;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.ToolbarActivity;
import be.ugent.zeus.hydra.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.utils.html.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author Niko Strijbol
 */
public class AgendaActivity extends ToolbarActivity {

    public static final String ARG_AGENDA_ITEM = "argAgendaItem";

    private AgendaItem agendaItem;
    private static final DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("nl"));

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minerva_agenda);

        Intent intent = getIntent();
        agendaItem = intent.getParcelableExtra(ARG_AGENDA_ITEM);

        TextView title = $(R.id.title);
        title.setText(agendaItem.getTitle());
        getToolBar().setTitle(agendaItem.getTitle());

        //Description
        if(!TextUtils.isEmpty(agendaItem.getContent())) {
            TextView description = $(R.id.agenda_description);
            description.setText(Utils.fromHtml(agendaItem.getContent()));
        }

        if(TextUtils.isEmpty(agendaItem.getLocation())) {
            $(R.id.agenda_location_row).setVisibility(View.GONE);
            $(R.id.divider_below_location).setVisibility(View.GONE);
        } else {
            //TODO: onclick?
            TextView location = $(R.id.agenda_location);
            location.setText(agendaItem.getLocation());
        }

        String start = format.format(agendaItem.getStartDate());
        String end = format.format(agendaItem.getEndDate());
        TextView startTime = $(R.id.agenda_time_start);
        //TODO: deta
        startTime.setText(start);
        TextView endTime = $(R.id.agenda_time_end);
        endTime.setText(end);

        TextView course = $(R.id.agenda_course);
        if(TextUtils.isEmpty(agendaItem.getCourse().getTitle())) {
            course.setText(agendaItem.getCourse().getCode());
        } else {
            course.setText(agendaItem.getCourse().getTitle());
        }

        TextView edit =$(R.id.agenda_organiser);
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
        super.onCreateOptionsMenu(menu);

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_minerva_agenda, menu);

        // We need to manually set the color of this Drawable for some reason.
        tintToolbarIcons(menu, R.id.minerva_agenda_add);

        return true;
    }

    private void addToCalendar() {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, agendaItem.getStartDate().getTime())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, agendaItem.getEndDate().getTime())
                .putExtra(CalendarContract.Events.TITLE, agendaItem.getTitle())
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);

        if(!TextUtils.isEmpty(agendaItem.getContent())) {
            intent.putExtra(CalendarContract.Events.DESCRIPTION, agendaItem.getContent());
        }

        if(!TextUtils.isEmpty(agendaItem.getLocation())) {
            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, agendaItem.getLocation());
        }
        startActivity(intent);
    }
}
