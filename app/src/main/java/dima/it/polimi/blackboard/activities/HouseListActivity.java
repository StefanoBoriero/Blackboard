package dima.it.polimi.blackboard.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;

import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;

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
public class HouseListActivity extends DoubleFragmentActivity{
    private final static String TAG = "HOUSE_LIST";
    private final static String ARG_HOUSE = "house";

    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_house_list);

        //super.setItemList(DataGeneratorUtil.generateTodoItems(30));
        super.onCreate(savedInstanceState);

        mFab = findViewById(R.id.add_fab);
        mFab.setTransitionName("revealCircular");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_house_list);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_house_list, menu);

        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    protected void showUndoMessage(TodoItem removedItem, int position, String action) {
        Snackbar.make(mFab, "You took charge of the activity",
                Snackbar.LENGTH_LONG)
                .setAction("UNDO", (v) ->
                        super.insertItem(removedItem, position)
                )
                .addCallback(new Snackbar.Callback(){
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        if(event == DISMISS_EVENT_TIMEOUT) {
                            handleItemAccepted(removedItem, position, action);
                        }
                    }
                })
                .show();
    }

    @Override
    protected void callNetwork(TodoItem itemChanged, String action) {
        switch(action){
            case TodoItemDetailFragment.ACTION_TAKEN: acceptItem(itemChanged);
                break;
            case TodoItemDetailFragment.ACTION_DELETED: deleteItem(itemChanged);
                break;
        }


    }

    private void acceptItem(TodoItem itemAccepted){
        String id = itemAccepted.getId();
        String house = (String)houses[whichHouse];
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            String userId = user.getUid();
            DocumentReference item = db.collection("houses").document(house).collection("items")
                    .document(id);
            item.update(
                    "taken", true,
                    "takenBy", userId
            );
        }
    }

    private void deleteItem(TodoItem itemDeleted){
        String id = itemDeleted.getId();
        String house = (String)houses[whichHouse];
        DocumentReference item = db.collection("houses").document(house).collection("items")
                .document(id);
        item.delete();

    }


    @Override
    public void onItemSwipe( int swipedPosition, int direction) {
        if(direction == ItemTouchHelper.LEFT) {
            super.removeItem(swipedPosition, TodoItemDetailFragment.ACTION_TAKEN);
        }
        else{
            super.removeItem(swipedPosition, TodoItemDetailFragment.ACTION_DELETED);
        }
    }

    /*
    FAB listener set in XML layout
     */
    public void fabListener(View v){
        if(houseDownloadComplete) {
            Intent intent = new Intent(this, NewToDoTaskActivity.class);
            intent.putExtra(ARG_HOUSE, houses[whichHouse]);

            ActivityOptions options = ActivityOptions.
                    makeSceneTransitionAnimation(this, mFab, mFab.getTransitionName());
            startActivity(intent, options.toBundle());
        }
        else{
            Toast.makeText(this, "Error connecting to network, try again", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onBackPressed() {
        ((TodoItemListFragment)firstFragment).stopListening();
        super.onBackPressed();
    }
}
