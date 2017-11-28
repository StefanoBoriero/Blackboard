package dima.it.polimi.blackboard.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.fragments.ToDoTaskDetailFragment;
import dima.it.polimi.blackboard.fragments.ToDoTaskListFragment;
import dima.it.polimi.blackboard.model.TodoTask;

public class GroupListActivity extends AppCompatActivity implements ToDoTaskListFragment.OnListFragmentInteractionListener, ToDoTaskDetailFragment.OnFragmentInteractionListener {

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

    /**
     * This method is called when an item from the list is clicked.
     * Once the click has been performed, the activity should load and
     * display the fragment with the item's details.
     * @param item the clicked item
     */
    @Override
    public void onListFragmentInteraction(TodoTask item) {
        //TODO get the real transition name
        String transitionName = "";
        Fragment detailFragment = ToDoTaskDetailFragment.newInstance(item, transitionName);

        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragment_detail_placeholder, detailFragment)
                .commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
