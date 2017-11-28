package dima.it.polimi.blackboard.adapters;

import android.app.Activity;
import android.app.ActivityOptions;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.List;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.activities.DetailTaskActivity;
import dima.it.polimi.blackboard.fragments.ToDoTaskDetailFragment;
import dima.it.polimi.blackboard.model.ToDoTask;
import dima.it.polimi.blackboard.model.ToDoTaskParcelable;

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
        Gson gson = new Gson();
        String todoTaskJson = gson.toJson(todoTask);
        JsonObject jsonObject = new JsonParser().parse(todoTaskJson).getAsJsonObject();

        View sharedElement = holder.getTaskName();
        ViewCompat.setTransitionName(sharedElement, todoTask.getName() + position);  //TODO change the transition name

        holder.bind(jsonObject);
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    private Context getContext(){
        return mContext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //TODO Change this and todo_task_item layout to a better option

        private TextView taskName;
        private TextView taskType;
        private JsonObject todoTaskJSON;

        private ViewHolder(View itemView){
            super(itemView);

            itemView.setOnClickListener(this);
            taskName = itemView.findViewById(R.id.task_name);
            taskType = itemView.findViewById(R.id.task_type);
        }

        private void bind(JsonObject jsonObject){
            this.todoTaskJSON = jsonObject;

            taskName.setText(todoTaskJSON.get("title").getAsString());
            taskType.setText(todoTaskJSON.get("type").getAsString());
        }

        public View getTaskName(){
            return taskName;
        }

        @Override
        public void onClick(View v) {
            //onClickActivity(v);

            onClickFragment(v);
        }

        private void onClickFragment(View v){
            AppCompatActivity host = (AppCompatActivity) v.getContext();
            TextView sharedElement = v.findViewById(R.id.task_name);
            String transitionName = sharedElement.getTransitionName();
            String taskName = (String)sharedElement.getText();

            ToDoTaskParcelable todoTaskParcelable = new ToDoTaskParcelable("Clean", "Cleaning", "Have to clean");


            FragmentTransaction fragmentTransaction = host.getSupportFragmentManager()
                    .beginTransaction()
                    .addSharedElement(sharedElement, transitionName)
                    .addToBackStack(null)
                    .replace(R.id.detail_additional_info,
                            ToDoTaskDetailFragment.newInstance(todoTaskParcelable,transitionName));
            fragmentTransaction.commit();
            return;
        }

        private void onClickActivity(View v){
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
            intent.putExtra("JSON_task", todoTaskJSON.toString());
            intent.putExtra(paramName, (String)sharedView.getText());
            host.startActivity(intent, bundle);
        }
    }
}


