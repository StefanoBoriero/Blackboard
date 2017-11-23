package dima.it.polimi.blackboard.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.adapters.ToDoListAdapter;
import dima.it.polimi.blackboard.model.ToDoTask;


/**
 * This Activity shows the user the list of activities his group has defined.
 * It also allows him to view more details about an existing one and pick it.
 * Lastly, the user can add a new task to the list
 * Created by Stefano on 22/11/2017.
 */

public class GroupListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_group)).setTitle("Group List"/*R.string.toolbar_group_list*/);

        /*
        TESTING CODE TO BE ELIMINATED FROM HERE!!!!!!!!!
         */

        int i;
        List<ToDoTask> list = new ArrayList<>();
        for(i=0; i<42; i++){
            ToDoTask task = new ToDoTask("Clean", "Cleaning", "The house has to be cleaned");
            list.add(task);
        }

        /* ELIMINATE UNTIL HERE !!!!!!!!!!!!!!!!!!*/

        RecyclerView rv = findViewById(R.id.todo_group_list);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new ToDoListAdapter(getBaseContext(),list));
    }

}
