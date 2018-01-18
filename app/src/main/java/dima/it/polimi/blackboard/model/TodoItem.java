package dima.it.polimi.blackboard.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import dima.it.polimi.blackboard.R;

/**
 * Object representing a task to be done
 * Created by Stefano on 27/11/2017.
 */

public class TodoItem implements Parcelable {
    private long id;
    private String name;
    private String type;
    private String description;
    private Double price;
    private String timestamp;
    private List<Detail> details;

    TodoItem(Parcel in) {
        //Order must be the same of writeToParcel
        id = in.readLong();
        name = in.readString();
        type = in.readString();

        details = new ArrayList<>();
        List<Detail> tmp = new ArrayList<>();
        in.readList(tmp, Detail.class.getClassLoader());
        details = new ArrayList<>(tmp);

    }

    public TodoItem(long id, String name, String type, String description, Double price){
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.price = price;
        this.timestamp = "12:02";

        details = new ArrayList<>();
        details.add(createDescription(description));
        details.add(createExpirationDetail("Tomorrow"));
        details.add(createPriceDetail(price.toString()));
        details.add(createSuggestionDetail("You"));
    }

    public TodoItem(long id, String name, String type, String description){
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.timestamp = "12:02";

        details = new ArrayList<>();
        details.add(createDescription(description));
        details.add(createSuggestionDetail("You"));
    }


    private Detail createPriceDetail(String content){
        Integer priceIcon = R.drawable.ic_menu_balance_24dp;
        return new Detail(priceIcon, content);
    }

    private Detail createDescription(String content){
        Integer descriptionIconId = R.drawable.ic_description_black_24dp;
        return new Detail(descriptionIconId, content);
    }

    private Detail createExpirationDetail(String exp){
        Integer expirationResId = R.drawable.ic_date_range_black_24dp;
        return new Detail(expirationResId, exp);
    }

    private Detail createSuggestionDetail(String suggestion){
        Integer suggestionResId = R.drawable.ic_person_black_24dp;
        return new Detail(suggestionResId, suggestion);
    }

    public List<Detail> getDetails(){
        return details;
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

    public Double getPrice(){return this.price;}

    public String getTimestamp(){
        return this.timestamp;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.type);
        dest.writeList(this.details);
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
