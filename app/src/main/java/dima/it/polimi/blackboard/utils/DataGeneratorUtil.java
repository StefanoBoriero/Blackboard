package dima.it.polimi.blackboard.utils;

import java.util.ArrayList;
import java.util.List;

import dima.it.polimi.blackboard.model.TodoItem;

/**
 * TODO delete this class
 * This class provides some utility functions to generate fake data for UI design and testing
 * Created by Stefano on 15/12/2017.
 */

public class DataGeneratorUtil {

    public static List<TodoItem> generateTodoItems(int amount){
        List<TodoItem> out = new ArrayList<>();
        int i=0;

        for(i=0; i<amount; i++){
            TodoItem item = new TodoItem("Clean" + i, "Housekeeping", "The kitchen is dirty");
            out.add(item);
        }
        return out;
    }
}
