package be.ugent.zeus.hydra.fragments.minerva;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.Loader;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.minerva.AnnouncementActivity;
import be.ugent.zeus.hydra.fragments.common.LoaderFragment;
import be.ugent.zeus.hydra.loaders.ThrowableEither;
import be.ugent.zeus.hydra.minerva.announcement.AnnouncementDao;
import be.ugent.zeus.hydra.minerva.announcement.AnnouncementDaoLoader;
import be.ugent.zeus.hydra.models.minerva.Announcement;
import be.ugent.zeus.hydra.models.minerva.Course;
import be.ugent.zeus.hydra.recyclerview.adapters.common.EmptyItemLoader;
import be.ugent.zeus.hydra.recyclerview.adapters.minerva.AnnouncementAdapter;
import su.j2e.rvjoiner.JoinableAdapter;
import su.j2e.rvjoiner.JoinableLayout;
import su.j2e.rvjoiner.RvJoiner;

import java.util.ArrayList;
import java.util.List;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
public class CourseAnnouncementFragment extends LoaderFragment<List<Announcement>> {

    private static final String ARG_COURSE = "argCourse";

    private Course course;
    private AnnouncementDao dao;
    private AnnouncementAdapter unreadAdapter;
    private AnnouncementAdapter readAdapter;

    public static CourseAnnouncementFragment newInstance(Course course) {
        CourseAnnouncementFragment fragment = new CourseAnnouncementFragment();
        Bundle data = new Bundle();
        data.putParcelable(ARG_COURSE, course);
        fragment.setArguments(data);
        return fragment;
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

        dao = new AnnouncementDao(getContext());
        readAdapter = new AnnouncementAdapter(R.layout.item_no_data);
        unreadAdapter = new AnnouncementAdapter(R.layout.item_no_data, this);

        RecyclerView recyclerView = $(view, R.id.recycler_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        RvJoiner joiner = new RvJoiner();
        joiner.add(new JoinableLayout(R.layout.fragment_minerva_announcement_unread_header));
        joiner.add(new JoinableAdapter(unreadAdapter, EmptyItemLoader.ITEMS_VIEW, EmptyItemLoader.EMPTY_VIEW));
        joiner.add(new JoinableLayout(R.layout.item_minerva_announcement_read_header));
        joiner.add(new JoinableAdapter(readAdapter, EmptyItemLoader.ITEMS_VIEW, EmptyItemLoader.EMPTY_VIEW));
        recyclerView.setAdapter(joiner.getAdapter());
    }

    /**
     * Receive the data if the request was completed successfully.
     *
     * @param data The data.
     */
    @Override
    public void receiveData(@NonNull List<Announcement> data) {
        List<Announcement> unread = new ArrayList<>();
        List<Announcement> read = new ArrayList<>();

        //Split data
        for (Announcement a: data) {
            if(a.isRead()) {
                read.add(a);
            } else {
                unread.add(a);
            }
        }

        unreadAdapter.setItems(unread);
        readAdapter.setItems(read);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == AnnouncementActivity.RESULT_ANNOUNCEMENT && resultCode == Activity.RESULT_OK) {
            if(data.getBooleanExtra(AnnouncementActivity.RESULT_ARG_ANNOUNCEMENT_READ, false)) {
                int id = data.getIntExtra(AnnouncementActivity.RESULT_ARG_ANNOUNCEMENT_ID, 0);
                //Get the item
                int pos = unreadAdapter.positionOf(id);
                Announcement a = unreadAdapter.get(pos);
                unreadAdapter.remove(pos);
                readAdapter.add(a);
                Snackbar.make(getView(), "Als gelezen gemarkeerd.", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public Loader<ThrowableEither<List<Announcement>>> getLoader() {
        return new AnnouncementDaoLoader(getContext(), dao, course);
    }
}