package dima.it.polimi.blackboard.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Object representing a detail of an item
 * Created by Stefano on 12/01/2018.
 */

public class Detail implements Parcelable{
    private Integer iconResId;
    private String content;

    public Detail(Integer icon, String content){
        this.content = content;
        this.iconResId = icon;
    }

    private Detail(Parcel in){
        iconResId = in.readInt();
        content = in.readString();
    }

    public static final Creator<Detail> CREATOR = new Creator<Detail>() {
        @Override
        public Detail createFromParcel(Parcel in) {
            return new Detail(in);
        }

        @Override
        public Detail[] newArray(int size) {
            return new Detail[size];
        }
    };

    public Integer getIconResId(){
        return iconResId;
    }

    public String getContent(){
        return content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(iconResId);
        dest.writeString(content);
    }
}
