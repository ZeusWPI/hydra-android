package be.ugent.zeus.hydra.ui.minerva.overview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.support.v7.widget.DividerItemDecoration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.common.loaders.LoaderResult;
import be.ugent.zeus.hydra.ui.plugins.loader.LoaderCallback;
import be.ugent.zeus.hydra.data.database.minerva.AgendaDao;
import be.ugent.zeus.hydra.data.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.data.models.minerva.Course;
import be.ugent.zeus.hydra.ui.plugins.RecyclerViewPlugin;
import be.ugent.zeus.hydra.ui.plugins.common.Plugin;
import be.ugent.zeus.hydra.ui.plugins.common.PluginFragment;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class CourseAgendaFragment extends PluginFragment implements LoaderCallback<List<AgendaItem>> {

    private static final String ARG_COURSE = "argCourse";

    private Course course;
    private AgendaDao dao;
    private AgendaAdapter adapter = new AgendaAdapter();
    private RecyclerViewPlugin<AgendaItem> plugin = new RecyclerViewPlugin<>(this, adapter);

    public static CourseAgendaFragment newInstance(Course course) {
        CourseAgendaFragment fragment = new CourseAgendaFragment();
        Bundle data = new Bundle();
        data.putParcelable(ARG_COURSE, course);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
        plugin.enableProgress();
        plugins.add(plugin);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        course = getArguments().getParcelable(ARG_COURSE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_minerva_course_announcements, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dao = new AgendaDao(getContext());

        plugin.addItemDecoration(new StickyRecyclerHeadersDecoration(adapter));
        plugin.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    public Loader<LoaderResult<List<AgendaItem>>> getLoader(Bundle args) {
        return new AgendaLoader(getContext(), dao, course);
    }
}