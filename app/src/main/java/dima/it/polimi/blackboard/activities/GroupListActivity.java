package dima.it.polimi.blackboard.activities;


import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;


import java.util.List;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.fragments.TodoItemDetailFragment;
import dima.it.polimi.blackboard.fragments.TodoItemListFragment;
import dima.it.polimi.blackboard.model.TodoItem;
import dima.it.polimi.blackboard.utils.DataGeneratorUtil;

/**
 * This activity presents the list of todo_items that have been added to the group but nobody has
 * taken in charge. It displays at first a fragment with the list in a RecyclerView, and on click of
 * one item displays the details in another fragment; if the device is large enough, the detail
 * fragment will be displayed permanently on the side of the screen
 */
public class GroupListActivity extends AppCompatActivity implements TodoItemListFragment.OnListFragmentInteractionListener,
TodoItemDetailFragment.OnTodoItemDetailInteraction{

    private static final String EXTRA_TODO_ITEM = "todo_item";

    private Fragment detailFragment;
    private TodoItemListFragment listFragment;

    //
    private boolean isDetailShowed;
    private TodoItem clickedItem;
    private int clickedPosition;
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);


        mFab = (FloatingActionButton) findViewById(R.id.add_fab);
        mFab.setTransitionName("revealCircular");
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
    @Override
    public void onItemClick(TodoItem todoItem, View view) {
        if(detailFragment == null){
            View sharedImage = view.findViewById(R.id.user_icon);
            View sharedElementName = view.findViewById(R.id.item_name);
            final Intent intent = new Intent(GroupListActivity.this, DetailTodoItemActivity.class);
            intent.putExtra(getResources().getString(R.string.todo_item), todoItem);
            intent.putExtra(getResources().getString(R.string.icon_tr_name), sharedImage.getTransitionName());
            intent.putExtra(getResources().getString(R.string.name_tr_name),sharedElementName.getTransitionName() );

            Pair<View, String> sharedImagePair = Pair.create(sharedImage, sharedImage.getTransitionName());
            Pair<View, String> sharedNamePair = Pair.create(sharedElementName, sharedElementName.getTransitionName());

            final ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(this, sharedImagePair, sharedNamePair);

            startActivity(intent, options.toBundle());
        }
        else{
            //TODO implement the fragment update for bigger screens
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

    /*
    FAB listener set in XML layout
     */
    public void fabListener(View v){
        Intent intent = new Intent(this, NewToDoTaskActivity.class);


        ActivityOptions options = ActivityOptions.
                makeSceneTransitionAnimation(this, mFab, mFab.getTransitionName());
        startActivity(intent, options.toBundle());;
    }
}
