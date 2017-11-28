package dima.it.polimi.blackboard.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.fragments.ToDoTaskDetailFragment;
import dima.it.polimi.blackboard.fragments.ToDoTaskListFragment;
import dima.it.polimi.blackboard.model.ToDoTaskParcelable;

public class GroupListActivity extends AppCompatActivity implements ToDoTaskListFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content, ToDoTaskListFragment.newInstance(1))
                .commit();
    }

    @Override
    public void onListFragmentInteraction(ToDoTaskParcelable item) {

    }
}
