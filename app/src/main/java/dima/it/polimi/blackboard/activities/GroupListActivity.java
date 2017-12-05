package dima.it.polimi.blackboard.activities;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.adapters.TodoListAdapter;
import dima.it.polimi.blackboard.fragments.TodoItemDetailFragment;
import dima.it.polimi.blackboard.fragments.TodoItemListFragment;
import dima.it.polimi.blackboard.helper.TodoDetailAnimatorHelper;
import dima.it.polimi.blackboard.model.TodoItem;

/**
 * This activity presents the list of todo_items that have been added to the group but nobody has
 * taken in charge. It displays at first a fragment with the list in a RecyclerView, and on click of
 * one item displays the details in another fragment.
 */
public class GroupListActivity extends AppCompatActivity implements TodoItemListFragment.OnListFragmentInteractionListener,
TodoItemDetailFragment.OnTodoItemDetailInteraction, TodoDetailAnimatorHelper.EndAnimationListener{

    private List<TodoItem> todoItemList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TodoListAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TodoDetailAnimatorHelper detailAnimatorHelper;
    private boolean isExpanded; //boolean flag for item detail expanded


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
/*
        recyclerView = findViewById(R.id.recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        // TODO implement refreshing
        // swipeRefreshLayout.setOnRefreshListener(this);

        createSampleTodoItems();

        mAdapter = new TodoListAdapter(this, todoItemList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
*/

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content, TodoItemListFragment.newInstance(1))
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_list, menu);
        return true;
    }


    /**
     * This method is called when an item in the list displayed by the fragment is clicked.
     * The activity creates a new fragment containing the details of the item clicked and shows it
     * @param todoItem the item clicked
     * @param view the view clicked
     */
    @Override
    public void onItemClick(TodoItem todoItem, View view) {
        //TODO implement detail fragment display
        detailAnimatorHelper = new TodoDetailAnimatorHelper(view, findViewById(R.id.content),
                getApplicationContext(), todoItem, this);
        detailAnimatorHelper.expand();
        isExpanded = true;

    }

    @Override
    public void onBackPressed(){
        if(isExpanded){
            detailAnimatorHelper.collapse();
            isExpanded = false;
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public void onSwipeLeft(){
        //TODO implement take in charge functionality
        View view = findViewById(R.id.root_view);
        Snackbar.make(view, "OH SHIT YOU SWIPED LEFT",
                Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public void onSwipeRight(){
        //TODO implement take in charge or delete functionality
        View view = findViewById(R.id.root_view);
        Snackbar.make(view, "OH SHIT YOU SWIPED RIGHT",
                Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }


    @Override
    public void onAcceptClick(TodoItem todoItem) {
        //onBackPressed();
    }

    @Override
    public void onCloseClick() {
        //onBackPressed();
    }

    @Override
    public void onAnimationEnded(TodoItem item) {
        //TODO load the fragment with the description
    }
}
