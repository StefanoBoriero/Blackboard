package dima.it.polimi.blackboard.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Stefano on 27/11/2017.
 */

public class TodoItem implements Parcelable {
    private long id;
    private String name;
    private String type;
    private String description;

    protected TodoItem(Parcel in) {
        //Order must be the same of writeToParcel
        this.id = in.readLong();
        this.name = in.readString();
        this.type = in.readString();
        this.description = in.readString();
    }

    public TodoItem(long id, String name, String type, String description){
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
    }

    public long getId(){return this.id;}

    public String getName(){
        return this.name;
    }

    public String getType(){
        return this.type;
    }

    public String getDescription(){
        return this.description;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.type);
        dest.writeString(this.description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TodoItem> CREATOR = new Creator<TodoItem>() {
        @Override
        public TodoItem createFromParcel(Parcel in) {
            return new TodoItem(in);
        }

        @Override
        public TodoItem[] newArray(int size) {
            return new TodoItem[size];
        }
    };
}
