package dima.it.polimi.blackboard.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.View;

import com.google.firebase.firestore.DocumentReference;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.fragments.TodoItemDetailFragment;
import dima.it.polimi.blackboard.fragments.TodoItemListFragment;
import dima.it.polimi.blackboard.model.TodoItem;
import dima.it.polimi.blackboard.utils.DataGeneratorUtil;

/**
 * This class shows the list of activities a user has taken charge of.
 * Created by Stefano on 27/12/2017.
 */

public class MyListActivity extends DoubleFragmentActivity implements DialogInterface.OnClickListener{
    private static String TAG = "my_list_activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_my_list);

        super.setItemList(DataGeneratorUtil.generateTodoItems(30));
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_my_list);
        setSupportActionBar(toolbar);

        ((TodoItemListFragment)firstFragment).setMyList(true);

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
        View view = findViewById(android.R.id.content);
        Snackbar.make(view, "You completed the activity!",
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
            case TodoItemDetailFragment.ACTION_TAKEN: completeItem(itemChanged);
                break;
            case TodoItemDetailFragment.ACTION_DELETED: refuseItem(itemChanged);
                break;
        }

    }

    private void completeItem(TodoItem completedItem){
        String itemId = completedItem.getId();
        String house = houses[whichHouse].toString();

        DocumentReference completedDoc = db.collection("houses").document(house).collection("items")
                .document(itemId);

        completedDoc.update(
                "completed", true
        );
    }

    private void refuseItem(TodoItem refusedItem){
        String itemId = refusedItem.getId();
        String house = houses[whichHouse].toString();

        DocumentReference refusedDoc = db.collection("houses").document(house).collection("items")
                .document(itemId);
        refusedDoc.update(
                "taken", false,
                "takenBy", null
        );
    }

    @Override
    public void onItemSwipe(int swipedPosition, int direction) {
        if(direction == ItemTouchHelper.LEFT) {
            super.removeItem(swipedPosition, TodoItemDetailFragment.ACTION_TAKEN);
        }
        else if(direction == ItemTouchHelper.RIGHT){
            super.removeItem(swipedPosition, TodoItemDetailFragment.ACTION_DELETED);
        }
    }

}
