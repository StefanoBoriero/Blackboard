package dima.it.polimi.blackboard.activities;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.fragments.TodoItemListFragment;
import dima.it.polimi.blackboard.model.TodoItem;
import dima.it.polimi.blackboard.utils.DataGeneratorUtil;

/**
 * This activity presents the list of todo_items that have been added to the group but nobody has
 * taken in charge. It displays at first a fragment with the list in a RecyclerView, and on click of
 * one item displays the details in another fragment; if the device is large enough, the detail
 * fragment will be displayed permanently on the side of the screen
 */
public class HouseListActivity extends DoubleFragmentActivity implements DialogInterface.OnClickListener{

    // TODO select preferred
    private int whichHouse = 0;
    private final static String TAG = "HOUSE_LIST";
    private CharSequence[] houses;
    private FirebaseFirestore db;

    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_house_list);

        super.setItemList(DataGeneratorUtil.generateTodoItems(30));
        super.onCreate(savedInstanceState);

        mFab = findViewById(R.id.add_fab);
        mFab.setTransitionName("revealCircular");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_house_list);
        setSupportActionBar(toolbar);

        ((TodoItemListFragment)firstFragment).setHouse("Sexy");
        ((TodoItemListFragment)firstFragment).setMyList(false);

        db = FirebaseFirestore.getInstance();
        getHouses();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_house_list, menu);

        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    protected void showUndoMessage(TodoItem removedItem, int position) {
        Snackbar.make(mFab, "You took charge of the activity",
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

    @SuppressWarnings("unchecked")
    private void getHouses(){
        DocumentReference user = db.collection("users").document("Serento");
        user.get().addOnCompleteListener((task) -> {
            if (task.isSuccessful()) {
               {
                   DocumentSnapshot document = task.getResult();
                   Map<String, Object> userParam = document.getData();
                   ArrayList<String> houses = (ArrayList<String>)userParam.get("houses");
                   Log.d(TAG, document.getId() + " => " + document.getData());
                   this.houses = new CharSequence[houses.size()];
                   for(int i=0; i<houses.size(); i++){
                       this.houses[i] = houses.get(i);
                   }
                }

            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }


    @Override
    public void onItemSwipe( int swipedPosition) {
        super.removeItem(swipedPosition);
    }


    public void onChooseHouse(MenuItem menuItem){
        ChooseHouseDialog.mListener = this;
        DialogFragment houseListDialog = ChooseHouseDialog.newInstance(whichHouse, houses);
        houseListDialog.show(getFragmentManager(), "dialog");
    }

    /*
    FAB listener set in XML layout
     */
    public void fabListener(View v){
        Intent intent = new Intent(this, NewToDoTaskActivity.class);


        ActivityOptions options = ActivityOptions.
                makeSceneTransitionAnimation(this, mFab, mFab.getTransitionName());
        startActivity(intent, options.toBundle());
    }

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

    public static class ChooseHouseDialog extends DialogFragment{
        public static Dialog.OnClickListener mListener;

        public static ChooseHouseDialog newInstance(int whichHouse, CharSequence[] houses) {
            Bundle args = new Bundle();
            args.putInt("chosen_house", whichHouse);
            args.putCharSequenceArray("houses", houses);
            ChooseHouseDialog fragment = new ChooseHouseDialog();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public Dialog onCreateDialog(final Bundle savedInstanceState) {
            int which = getArguments().getInt("chosen_house");
            CharSequence[] houses = getArguments().getCharSequenceArray("houses");
            final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle(R.string.action_choose_house);
            //CharSequence[] entries = new CharSequence[]{"One", "Two", "Three"};
            dialog.setSingleChoiceItems(houses, which, mListener);
            return dialog.create();
        }
    }
}
