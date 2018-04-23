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
    public static final String ADDED_MESSAGE = "Number of tasks added: ";

    private Date day;
    private int completedItems;
    private int createdItems;

    public DayResume(){
        //Empty constructor for Firestore serialization
    }

    public DayResume(int completedItems, int addedTasks){
        this.completedItems = completedItems;
        this.createdItems = addedTasks;
    }

    public int getCompletedItems(){
        return completedItems;
    }

    public void setCompletedItems(int completedItems){
        this.completedItems = completedItems;
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
