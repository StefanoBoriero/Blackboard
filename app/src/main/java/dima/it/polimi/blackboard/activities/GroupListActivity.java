package dima.it.polimi.blackboard.activities;


import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
TodoItemDetailFragment.OnTodoItemDetailInteraction, DialogInterface.OnClickListener{

    private static final int ACCEPT_TASK_REQUEST = 1;

    // TODO select preferred
    private int whichHouse = 0;

    private TodoItemDetailFragment detailFragment;
    private TodoItemListFragment listFragment;


    private FloatingActionButton mFab;

    //TODO remove this attribute
    List<TodoItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);


        mFab = findViewById(R.id.add_fab);
        mFab.setTransitionName("revealCircular");
        displayListFragment();
        View detailContainer = findViewById(R.id.fragment_detail);
        if(detailContainer != null){
            displayDetailFragment();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    private void displayListFragment(){
        //Todo remove this call
        items = DataGeneratorUtil.generateTodoItems(30);
        listFragment = TodoItemListFragment.newInstance(1, items);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_list_container, listFragment)
                .commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    private void displayDetailFragment(){
        //TODO set detail of first todoItem
        detailFragment = (TodoItemDetailFragment)TodoItemDetailFragment.newInstance(items.get(0));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_detail, detailFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
            final Intent intent = new Intent(GroupListActivity.this, DetailTodoItemActivity.class);
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
            detailFragment.updateFragment(todoItem);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == ACCEPT_TASK_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                listFragment.onSwiped(null, 0, data.getIntExtra(getResources().getString(R.string.position),0));
                /*
                RecyclerView recyclerView = findViewById(R.id.recycler_view);
                TodoListAdapter adapter = (TodoListAdapter)recyclerView.getAdapter();
                int position = data.getIntExtra(getResources().getString(R.string.position),0);
                adapter.removeItem(position);
                */
            }
        }
    }

    public void onChooseHouse(MenuItem menuItem){
        ChooseHouseDialog.mListener = this;
        DialogFragment houseListDialog = ChooseHouseDialog.newInstance(whichHouse);
        houseListDialog.show(getFragmentManager(), "dialog");
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }


    @Override
    public void onAcceptClick(TodoItem todoItem) {
        //onBackPressed();
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
        dialog.dismiss();

    }

    public static class ChooseHouseDialog extends DialogFragment{
        public static Dialog.OnClickListener mListener;

        public static ChooseHouseDialog newInstance(int whichHouse) {

            Bundle args = new Bundle();
            args.putInt("chosen_house", whichHouse);
            ChooseHouseDialog fragment = new ChooseHouseDialog();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public Dialog onCreateDialog(final Bundle savedInstanceState) {
            int which = getArguments().getInt("chosen_house");
            final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle(R.string.action_choose_house);
            CharSequence[] entries = new CharSequence[]{"One", "Two", "Three"};
            dialog.setSingleChoiceItems(entries, which, mListener);
            return dialog.create();
        }
    }
}
