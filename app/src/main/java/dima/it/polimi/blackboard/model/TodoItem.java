package dima.it.polimi.blackboard.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Object representing a task to be done
 * Created by Stefano on 27/11/2017.
 */

public class TodoItem implements Parcelable {
    private String id;
    private String name;
    private String type;
    private Date createdOn;
    private String createdOnString;
    private String createdBy;
    private String priority;
    private Map<String, Object> additionalInfo;
    private String suggestedTo;
    private boolean taken;
    private String takenBy;

    public TodoItem(){
        //Needed for Firebase
    }

    TodoItem(Parcel in) {
        //Order must be the same of writeToParcel
        id = in.readString();
        name = in.readString();
        type = in.readString();
        suggestedTo = in.readString();
        createdBy = in.readString();
        createdOnString = in.readString();
        additionalInfo = new HashMap<>();
        in.readMap(additionalInfo, HashMap.class.getClassLoader());
    }

    public TodoItem(String name, String type, String priority, Map<String, Object> additionalInfo){
        this.name = name;
        this.type = type;
        this.priority = priority;
        this.additionalInfo = additionalInfo;
        this.taken = false;

        this.createdOn = Calendar.getInstance().getTime();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            this.createdBy = user.getUid();
        }
    }

    public List<Detail> getDetails(){
        List<Detail> details = new ArrayList<>(additionalInfo.size());
        for (Map.Entry<String, Object> entry : additionalInfo.entrySet()) {
            Detail det = new Detail(entry.getKey(), entry.getValue());
            details.add(det);
        }
        return details;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getId(){return this.id;}

    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getType(){
        return this.type;
    }

    public void setPriority(String priority){
        this.priority = priority;
    }

    public String getPriority(){
        return priority;
    }

    public void setTaken(boolean b){
        taken = b;
    }

    public boolean isTaken(){
        return taken;
    }

    public void setTakenBy(String takenBy){
        this.takenBy = takenBy;
    }

    public String getTakenBy(){
        return this.takenBy;
    }

    public void setSuggestedTo(String suggestion){
        this.suggestedTo = suggestion;
    }

    public String getSuggestedTo(){
        return this.suggestedTo;
    }

    public void setCreatedBy(String user){
        this.createdBy = user;
    }

    public String getCreatedBy(){
        return createdBy;
    }

    public void setCreatedOn(Date date){
        this.createdOn = date;
        Calendar myCal = new GregorianCalendar();
        myCal.setTime(date);
        this.createdOnString = decodeDate(myCal);
    }

    public String getMyCreatedOn(){
        return createdOnString;
    }

    public void setAdditionalInfo(Map<String, Object> info){
        this.additionalInfo = info;
    }

    public Map<String, Object> getAdditionalInfo(){
        return this.additionalInfo;
    }

    private String decodeDate(Calendar calendar){
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        String day = String.valueOf(dayOfMonth);

        int monthInt = calendar.get(Calendar.MONTH);
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (monthInt >= 0 && monthInt <= 11 ) {
            month = months[monthInt];
        }

        return day + " " + month;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.type);
        dest.writeString(this.suggestedTo);
        dest.writeString(this.createdBy);
        dest.writeString(this.createdOnString);
        dest.writeMap(this.additionalInfo);
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
