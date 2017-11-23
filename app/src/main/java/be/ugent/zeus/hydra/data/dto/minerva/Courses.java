package be.ugent.zeus.hydra.data.dto.minerva;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by feliciaan on 21/06/16.
 */
public class Courses implements Serializable {

    private ArrayList<CourseDTO> courses;

    public ArrayList<CourseDTO> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<CourseDTO> courses) {
        this.courses = courses;
    }
}
