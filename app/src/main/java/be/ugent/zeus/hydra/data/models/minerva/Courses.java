package be.ugent.zeus.hydra.data.models.minerva;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by feliciaan on 21/06/16.
 */
public class Courses implements Serializable {

    private ArrayList<Course> courses;

    public ArrayList<Course> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<Course> courses) {
        this.courses = courses;
    }
}
