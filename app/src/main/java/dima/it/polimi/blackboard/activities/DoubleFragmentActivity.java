package dima.it.polimi.blackboard.activities;


import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;


import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.View;

import java.util.List;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.fragments.TodoItemDetailFragment;
import dima.it.polimi.blackboard.fragments.TodoItemListFragment;
import dima.it.polimi.blackboard.model.TodoItem;

/**
 * This class represents an activity which shows one fragment on smaller devices,
 * and two fragments on larger ones. On smaller devices, the second fragment is displayed
 * by another activity
 * Created by Stefano on 07/01/2018.
 */

public abstract class DoubleFragmentActivity extends AppCompatActivity
        implements TodoItemListFragment.OnListFragmentInteractionListener,
            TodoItemDetailFragment.OnTodoItemDetailInteraction{

    private static final int ACCEPT_TASK_REQUEST = 1;

    protected Fragment firstFragment;
    protected Fragment secondFragment;

    protected List<TodoItem> itemList;
    boolean isDouble;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isDouble = isDouble();

        showFirstFragment();
        if(isDouble()){
            showSecondFragment();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Adding back navigation on toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Implementing the search functionality
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView)menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        ((TodoItemListFragment)firstFragment).setSearchView(searchView);

        super.onCreateOptionsMenu(menu);
        return true;
    }

    public void setItemList(List<TodoItem> items){
        itemList = items;
    }

    /**
     * This method checks if the second fragment is displayed in the activity, and sets all the
     * flags accordingly
     * @return true if the second fragment is shown, false otherwise
     */
    private boolean isDouble(){
        View secondFragmentContainer = findViewById(R.id.fragment_detail_container);
        boolean result = false;
        if(secondFragmentContainer != null){
            result = true;
        }
        return  result;
    }

    protected void showFirstFragment(){
        firstFragment = TodoItemListFragment.newInstance(1, itemList);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_list_container, firstFragment)
                .commit();
        getFragmentManager().executePendingTransactions();
    }

    private void showSecondFragment(){
        secondFragment = TodoItemDetailFragment.newInstance(itemList.get(0));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_detail_container, secondFragment)
                .commit();
    }

    @Override
    public void onItemClick(TodoItem item, View view, int clickedPosition) {
        if(isDouble){
            doubleFragmentClickHandler(item);
        } else {
            singleFragmentClickHandler(item, view, clickedPosition);
        }
    }

    /**
     * Starts another activity for result in case of single fragment activity
     * @param item the item clicked
     * @param view the view containing the clicked item
     * @param clickedPosition the position of the item clicked
     */
    @SuppressLint("RestrictedApi")
    private void singleFragmentClickHandler(TodoItem item, View view, int clickedPosition){
        View sharedImage = view.findViewById(R.id.user_icon);
        View sharedElementName = view.findViewById(R.id.item_name);
        final Intent intent = new Intent(this, DetailTodoItemActivity.class);
        intent.putExtra(getResources().getString(R.string.todo_item), item);
        intent.putExtra(getResources().getString(R.string.icon_tr_name), sharedImage.getTransitionName());
        intent.putExtra(getResources().getString(R.string.name_tr_name),sharedElementName.getTransitionName() );
        intent.putExtra(getResources().getString(R.string.position), clickedPosition);

        Pair<View, String> sharedImagePair = Pair.create(sharedImage, sharedImage.getTransitionName());
        Pair<View, String> sharedNamePair = Pair.create(sharedElementName, sharedElementName.getTransitionName());

        final ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(this, sharedImagePair, sharedNamePair);

        startActivityForResult(intent, ACCEPT_TASK_REQUEST, options.toBundle());
    }

    /**
     * Updates the second fragment if shown in the same activity
     * @param item the item selected
     */
    private void doubleFragmentClickHandler(TodoItem item){
        ((TodoItemDetailFragment)secondFragment).updateFragment(item);
    }

    /**
     * When returning from the detail activity, checks what has been decided:
     * if the item has been accepted, removes it from the list and shows a message
     * @param requestCode code representing the request performed
     * @param resultCode code representing the decision
     * @param data additional info on the decision
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == ACCEPT_TASK_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                removeItem(data.getIntExtra(getResources().getString(R.string.position),0));
            }
        }
    }

    @Override
    public void onAcceptClick(TodoItem todoItem) {

    }

    /**
     * When an item is removed from the list, shows a Snackbar giving the opportunity to UNDO
     * the action
     * @param position position of the item removed
     */
    protected abstract void showUndoMessage(TodoItem removedItem, int position);

    protected void removeItem(int position){
        TodoItem removedItem = ((TodoItemListFragment)firstFragment).removeItem(position);
        showUndoMessage(removedItem, position);
        if(isDouble){
            TodoItem nextItem = ((TodoItemListFragment)firstFragment).getItem(position);
            ((TodoItemDetailFragment)secondFragment).updateFragment(nextItem);
        }
    }

    protected void insertItem(TodoItem item, int position){
        ((TodoItemListFragment)firstFragment).insertItem(item, position);
        if(isDouble){
            ((TodoItemDetailFragment)secondFragment).updateFragment(item);
        }
    }

    /**
     * Handles the item's final acceptance after the Snackbar message
     * @param removedItem the item that has been removed
     */
    protected void handleItemAccepted(TodoItem removedItem, int position){
        callNetwork(removedItem);
    }

    protected abstract void callNetwork(TodoItem removedItem);

}
