package dima.it.polimi.blackboard.activities;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;


import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Filter;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.adapters.FirestoreAdapter;
import dima.it.polimi.blackboard.exceptions.AlreadyRemovedException;
import dima.it.polimi.blackboard.fragments.TodoItemDetailFragment;
import dima.it.polimi.blackboard.fragments.TodoItemListFragment;
import dima.it.polimi.blackboard.model.TodoItem;
import dima.it.polimi.blackboard.model.User;
import dima.it.polimi.blackboard.utils.HouseDecoder;
import dima.it.polimi.blackboard.utils.UserDecoder;

/**
 * This class represents an activity which shows one fragment on smaller devices,
 * and two fragments on larger ones. On smaller devices, the second fragment is displayed
 * by another activity
 * Created by Stefano on 07/01/2018.
 */

public abstract class DoubleFragmentActivity extends AppCompatActivity
        implements TodoItemListFragment.OnListFragmentInteractionListener,
            TodoItemDetailFragment.OnTodoItemDetailInteraction,
            DialogInterface.OnClickListener,
        FirestoreAdapter.OnCompleteListener, Filter.FilterListener{

    private static final String TAG = "double_frag_activity";
    private static final String CURRENT_HOUSE_INDEX = "current-house-index";
    private static final String CURRENT_ITEM_INDEX = "current-item-index";
    private static final String CURRENT_ITEM = "current-item";
    private static final String CURRENT_SEARCH_QUERY = "current-search-query";
    private static final int ACCEPT_TASK_REQUEST = 1;
    private static final int ANIM_DURATION = 250;

    protected TodoItemListFragment firstFragment;
    private TodoItemDetailFragment secondFragment;

    private View itemRowClicked;
    private int clickedPosition;
    private TodoItem clickedItem;

    private List<TodoItem> itemList;
    private boolean isDouble;
    private boolean isActivityResult;
    private boolean isEmpty;
    private boolean wasEmpty;

    private CharSequence searchQuery;
    private SearchView searchView;

    protected int whichHouse = 0;
    protected CharSequence[] houses;
    protected CharSequence[] housesDecoded;
    protected boolean houseDownloadComplete;

    protected FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isDouble = isDouble();
        getHouses();
        if(savedInstanceState != null){
            whichHouse = savedInstanceState.getInt(CURRENT_HOUSE_INDEX);
            clickedPosition = savedInstanceState.getInt(CURRENT_ITEM_INDEX);
            firstFragment = (TodoItemListFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_list_container);
            firstFragment.setAuthId(User.getInstance().getAuth_id());
            searchQuery = savedInstanceState.getCharSequence(CURRENT_SEARCH_QUERY);
            //firstFragment.setHouse((String)houses[whichHouse]);

            if(isDouble){
                clickedItem = savedInstanceState.getParcelable(CURRENT_ITEM);
                secondFragment = (TodoItemDetailFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_detail_container);
                if(!isEmpty) {
                    doubleFragmentClickHandler(itemRowClicked, clickedItem, clickedPosition);
                }
            }
        }
        else{
            setDefaultHouse();
            instantiateFirstFragment();
            firstFragment.setAuthId(User.getInstance().getAuth_id());
            showFirstFragment();

        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(CURRENT_HOUSE_INDEX, whichHouse);
        savedInstanceState.putInt(CURRENT_ITEM_INDEX, clickedPosition);
        savedInstanceState.putParcelable(CURRENT_ITEM, clickedItem);
        savedInstanceState.putCharSequence(CURRENT_SEARCH_QUERY, searchView.getQuery());
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Adding back navigation on toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getMenuInflater().inflate(R.menu.menu_house_list, menu);

        // Implementing the search functionality
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView)menu.findItem(R.id.action_search)
                .getActionView();
        if(searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        searchView.setMaxWidth(Integer.MAX_VALUE);

        firstFragment.setSearchView(searchView);

        if(searchQuery != null && !TextUtils.isEmpty(searchQuery)/*!searchQuery.equals("")*/) {
            searchView.setQuery(searchQuery, true);
            searchView.setIconified(false);
        }

        super.onCreateOptionsMenu(menu);
        return true;
    }

    private void setDefaultHouse(){
        // TODO: 15/03/2018 get the index from the Settings
        whichHouse = 0;
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
        return result;
    }

    private void showFirstFragment(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_list_container, firstFragment)
                .commit();
        getFragmentManager().executePendingTransactions();
    }

    private void instantiateFirstFragment(){
        firstFragment = TodoItemListFragment.newInstance(1, isDouble(), (String)houses[whichHouse]);
    }

    private void instantiateSecondFragment(){
        secondFragment = TodoItemDetailFragment.newInstance();
    }

    /**
     * Method called at the end of update from Firestore
     * @param isEmpty represents the fact whether the item list is empty or not
     */
    @Override
    public void onComplete(boolean isEmpty) {
        //Check if the second fragment has been instantiated
        if(isDouble){
            if(secondFragment == null){
                Log.d(TAG, "Instantiating second fragment");
                // instantiate it
                instantiateSecondFragment();
                // update its state with the first item in the list
                TodoItem firstItem = firstFragment.getItem(0);
                secondFragment.updateFragment(firstItem, 0, false);
                // show it
                showSecondFragment();
            }
        }
        if(isEmpty){
            // If the list of items is empty, show a message
            Log.d(TAG, "Showing empty message");
            firstFragment.emptyFragment();
            if(isDouble){
                wasEmpty = true;
                secondFragment.emptyFragment();
            }
        }
        else{
            Log.d(TAG, "Showing the content");
            if(wasEmpty) {
                // Show the correct content
                firstFragment.fillFragment();
                if (isDouble) {
                    if (wasEmpty) {
                        wasEmpty = false;
                        // If it's the first item in the list
                        secondFragment.updateFragment(firstFragment.getItem(0), 0, false);
                    }
                    secondFragment.fillFragment();
                }
            }
        }
    }

    @Override
    public void onFilterComplete(int i) {
        if(isDouble){
            if(firstFragment.getRemainingItems() == 0){
                // The search didn't match any item
                secondFragment.emptyFragment();
                return;
            }
            TodoItem firstItem = firstFragment.getItem(0);
            //View rowToSelect = firstFragment.getViewHolder(0);
            //doubleFragmentClickHandler(rowToSelect, firstItem, 0);

            firstFragment.setSelectedItem(0);
            secondFragment.updateFragment(firstItem, 0, false);
            //Cannot get the correct one due to filtering conflict
            //Will satisfy the condition in doubleFragmentClickHandler
            itemRowClicked = null;
        }
    }

    private void showSecondFragment(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_detail_container, secondFragment)
                .commit();
    }

    @Override
    public void onItemClick(TodoItem item, View view, int clickedPosition) {
        this.clickedPosition = clickedPosition;
        this.clickedItem = item;
        if(isDouble){
            doubleFragmentClickHandler(view, item, clickedPosition);
        } else {
            // Disable swipe while the animation is going
            itemRowClicked = view;
            firstFragment.disableSwipe();
            isActivityResult = false;
            // Animate the view clicked
            view.animate()
                    .scaleX(1.06f)
                    .scaleY(1.06f)
                    .translationZ(8f)
                    .setDuration(ANIM_DURATION)
                    .withEndAction(()->singleFragmentClickHandler(item, view, clickedPosition))
                    .start();
        }
    }

    /**
     * Animates the view of a row when returning from the detail page
     * @param itemRow the view to animate
     */
    private void resize(View itemRow){
        itemRow.animate()
                .scaleX(1f)
                .scaleY(1f)
                .translationZ(0f)
                .setDuration(ANIM_DURATION)
                .start();
    }


    @Override
    protected void onResume() {
        firstFragment = (TodoItemListFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_list_container);
        firstFragment.enableSwipe();
        if(!isActivityResult)
            if(itemRowClicked != null) {
                if (!isDouble) {
                    resize(itemRowClicked);
                }
            }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    /**
     * Starts another activity for result in case of single fragment activity
     * @param item the item clicked
     * @param view the view containing the clicked item
     * @param clickedPosition the position of the item clicked
     */
    @SuppressLint("RestrictedApi")
    @SuppressWarnings("unchecked")
    private void singleFragmentClickHandler(TodoItem item, View view, int clickedPosition){

        // Preparing the transition elements
        View sharedImage = view.findViewById(R.id.user_icon);
        View sharedElementName = view.findViewById(R.id.item_name);
        final Intent intent = new Intent(this, DetailTodoItemActivity.class);
        intent.putExtra(getResources().getString(R.string.todo_item), item);
        intent.putExtra(getResources().getString(R.string.icon_tr_name), sharedImage.getTransitionName());
        intent.putExtra(getResources().getString(R.string.name_tr_name),sharedElementName.getTransitionName() );
        intent.putExtra(getResources().getString(R.string.position), clickedPosition);

        Pair<View, String>[] array = new Pair[]{
                Pair.create(sharedImage, sharedImage.getTransitionName()),
                Pair.create(sharedElementName, sharedElementName.getTransitionName())
        };

        final ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(this, array);

        startActivityForResult(intent, ACCEPT_TASK_REQUEST, options.toBundle());
    }



    /**
     * Updates the second fragment if shown in the same activity
     * @param item the item selected
     */
    private void doubleFragmentClickHandler(View clickedView, TodoItem item, int position){
        if(clickedView != itemRowClicked) {
            //setFocusedItem(clickedView);
            firstFragment.setSelectedItem(position);
            secondFragment.updateFragment(item, position, false);
        }
        itemRowClicked = clickedView;
    }

    /**
     * Sets a visual flag for the item currently selected in a large device
     * @param view the view currently selected
     */
    private void setFocusedItem(View view){
        Drawable selected = getDrawable(R.color.colorAccent);
        view.findViewById(R.id.selected_flag).setBackground(selected);
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
        /*TODO pass also the item, try to remove it from the list and catch the exception at line 359
        // if there was an exception, it had been taken by someone in the meantime
        */
        // Check which request we're responding to
        if (requestCode == ACCEPT_TASK_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                isActivityResult = true;
                final int position = data.getIntExtra(getResources().getString(R.string.position),0);
                final String action = data.getStringExtra(DetailTodoItemActivity.RES_ACTION);
                final TodoItem shownItem= data.getParcelableExtra(DetailTodoItemActivity.SHOWN_ITEM);
                itemRowClicked.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .translationZ(0f)
                        .setDuration(ANIM_DURATION)
                        .withEndAction( () -> removeItem(shownItem, position, action))
                        .start();
            }
        }
    }

    @Override
    public void onAcceptClick(TodoItem todoItem, int position, String action) {
        removeItem(todoItem, position, action);
    }

    /**
     * When an item is removed from the list, shows a Snackbar giving the opportunity to UNDO
     * the action
     * @param position position of the item removed
     */
    protected abstract void showUndoMessage(TodoItem removedItem, int position, String action);

    /**
     * Removes an item at a given position and sets the environment for showing next one
     * @param position the position to remove
     */
    void removeItem(TodoItem shownItem, int position, String action){
        View nextItemView = firstFragment.getViewHolder(position + 1);
        try{
            firstFragment.contains(shownItem);
            TodoItem removedItem = firstFragment.removeItem(position);
            showUndoMessage(removedItem, position, action);
            if(isDouble && checkSelected(position)) {
                if(firstFragment.getRemainingItems() == 0){
                    //We're removing the last item
                    secondFragment.emptyFragment();
                    isEmpty = true;
                }
                else {
                    isEmpty = false;
                    TodoItem nextItem = firstFragment.getItem(position);
                    if (nextItem == null) {
                        //It was the last of the list
                        nextItem = firstFragment.getItem(position - 1);
                        secondFragment.updateFragment(nextItem, position - 1, false);
                        firstFragment.setSelectedItem(position - 1);
                    } else {
                        secondFragment.updateFragment(nextItem, position, false);
                        firstFragment.setSelectedItem(position + 1);
                    }
                    itemRowClicked = nextItemView;
                }
            }
        }
        catch (AlreadyRemovedException e){
            Log.e(TAG, e.getMessage());
            showErrorToast(shownItem);
        }

    }

    /**
     * Removes an item in a double fragment environment.
     * Selects also the next one to show
     * @param position position of removed item
     * @param action action to take with removed item
     */
    public void removeItem(int position, String action){
        TodoItem removedItem = firstFragment.removeItem(position);
        showUndoMessage(removedItem, position, action);
        if(firstFragment.getRemainingItems() == 0){
            //We're removing the last item
            secondFragment.emptyFragment();
            isEmpty = true;
        }
        else {
            //We have to determine which will be the next item shown
            isEmpty = false;
            View nextItemView = firstFragment.getViewHolder(position + 1);
            if (isDouble && checkSelected(position)) {
                TodoItem nextItem = firstFragment.getItem(position);
                if (nextItem == null) {
                    //It was the last of the list
                    nextItem = firstFragment.getItem(position - 1);
                    secondFragment.updateFragment(nextItem, position - 1, false);
                    firstFragment.setSelectedItem(position - 1);
                } else {
                    secondFragment.updateFragment(nextItem, position, false);
                    firstFragment.setSelectedItem(position + 1);
                }
                itemRowClicked = nextItemView;
            }
        }
    }

    /**
     * Checks if the item removed is the one currently selected in large devices
     * @param removedPosition the position of the item removed
     * @return true if the removed position is the one currently shown
     */
    private boolean checkSelected(int removedPosition){
        return removedPosition == secondFragment.getPosition();
    }

    /**
     * Inserts an item at a given position
     * @param item the item to insert
     * @param position the position where to insert it
     */
    void insertItem(TodoItem item, int position){
        int remainingItems = firstFragment.getRemainingItems();
        firstFragment.insertItem(item, position);
        if(isDouble && checkSelected(position)){
            if(remainingItems == 0){
                //I'm inserting in empty list
                //Could be during search
                secondFragment.updateFragment(item, position, true);
                firstFragment.reSelectLastOne();
            }
            //secondFragment.updateFragment(item, position);
            //firstFragment.setSelectedItem(position);
        }
    }

    /**
     * Handles the item's final acceptance after the Snackbar message
     * @param removedItem the item that has been removed
     */
    void handleItemAccepted(TodoItem removedItem, int position, String action){
        callNetwork(removedItem, action);
    }

    protected abstract void callNetwork(TodoItem changedItem, String action);

    protected abstract void showErrorToast(TodoItem item);


    /**
     * This method return the chosen house from the list
     * @param dialog the dialog sending data
     * @param which the house chosen
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {
        this.secondFragment = null;
        this.whichHouse = which;
        String currentHouse = (String)this.houses[whichHouse];
        firstFragment.setHouse(currentHouse);
        dialog.dismiss();

    }

    @SuppressWarnings("unchecked")
    private void getHouses(){
        User user = User.getInstance();
        List<Object> houses = user.getHouses();
        this.houses = new CharSequence[houses.size()];
        this.housesDecoded = new CharSequence[houses.size()];
        for(int i=0; i<houses.size(); i++){
            this.houses[i] = (String)houses.get(i);
            UserDecoder.getInstance().populateFromHouse((String)this.houses[i]);
            this.housesDecoded[i] = HouseDecoder.getInstance().decodeId((String)this.houses[i]);
        }
        houseDownloadComplete = true;
    }

    public void onChooseHouse(MenuItem menuItem){
        HouseListActivity.ChooseHouseDialog.mListener = this;
        DialogFragment houseListDialog = DoubleFragmentActivity.ChooseHouseDialog.newInstance(whichHouse, housesDecoded);
        houseListDialog.show(getFragmentManager(), "dialog");
    }

    public static class ChooseHouseDialog extends DialogFragment {
        public static Dialog.OnClickListener mListener;

        public static HouseListActivity.ChooseHouseDialog newInstance(int whichHouse, CharSequence[] houses) {
            Bundle args = new Bundle();
            args.putInt("chosen_house", whichHouse);
            args.putCharSequenceArray("houses", houses);
            HouseListActivity.ChooseHouseDialog fragment = new HouseListActivity.ChooseHouseDialog();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public Dialog onCreateDialog(final Bundle savedInstanceState) {
            int which = getArguments().getInt("chosen_house");
            CharSequence[] houses = getArguments().getCharSequenceArray("houses");
            final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle(R.string.action_choose_house);
            dialog.setSingleChoiceItems(houses, which, mListener);
            return dialog.create();
        }
    }
}
