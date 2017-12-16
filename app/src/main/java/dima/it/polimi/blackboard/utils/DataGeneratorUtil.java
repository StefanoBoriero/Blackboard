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
    private static final String[] priorities = {"High","Medium","Low"};
    private static final String[] types = {"Housing", "Billing", "Shopping"};
    private static final String[] names = {"Pay Netflix", "Fancy Hat", "Name"};

    public static List<TodoItem> generateTodoItems(int amount){
        List<TodoItem> out = new ArrayList<>();
        int i=0;

        for(i=0; i<amount; i++){
            int name = (int)(Math.random() * 3);
            int type = (int)(Math.random() * 3);

            TodoItem item = new TodoItem(i,names[name], types[type], "Lorem ipsum dolor sit amet");
            out.add(item);
        }
        return out;
    }
}
