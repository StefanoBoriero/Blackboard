package dima.it.polimi.blackboard.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.activities.DetailTaskActivity;
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

        return new ViewHolder(todoTaskView);
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

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        /*TODO
        Change this and todo_task_item layout to a better option
         */
        TextView task_name;
        TextView task_type;

        public ViewHolder(View itemView){
            super(itemView);

            itemView.setOnClickListener(this);
            task_name = itemView.findViewById(R.id.task_name);
            task_type = itemView.findViewById(R.id.task_type);
        }

        @Override
        public void onClick(View v) {
            TextView sharedView = v.findViewById(R.id.task_name);

            //Gets the activity that hosts the view:
            Activity host = (Activity)v.getContext();

            Bundle bundle = ActivityOptions
                    .makeSceneTransitionAnimation(
                            host,
                            sharedView,
                            sharedView.getTransitionName()
                    ).toBundle();
            Intent intent = new Intent(host, DetailTaskActivity.class);

            String paramName = v.getResources().getString(R.string.task_name);
            intent.putExtra(paramName, (String)sharedView.getText());
            host.startActivity(intent, bundle);
        }
    }
}


