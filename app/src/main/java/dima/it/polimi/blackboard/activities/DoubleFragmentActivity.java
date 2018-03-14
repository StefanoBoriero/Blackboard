package dima.it.polimi.blackboard.activities;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.fragments.TodoItemDetailFragment;
import dima.it.polimi.blackboard.fragments.TodoItemListFragment;
import dima.it.polimi.blackboard.model.TodoItem;
import dima.it.polimi.blackboard.model.User;
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
            DialogInterface.OnClickListener{

    private static final String TAG = "double_frag_activity";
    private static final String CURRENT_HOUSE_INDEX = "current-house-index";
    private static final int ACCEPT_TASK_REQUEST = 1;
    private static final int ANIM_DURATION = 250;

    protected Fragment firstFragment;
    private Fragment secondFragment;

    private View itemRowClicked;

    private List<TodoItem> itemList;
    private boolean isDouble;
    private boolean isActivityResult;

    protected int whichHouse = 0;
    protected CharSequence[] houses;
    protected boolean houseDownloadComplete;

    protected FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isDouble = isDouble();

        if(savedInstanceState != null){
            whichHouse = savedInstanceState.getInt(CURRENT_HOUSE_INDEX);
        }
        else{
            whichHouse = 0;
        }

        getHouses();
        showFirstFragment();
        /*
        if(isDouble()){
            showSecondFragment();
        }
        */

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(CURRENT_HOUSE_INDEX, whichHouse);
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
        SearchView searchView = (SearchView)menu.findItem(R.id.action_search)
                .getActionView();
        if(searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        searchView.setMaxWidth(Integer.MAX_VALUE);
        ((TodoItemListFragment)firstFragment).setSearchView(searchView);

        super.onCreateOptionsMenu(menu);
        return true;
    }

    void setItemList(List<TodoItem> items){
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
        return result;
    }

    private void showFirstFragment(){
        firstFragment = TodoItemListFragment.newInstance(1, itemList, (String)houses[whichHouse]);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_list_container, firstFragment)
                .commit();
        getFragmentManager().executePendingTransactions();
    }

    @Override
    public void onDownloadComplete(TodoItem item){
        if(isDouble()){
            if(secondFragment == null) {
                showSecondFragment(item);
            }
        }
    }

    private void showSecondFragment(TodoItem item){
        secondFragment = TodoItemDetailFragment.newInstance(itemList.get(0), 0);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_detail_container, secondFragment)
                .commit();
    }

    @Override
    public void onItemClick(TodoItem item, View view, int clickedPosition) {
        if(isDouble){
            doubleFragmentClickHandler(view, item, clickedPosition);
        } else {
            // Disable swipe while the animation is going
            ((TodoItemListFragment)firstFragment).disableSwipe();
            itemRowClicked = view;
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
        ((TodoItemListFragment)firstFragment).enableSwipe();
        if(!isActivityResult)
            if(itemRowClicked != null) {
                if (!isDouble) {
                    resize(itemRowClicked);
                }
            }
        super.onResume();
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
            setFocusedItem(clickedView);
            if(itemRowClicked != null) {
                itemRowClicked.findViewById(R.id.selected_flag).setBackground(null);
            }
            ((TodoItemDetailFragment)secondFragment).updateFragment(item, position);
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
        // Check which request we're responding to
        if (requestCode == ACCEPT_TASK_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                isActivityResult = true;
                final int position = data.getIntExtra(getResources().getString(R.string.position),0);
                final String action = data.getStringExtra(DetailTodoItemActivity.RES_ACTION);
                itemRowClicked.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .translationZ(0f)
                        .setDuration(ANIM_DURATION)
                        .withEndAction( () -> removeItem(position, action))
                        .start();
            }
        }
    }

    @Override
    public void onAcceptClick(TodoItem todoItem, int position, String action) {
        removeItem(position, action);
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
    void removeItem(int position, String action){
        View nextItemView = ((TodoItemListFragment)firstFragment).getViewHolder(position + 1);
        TodoItem removedItem = ((TodoItemListFragment)firstFragment).removeItem(position);
        showUndoMessage(removedItem, position, action);
        if(isDouble && checkSelected(position)){
            TodoItem nextItem = ((TodoItemListFragment)firstFragment).getItem(position);
            setFocusedItem(nextItemView);
            itemRowClicked = nextItemView;
            ((TodoItemDetailFragment)secondFragment).updateFragment(nextItem, position);
        }
    }

    /**
     * Checks if the item removed is the one currently selected in large devices
     * @param removedPosition the position of the item removed
     * @return true if the removed position is the one currently shown
     */
    private boolean checkSelected(int removedPosition){
        return removedPosition == ((TodoItemDetailFragment)secondFragment).getPosition();
    }

    /**
     * Inserts an item at a given position
     * @param item the item to insert
     * @param position the position where to insert it
     */
    void insertItem(TodoItem item, int position){
        ((TodoItemListFragment)firstFragment).insertItem(item, position);
        if(isDouble && checkSelected(position)){
            ((TodoItemDetailFragment)secondFragment).updateFragment(item, position);
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


    /**
     * This method return the chosen house from the list
     * @param dialog the dialog sending data
     * @param which the house chosen
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {
        this.whichHouse = which;
        String currentHouse = (String)this.houses[whichHouse];
        ((TodoItemListFragment)firstFragment).changeHouse(currentHouse);
        dialog.dismiss();

    }

    @SuppressWarnings("unchecked")
    private void getHouses(){
        User user = User.getInstance();
        List<Object> houses = user.getHouses();
        this.houses = new CharSequence[houses.size()];
        for(int i=0; i<houses.size(); i++){
            this.houses[i] = (String)houses.get(i);
            UserDecoder.getInstance().populateFromHouse((String)this.houses[i]);
        }
        houseDownloadComplete = true;
    }

    public void onChooseHouse(MenuItem menuItem){
        HouseListActivity.ChooseHouseDialog.mListener = this;
        DialogFragment houseListDialog = DoubleFragmentActivity.ChooseHouseDialog.newInstance(whichHouse, houses);
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
