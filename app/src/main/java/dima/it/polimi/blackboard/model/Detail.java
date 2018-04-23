package dima.it.polimi.blackboard.model;

import android.os.Parcel;
import android.os.Parcelable;

import dima.it.polimi.blackboard.R;

/**
 * Object representing a detail of an item
 * Created by Stefano on 12/01/2018.
 */

public class Detail implements Parcelable{
    private Integer iconResId;
    private String content;
    private String title;

    public Detail(String title, Object content){
        this.title = title;
        decode(title, content);
    }

    private void decode(String title, Object content){
        switch (title){
            case ("description"):
                iconResId = R.drawable.ic_description_black_24dp;
                this.title = "Description";
                this.content = (String)content;
                break;
            case("amount"):
                iconResId = R.drawable.ic_menu_balance_24dp;
                this.title = "Bill Amount";
                this.content = String.valueOf(content);
                break;
            case("expiration"):
                iconResId = R.drawable.ic_date_range_black_24dp;
                break;
            case("suggestedTo"):
                iconResId = R.drawable.ic_person_black_24dp;
                this.title = "Suggested to";
                this.content = (String)content;
                break;
            case("priority"):
                iconResId = R.drawable.ic_priority_high_black_24dp;
                this.title = "Priority";
                this.content = (String)content;
                break;
            default:
                iconResId = 0;
        }
    }

    private Detail(Parcel in){
        title = in.readString();
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

    public String getTitle(){
        return title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeInt(iconResId);
        dest.writeString(content);
    }
}
