package dima.it.polimi.blackboard.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

/**
 * Represents an achievement achieved by the user
 * Created by Stefano on 30/12/2017.
 */

public class Achievement implements Parcelable {
    private String title;
    @Exclude private String description;
    private String type;
    private int completedTasks;

    public Achievement(){
        //Empty constructor for Firestore
    }

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

    public void setTitle(String title){
        this.title = title;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getType(){
        return this.type;
    }

    public void setCompletedTasks(int t){
        this.completedTasks = t;
    }

    public int getCompletedTasks(){
        return this.completedTasks;
    }

    public String getDescription(){
        return completedTasks + " items";
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
