package be.ugent.zeus.hydra.models.minerva;

import com.google.gson.annotations.SerializedName;

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
}
