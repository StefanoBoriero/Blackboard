package dima.it.polimi.blackboard.utils;

import java.util.ArrayList;
import java.util.List;

import dima.it.polimi.blackboard.model.Achievement;
import dima.it.polimi.blackboard.model.PaymentItem;
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
    private static final String[] froms = {"Ezio Greggio", "Enzo Iacchetti" , "Fabio Fazio", "Stefy & Simo"};
    private static final String[] tos = {"Ezio Greggio", "Enzo Iacchetti" , "Fabio Fazio", "Stefy & Simo"};
    private static final String[] titles = {"Vorwerk Folletto", "Donald Trump"};
    private static final String[] descriptions = {"You completed 10 Housekeeping items", "You completed 10 Shopping items"};

    public static List<TodoItem> generateTodoItems(int amount){
        List<TodoItem> out = new ArrayList<>();
        int i=0;

        for(i=0; i<amount; i++){
            int name = (int)(Math.random() * 3);
            int type = (int)(Math.random() * 3);
            double price = (double) (Math.random() * 3);

            TodoItem item = new TodoItem(i,names[name], types[type], "Lorem ipsum dolor sit amet",price);
            out.add(item);
        }
        return out;
    }

    public static List<PaymentItem> generatePaymentItems(int amount){
        List<PaymentItem> out = new ArrayList<>();
        int i=0;

        for(i=0; i<amount; i++){
            int name = (int)(Math.random() * 3);
            int from = (int)(Math.random() * 4);
            int to = (int)(Math.random() * 4);
            double price = (double) (Math.random() * 4);

            PaymentItem item = new PaymentItem(i,names[name],froms[from],tos[to] ,price);
            out.add(item);
        }
        return out;
    }

    public static List<Achievement> generateAchievements(int amount){
        List<Achievement> list = new ArrayList<>();

        for(int i=0; i<amount; i++) {
            int pos = (int) (Math.random() * 2);
            Achievement a = new Achievement(titles[pos], descriptions[pos]);
            list.add(a);
        }
        return list;
    }
}
