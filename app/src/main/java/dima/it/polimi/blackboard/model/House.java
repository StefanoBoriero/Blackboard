package dima.it.polimi.blackboard.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class represent an House in terms of people in the list.
 * Contains also additional info on its creation (Date and "Owner")
 * Created by Stefano on 14/01/2018.
 */

public class House implements Parcelable {
    private String name;

    public House(String name){
        this.name = name;
    }

    private House(Parcel in) {
        name = in.readString();
    }

    public String getName(){
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }


    public static final Creator<House> CREATOR = new Creator<House>() {
        @Override
        public House createFromParcel(Parcel in) {
            return new House(in);
        }

        @Override
        public House[] newArray(int size) {
            return new House[size];
        }
    };
}
