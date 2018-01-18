package dima.it.polimi.blackboard.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents an achievement achieved by the user
 * Created by Stefano on 30/12/2017.
 */

public class Achievement implements Parcelable {
    private final String title;
    private final String description;

    private Achievement(Parcel in){
        title = in.readString();
        description = in.readString();
    }

    public Achievement(String title, String description){
        this.title = title;
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
    }

    public String getTitle(){
        return this.title;
    }

    public String getDescription(){
        return this.description;
    }

    public static final Creator<Achievement> CREATOR = new Creator<Achievement>() {
        @Override
        public Achievement createFromParcel(Parcel in) {
            return new Achievement(in);
        }

        @Override
        public Achievement[] newArray(int size) {
            return new Achievement[size];
        }
    };
}
