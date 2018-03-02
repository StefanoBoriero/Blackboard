package dima.it.polimi.blackboard.model;

/*
  This class represents a roomMate of one of the houses the user is part of
  Created by simone on 17/01/2018.
 */

import android.os.Parcel;
import android.os.Parcelable;

public class RoomMate implements Parcelable {
    private final String name;

    public RoomMate(String name) {
        this.name = name;
    }

    protected RoomMate(Parcel in) {
        name = in.readString();
    }

    public String getName() {
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


    public static final Creator<RoomMate> CREATOR = new Creator<RoomMate>() {
        @Override
        public RoomMate createFromParcel(Parcel in) {
            return new RoomMate(in);
        }

        @Override
        public RoomMate[] newArray(int size) {
            return new RoomMate[size];
        }
    };
}
