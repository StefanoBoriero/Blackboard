package dima.it.polimi.blackboard.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.model.ToDoTask;

/**
 * Adapter to display the list of tasks to be done
 * Created by Stefano on 23/11/2017.
 */

public class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.ViewHolder> {
    private List<ToDoTask> todoList;
    private Context mContext;

    public ToDoListAdapter(Context context, List<ToDoTask> todoList){
        this.mContext = context;
        this.todoList = todoList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View todoTaskView = inflater.inflate(R.layout.todo_task_item, parent, false);
        ViewHolder holder = new ViewHolder(todoTaskView);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ToDoTask todoTask = todoList.get(position);

        holder.task_name.setText(todoTask.getName());
        holder.task_type.setText(todoTask.getType());
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    private Context getContext(){
        return mContext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        /*TODO
        Change this and todo_task_item layout to a better option
         */
        TextView task_name;
        TextView task_type;

        public ViewHolder(View itemView){
            super(itemView);

            task_name = itemView.findViewById(R.id.task_name);
            task_type = itemView.findViewById(R.id.task_type);
        }
    }
}


