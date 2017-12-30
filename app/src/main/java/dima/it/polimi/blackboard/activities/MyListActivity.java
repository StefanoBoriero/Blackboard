package dima.it.polimi.blackboard.activities;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import java.util.List;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.adapters.TodoListAdapter;
import dima.it.polimi.blackboard.fragments.TodoItemDetailFragment;
import dima.it.polimi.blackboard.fragments.TodoItemListFragment;
import dima.it.polimi.blackboard.model.TodoItem;
import dima.it.polimi.blackboard.utils.DataGeneratorUtil;

/**
 * This class shows the list of activities a user has taken charge of.
 * Created by Stefano on 27/12/2017.
 */

public class MyListActivity extends AppCompatActivity implements TodoItemListFragment.OnListFragmentInteractionListener,
        TodoItemDetailFragment.OnTodoItemDetailInteraction{
    private static final int ACCEPT_TASK_REQUEST = 1;

    private Fragment detailFragment;
    private TodoItemListFragment listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);


        displayListFragment();
        detailFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_detail);
        if(detailFragment != null){
            displayDetailFragment();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void displayListFragment(){
        //Todo remove this call
        List<TodoItem> items = DataGeneratorUtil.generateTodoItems(30);
        listFragment = TodoItemListFragment.newInstance(1, items);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_list_container, listFragment)
                .commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    private void displayDetailFragment(){
        //TODO set detail of first todoItem
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_list, menu);

        // Adding back navigation on toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Implementing the search functionality
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView)menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        listFragment.setSearchView(searchView);

        super.onCreateOptionsMenu(menu);
        return true;
    }

    /**
     * This method is called when an item in the list displayed by the fragment is clicked.
     * The activity creates a new fragment containing the details of the item clicked and shows it
     * @param todoItem the item clicked
     * @param view the view clicked
     */
    @SuppressLint("RestrictedApi")
    @Override
    public void onItemClick(TodoItem todoItem, View view, int clickedPosition) {
        if(detailFragment == null){
            View sharedImage = view.findViewById(R.id.user_icon);
            View sharedElementName = view.findViewById(R.id.item_name);
            final Intent intent = new Intent(MyListActivity.this, DetailTodoItemActivity.class);
            intent.putExtra(getResources().getString(R.string.todo_item), todoItem);
            intent.putExtra(getResources().getString(R.string.icon_tr_name), sharedImage.getTransitionName());
            intent.putExtra(getResources().getString(R.string.name_tr_name),sharedElementName.getTransitionName() );
            intent.putExtra(getResources().getString(R.string.position), clickedPosition);

            Pair<View, String> sharedImagePair = Pair.create(sharedImage, sharedImage.getTransitionName());
            Pair<View, String> sharedNamePair = Pair.create(sharedElementName, sharedElementName.getTransitionName());

            final ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(this, sharedImagePair, sharedNamePair);

            startActivityForResult(intent, ACCEPT_TASK_REQUEST, options.toBundle());
        }
        else{
            //TODO implement the fragment update for bigger screens
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == ACCEPT_TASK_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                RecyclerView recyclerView = findViewById(R.id.recycler_view);
                TodoListAdapter adapter = (TodoListAdapter)recyclerView.getAdapter();
                int position = data.getIntExtra(getResources().getString(R.string.position),0);
                adapter.removeItem(position);
            }
        }
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }


    @Override
    public void onAcceptClick(TodoItem todoItem) {
        //onBackPressed();
    }

    @Override
    public void onCloseClick() {
        //onBackPressed();
    }


}