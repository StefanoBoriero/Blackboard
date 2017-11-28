package dima.it.polimi.blackboard.listeners;

import android.widget.TextView;

import dima.it.polimi.blackboard.model.ToDoTaskParcelable;

/**
 * Created by Stefano on 27/11/2017.
 */

public interface TodoTaskItemClickListener {
    void onTodoTaskItemClicked(int pos, ToDoTaskParcelable todoTask, TextView sharedElement);
}
