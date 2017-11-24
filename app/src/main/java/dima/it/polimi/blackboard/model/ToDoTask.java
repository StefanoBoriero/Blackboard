package dima.it.polimi.blackboard.model;


import android.content.res.Resources;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import dima.it.polimi.blackboard.R;

/**
 * This class represents a task that has to be done
 * Created by Stefano on 23/11/2017.
 */

public class ToDoTask{
    //TODO try to set serialized name via R class

    @SerializedName("title")
    @Expose
    private String name;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("priority")
    @Expose
    private int priority;

    public ToDoTask(String name, String type, String description){
        this.name = name;
        this.type = type;
        this.description = description;
    }

    public ToDoTask(String name, String type){
        this.name = name;
        this.type = type;
        this.description="";
    }

    public String getName(){
        return this.name;
    }

    public String getType(){
        return this.type;
    }

    public String getDescription(){
        return this.description;
    }
}
