package dima.it.polimi.blackboard.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class represents a payment item issued by an user
 * Created by simone on 21/12/2017.
 */

public class PaymentItem implements Parcelable{
    private final long id;
    private final String name;
    private final String from;
    private final String to;
    private final double price;

    protected PaymentItem(Parcel in) {
        //Order must be the same of writeToParcel
        this.id = in.readLong();
        this.name = in.readString();
        this.from = in.readString();
        this.to = in.readString();
        this.price = in.readDouble();
    }

    public PaymentItem(long id, String name, String from, String to, Double price){
        this.id = id;
        this.name = name;
        this.from = from;
        this.to = to;
        this.price = price;
    }

    public long getId(){return this.id;}

    public String getName(){
        return this.name;
    }

    public String getEmitter(){
        return this.from;
    }

    public String getReceiver(){
        return this.to;
    }

    public Double getPrice(){return this.price;}

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.from);
        dest.writeString(this.to);
        dest.writeDouble(this.price);
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
