package dima.it.polimi.blackboard.activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.model.TodoItem;
import dima.it.polimi.blackboard.utils.DataGeneratorUtil;

/**
 * This class shows the list of activities a user has taken charge of.
 * Created by Stefano on 27/12/2017.
 */

public class MyListActivity extends DoubleFragmentActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_my_list);

        super.setItemList(DataGeneratorUtil.generateTodoItems(30));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_list, menu);

        super.onCreateOptionsMenu(menu);

        return true;
    }

    @Override
    protected void showUndoMessage(TodoItem removedItem, int position) {
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
                            handleItemAccepted(removedItem, position);
                        }
                    }
                })
                .show();
    }

    @Override
    protected void callNetwork(TodoItem removedItem) {
        //TODO update firebase
    }

    @Override
    public void onItemSwipe(int swipedPosition) {
        super.removeItem(swipedPosition);
    }
}
