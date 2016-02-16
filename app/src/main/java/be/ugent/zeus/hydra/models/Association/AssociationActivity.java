package be.ugent.zeus.hydra.models.Association;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by feliciaan on 27/01/16.
 */
public class AssociationActivity implements Parcelable {
    public String title;
    //public Date start; //TODO: add @-with format
    //public Date end;
    public String location;
    public double latitude;
    public double longitude;
    public String description;
    public String url;
    public String facebook_id;
    public int highlighted; //TODO: make boolean
    public Association association;

    protected AssociationActivity(Parcel in) {
        title = in.readString();
        location = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        description = in.readString();
        url = in.readString();
        facebook_id = in.readString();
        highlighted = in.readInt();
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

    public boolean isHiglighted() {
        return highlighted == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(location);
        dest.writeString(description);
        dest.writeString(url);
        dest.writeString(facebook_id);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeInt(highlighted);
        dest.writeParcelable(association,flags);

    }
}
