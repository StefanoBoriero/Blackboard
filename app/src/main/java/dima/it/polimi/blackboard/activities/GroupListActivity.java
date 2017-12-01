package dima.it.polimi.blackboard.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.adapters.TodoListAdapter;
import dima.it.polimi.blackboard.fragments.ToDoTaskDetailFragment;
import dima.it.polimi.blackboard.fragments.TodoItemListFragment;
import dima.it.polimi.blackboard.helper.TodoItemTouchHelper;
import dima.it.polimi.blackboard.model.TodoItem;

public class GroupListActivity extends AppCompatActivity implements TodoItemListFragment.OnListFragmentInteractionListener,
        ToDoTaskDetailFragment.OnFragmentInteractionListener, TodoListAdapter.TodoListAdapterListener,
        TodoItemTouchHelper.TodoItemTouchHelperListener{

    private List<TodoItem> todoItemList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TodoListAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;


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

    private void createSampleTodoItems(){
        for(int i=0; i<31; i++){
            TodoItem item = new TodoItem("Clean " + i,
                    "Housing", "The house has to be cleaned");
            todoItemList.add(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_list, menu);
        return true;
    }

    /**
     * This method is called when an item from the list is clicked.
     * Once the click has been performed, the activity should load and
     * display the fragment with the item's details.
     * @param item the clicked item
     */
    @Override
    public void onListFragmentInteraction(TodoItem item, View itemView) {
        //TODO get the real transition name
        View sharedElement = itemView.findViewById(R.id.task_name);
        String transitionName = sharedElement.getTransitionName();
        Fragment detailFragment = ToDoTaskDetailFragment.newInstance(item, transitionName);
/*
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .addSharedElement(sharedElement, transitionName)
                //.replace(R.id.fragment_detail_placeholder, detailFragment)
                .replace(R.id.content, detailFragment)
                .commit();
             */
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onTodoItemClicked(TodoItem todoItem, View view) {
        //TODO implement detail fragment display
        Snackbar.make(view, "Item " + todoItem.getName() + " have been clicked",
                Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        View view = viewHolder.itemView;

        //TODO implement take in charge or delete functionalities
        if(direction == ItemTouchHelper.LEFT){
            Snackbar.make(view, "OH SHIT YOU SWIPED LEFT",
                    Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        else if(direction == ItemTouchHelper.RIGHT){
            Snackbar.make(view, "OH SHIT YOU SWIPED RIGHT",
                    Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }


}
