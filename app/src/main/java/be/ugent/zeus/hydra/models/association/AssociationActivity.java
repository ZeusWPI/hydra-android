package be.ugent.zeus.hydra.models.association;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.JsonAdapter;

import java.util.Date;

import be.ugent.zeus.hydra.models.converters.BooleanJsonAdapter;
import be.ugent.zeus.hydra.models.converters.TimeStampDateJsonAdapter;

/**
 * Created by feliciaan on 27/01/16.
 */
public class AssociationActivity implements Parcelable {
    public String title;
    @JsonAdapter(TimeStampDateJsonAdapter.class)
    public Date start;
    @JsonAdapter(TimeStampDateJsonAdapter.class)
    public Date end;
    public String location;
    public double latitude;
    public double longitude;
    public String description;
    public String url;
    public String facebook_id;
    @JsonAdapter(BooleanJsonAdapter.class)
    public boolean highlighted;
    public Association association;

    protected AssociationActivity(Parcel in) {
        title = in.readString();
        start = new Date(in.readLong());
        end = new Date(in.readLong());
        location = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        description = in.readString();
        url = in.readString();
        facebook_id = in.readString();
        highlighted = in.readByte() == 1;
        association = in.readParcelable(Association.class.getClassLoader());
    }

    public static final Creator<AssociationActivity> CREATOR = new Creator<AssociationActivity>() {
        @Override
        public AssociationActivity createFromParcel(Parcel in) {
            return new AssociationActivity(in);
        }

        @Override
        public AssociationActivity[] newArray(int size) {
            return new AssociationActivity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeLong(start.getTime());
        dest.writeLong(end.getTime());
        dest.writeString(location);
        dest.writeString(description);
        dest.writeString(url);
        dest.writeString(facebook_id);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeByte((byte) (highlighted ? 1 : 0));
        dest.writeParcelable(association, flags);

    }
}
