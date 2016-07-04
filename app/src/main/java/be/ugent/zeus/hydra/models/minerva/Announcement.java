package be.ugent.zeus.hydra.models.minerva;

import be.ugent.zeus.hydra.models.converters.MinervaDateJsonAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by feliciaan on 29/06/16.
 */
public class Announcement {

    private String title;
    private String content;
    @SerializedName("email_sent")
    private boolean emailSent;
    @SerializedName("item_id")
    private int itemId;
    @SerializedName("last_edit_user")
    private String lecturer;
    @JsonAdapter(MinervaDateJsonAdapter.class)
    @SerializedName("last_edit_time")
    private MinervaDate minervaDate;

    private Course course;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isEmailSent() {
        return emailSent;
    }

    public void setEmailSent(boolean emailSent) {
        this.emailSent = emailSent;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public Date getDate() {
        return this.minervaDate.getDate();
    }

    public void setDate(Date date) {
        this.minervaDate.setDate(date);
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public MinervaDate getMinervaDate() {
        return minervaDate;
    }

    public void setMinervaDate(MinervaDate minervaDate) {
        this.minervaDate = minervaDate;
    }
}
