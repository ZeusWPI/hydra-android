package be.ugent.zeus.hydra.models.association;

import android.os.Parcel;
import android.os.Parcelable;
import be.ugent.zeus.hydra.models.converters.BooleanJsonAdapter;
import be.ugent.zeus.hydra.models.converters.TimeStampDateJsonAdapter;
import com.google.gson.annotations.JsonAdapter;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by feliciaan on 04/02/16.
 */
public class NewsItem implements Serializable, Parcelable {
    private int id;
    private String title;
    private String content;
    private Association association;
    @JsonAdapter(TimeStampDateJsonAdapter.class)
    private Date date;
    @JsonAdapter(BooleanJsonAdapter.class)
    private boolean highlighted;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public Association getAssociation() {
        return association;
    }

    public void setAssociation(Association association) {
        this.association = association;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeParcelable(this.association, flags);
        dest.writeLong(this.date != null ? this.date.getTime() : -1);
        dest.writeByte(this.highlighted ? (byte) 1 : (byte) 0);
    }

    public NewsItem() {
    }

    protected NewsItem(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.content = in.readString();
        this.association = in.readParcelable(Association.class.getClassLoader());
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
        this.highlighted = in.readByte() != 0;
    }

    public static final Parcelable.Creator<NewsItem> CREATOR = new Parcelable.Creator<NewsItem>() {
        @Override
        public NewsItem createFromParcel(Parcel source) {
            return new NewsItem(source);
        }

        @Override
        public NewsItem[] newArray(int size) {
            return new NewsItem[size];
        }
    };
}
