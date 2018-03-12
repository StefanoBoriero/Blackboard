package dima.it.polimi.blackboard.model;

import com.google.firebase.firestore.Exclude;

import java.text.DecimalFormat;
import java.util.Date;

/**
 * Object representing what has been done in one day
 * Created by Stefano on 30/01/2018.
 */

public class DayResume {
    public static final String COMPLETED_MESSAGE = "Number of tasks completed: ";
    public static final String BALANCE_MESSAGE = "Total balance movement: ";
    public static final String ADDED_MESSAGE = "Number of tasks added: ";

    private Date day;
    private int completedItems;
    @Exclude private String balanceDiff;
    private int createdItems;

    public DayResume(){
        //Empty constructor for Firestore serialization
    }

    public DayResume(int completedItems, double balanceDiff, int addedTasks){
        DecimalFormat numberFormat = new DecimalFormat("#.00");

        this.completedItems = completedItems;
        this.balanceDiff = numberFormat.format(balanceDiff);
        this.createdItems = addedTasks;
    }

    public int getCompletedItems(){
        return completedItems;
    }

    public void setCompletedItems(int completedItems){
        this.completedItems = completedItems;
    }

    public String getBalanceDiff(){
        return balanceDiff;
    }

    public int getCreatedItems(){
        return createdItems;
    }

    public void setCreatedItems(int t){
        this.createdItems = t;
    }

    public Date getDay(){
        return day;
    }

    public void setDay(Date d){
        this.day = d;
    }
}
