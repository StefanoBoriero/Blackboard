package dima.it.polimi.blackboard.model;

import java.text.DecimalFormat;

/**
 * Object representing what has been done in one day
 * Created by Stefano on 30/01/2018.
 */

public class DayResume {
    public static final String COMPLETED_MESSAGE = "Number of tasks completed: ";
    public static final String BALANCE_MESSAGE = "Total balance movement: ";
    public static final String ADDED_MESSAGE = "Number of tasks added: ";

    private String day;
    private int taskCompleted;
    private String balanceDiff;
    private int addedTasks;

    public DayResume(int taskCompleted, double balanceDiff, int addedTasks){
        DecimalFormat numberFormat = new DecimalFormat("#.00");

        this.day = "August 19 2017";
        this.taskCompleted = taskCompleted;
        this.balanceDiff = numberFormat.format(balanceDiff);
        this.addedTasks = addedTasks;
    }

    public int getTaskCompleted(){
        return taskCompleted;
    }

    public String getBalanceDiff(){
        return balanceDiff;
    }

    public int getAddedTasks(){
        return addedTasks;
    }

    public String getDay(){
        return day;
    }
}
