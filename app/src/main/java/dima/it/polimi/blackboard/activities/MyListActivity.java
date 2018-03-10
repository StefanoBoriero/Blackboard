package dima.it.polimi.blackboard.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.fragments.TodoItemListFragment;
import dima.it.polimi.blackboard.model.TodoItem;
import dima.it.polimi.blackboard.utils.DataGeneratorUtil;

/**
 * This class shows the list of activities a user has taken charge of.
 * Created by Stefano on 27/12/2017.
 */

public class MyListActivity extends DoubleFragmentActivity implements DialogInterface.OnClickListener{
    private static String TAG = "my_list_activity";
    private FirebaseFirestore db;
    private int whichHouse = 0;
    private CharSequence[] houses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_my_list);

        super.setItemList(DataGeneratorUtil.generateTodoItems(30));
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_my_list);
        setSupportActionBar(toolbar);




        ((TodoItemListFragment)firstFragment).setHouse("Sexy");
        ((TodoItemListFragment)firstFragment).setMyList(true);

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
        String itemId = removedItem.getId();

        DocumentReference completedDoc = db.collection("houses").document("Sexy").collection("items")
                .document(itemId);

        completedDoc.update(
                "completed", true
        );
    }

    @Override
    public void onItemSwipe(int swipedPosition) {
        super.removeItem(swipedPosition);
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
                    //houseDownloadComplete = true;
                }

            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    public void onChooseHouse(MenuItem menuItem){
        HouseListActivity.ChooseHouseDialog.mListener = this;
        DialogFragment houseListDialog = HouseListActivity.ChooseHouseDialog.newInstance(whichHouse, houses);
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
