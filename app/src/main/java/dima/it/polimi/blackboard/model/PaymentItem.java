package dima.it.polimi.blackboard.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * This class represents a payment item issued by an user
 * Created by simone on 21/12/2017.
 */

public class PaymentItem implements Parcelable{
    private  String name;
    private  float price;
    private  String performedBy;

    public PaymentItem()
    {
        //used for firebase
    }

    protected PaymentItem(Parcel in) {
        //Order must be the same of writeToParcel
        this.name = in.readString();
        this.price = in.readFloat();
        this.performedBy = in.readString();
    }

    public PaymentItem( String name, float price){
        this.name = name;
        this.price = price;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            this.performedBy = user.getUid();
        }
    }

    public String getPerformedBy(){ return  this.performedBy;}

    public void setPerformedBy(String performedBy){ this.performedBy = performedBy;}

    public void setName(String name){ this.name = name;}

    public void setPrice(Float price){ this.price = price;}


    public String getName(){
        return this.name;
    }


    public Float getPrice(){return this.price;}

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeDouble(this.price);
        dest.writeString(this.performedBy);
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
