package dima.it.polimi.blackboard.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

/**
 * This class represents a payment item issued by an user
 * Created by simone on 21/12/2017.
 */

public class PaymentItem implements Parcelable{
    private String id;
    private  String name;
    private double price;
    private  String performedBy;
    private Date performedOn;
    private int numberOfRoommates;

    public PaymentItem()
    {
        //used for firebase
    }

    protected PaymentItem(Parcel in) {
        //Order must be the same of writeToParcel
        this.id = in.readString();
        this.name = in.readString();
        this.price = in.readFloat();
        this.performedBy = in.readString();
        this.performedOn = null;
        this.numberOfRoommates = in.readInt();
    }

    public PaymentItem(String id, String name, Double price,Date performedOn,int numberOfRoommates){
        this.id = id;
        this.name = name;
        this.price = price;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            this.performedBy = user.getUid();
        }
        this.performedOn = performedOn;
        this.numberOfRoommates = numberOfRoommates;
    }

    public PaymentItem(String id, String name, Double price,Date performedOn, String performedBy,int numberOfRoommates){
        this.id = id;
        this.name = name;
        this.price = price;
        this.performedBy = performedBy;
        this.performedOn = performedOn;
        this.numberOfRoommates = numberOfRoommates;
    }

    public String getPerformedBy(){ return  this.performedBy;}

    public void setPerformedBy(String performedBy){ this.performedBy = performedBy;}

    public void setName(String name){ this.name = name;}

    public void setPrice(Float price){ this.price = price;}

    public void setId(String id){this.id = id;}

    public String getId()
    {
        return this.id;
    }

    public int getNumberOfRoommates() {return this.numberOfRoommates;}


    public String getName(){
        return this.name;
    }
    public Date getPerformedOn(){return this.performedOn;}


    public double getPrice(){return this.price;}

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeDouble(this.price);
        dest.writeString(this.performedBy);
        dest.writeInt(this.numberOfRoommates);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PaymentItem> CREATOR = new Creator<PaymentItem>() {
        @Override
        public PaymentItem createFromParcel(Parcel in) {
            return new PaymentItem(in);
        }

        @Override
        public PaymentItem[] newArray(int size) {return new PaymentItem[size];}
    };
}
