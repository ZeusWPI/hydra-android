package be.ugent.zeus.hydra.fragments.minerva;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.minerva.Course;
import be.ugent.zeus.hydra.utils.NetworkUtils;
import be.ugent.zeus.hydra.utils.html.Utils;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
public class CourseInfoFragment extends Fragment {

    private static final String ARG_COURSE = "argCourse";

    private static final String URL = "https://studiegids.ugent.be/%d/NL/studiefiches/%s.pdf";
    private static final int DEFAULT_YEAR = 2016;

    private Course course;

    public static CourseInfoFragment newInstance(Course course) {
        CourseInfoFragment fragment = new CourseInfoFragment();
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
        return inflater.inflate(R.layout.fragment_minerva_course_info, container, false);
    }

    @Override
    public void onViewCreated(View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        TextView courseTitle = $(v, R.id.course_title);
        TextView courseCode = $(v, R.id.course_code);
        TextView courseTutor = $(v, R.id.course_tutor);
        TextView courseYear = $(v, R.id.course_year);
        TextView courseDescription = $(v, R.id.course_description);
        TextView courseFiche = $(v, R.id.course_fiche);

        courseTitle.setText(course.getTitle());
        courseCode.setText(course.getCode());
        courseTutor.setText(Utils.fromHtml(course.getTutorName()));
        courseYear.setText(getAcademicYear());

        if(TextUtils.isEmpty(course.getDescription())) {
            $(v, R.id.course_description_header).setVisibility(View.GONE);
        } else {
            courseDescription.setText(Utils.fromHtml(course.getDescription()));
        }

        final String url = getUrl();
        if(url == null) {
            $(v, R.id.course_fiche_header).setVisibility(View.GONE);
            courseFiche.setVisibility(View.GONE);
        } else {
            courseFiche.setOnClickListener(view -> NetworkUtils.maybeLaunchBrowser(view.getContext(), url));
        }
    }

    private String getAcademicYear() {
        if(course.getAcademicYear() == 0) {
            return getContext().getString(R.string.without);
        } else {
            return String.valueOf(course.getAcademicYear());
        }
    }

    private String getUrl() {
        int year = course.getAcademicYear();
        if(year == 0) {
            year = DEFAULT_YEAR;
        }

        if(course.getCode() == null) {
            return null;
        }

        return String.format(URL, year, course.getCode());
    }
}