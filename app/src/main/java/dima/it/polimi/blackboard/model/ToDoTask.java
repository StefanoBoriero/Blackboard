package dima.it.polimi.blackboard.model;

/**
 * This class represents a task that has to be done
 * Created by Stefano on 23/11/2017.
 */

public class ToDoTask {
    private String name;
    private String type;
    private String description;
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
