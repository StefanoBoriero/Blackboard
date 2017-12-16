package dima.it.polimi.blackboard.activities;


import android.app.SearchManager;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.ArcMotion;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.transition.Visibility;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;


import java.util.List;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.adapters.TodoListAdapter;
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

    private static final int FADE_DURATION = 400;
    private static final int MOVE_DURATION = 400;
    private static final int SLIDE_DURATION = 400;

    private Fragment detailFragment;
    private Fragment listFragment;
    private List<TodoItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Todo remove this call
        items = DataGeneratorUtil.generateTodoItems(30);

        displayListFragment();
        detailFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_detail);
        if(detailFragment != null){
            displayDetailFragment();
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_list, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView)menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        final TodoListAdapter adapter = ((TodoItemListFragment)listFragment).getAdapter();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }

    private void displayListFragment(){
        listFragment = TodoItemListFragment.newInstance(1, items);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_list_container, listFragment)
                .commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    private void displayDetailFragment(){
        //TODO set detail of first todoItem
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
            swapFragments(todoItem, view);
        }
        else{
            //TODO implement the fragment update for bigger screens
        }
    }

    /**
     * This method is called on devices with smaller screens. It swaps the fragment containing
     * the list of all items with one containing the details of the one that has been clicked
     * @param todoItem the item which details have to be displayed
     * @param itemRow  the view row that has been clicked
     */
    private void swapFragments(TodoItem todoItem, View itemRow){
        final View sharedElementIcon = itemRow.findViewById(R.id.user_icon);
        final View sharedElementName = itemRow.findViewById(R.id.item_name);
        final Fragment listFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_list_container);
        final Fragment detailFragment = TodoItemDetailFragment.newInstance(todoItem, sharedElementIcon.getTransitionName(),
                sharedElementName.getTransitionName());

        findViewById(R.id.background_row).setVisibility(View.GONE);

        // Set proper transitions
        listFragment.setExitTransition(createExitTransition());
        detailFragment.setEnterTransition(createEnterTransition());
        detailFragment.setSharedElementEnterTransition(createSharedElementTransitions());

        // Swap the fragments
        getSupportFragmentManager().beginTransaction()
                .addToBackStack(detailFragment.getTag())
                .addSharedElement(sharedElementIcon, sharedElementIcon.getTransitionName())
                .addSharedElement(sharedElementName, sharedElementName.getTransitionName())
                .replace(R.id.fragment_list_container, detailFragment)
                .commit();
    }

    private Transition createExitTransition(){
        //Fading the old content out
        Fade fade = new Fade();
        fade.setMode(Visibility.MODE_OUT);
        fade.setDuration(FADE_DURATION);
        return fade;
    }

    private TransitionSet createEnterTransition(){
        TransitionSet enterSet = new TransitionSet();

        //Fading the new content in
        Fade fadeIn = new Fade();
        fadeIn.setMode(Visibility.MODE_IN);
        fadeIn.setDuration(FADE_DURATION);

        //Sliding in the header
        Slide enterSlide = new Slide();
        enterSlide.setSlideEdge(Gravity.TOP);
        enterSlide.setDuration(SLIDE_DURATION);
        enterSlide.addTarget(R.id.header);

        enterSet.addTransition(fadeIn);
        enterSet.addTransition(enterSlide);
        enterSet.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);

        return enterSet;
    }

    private TransitionSet createSharedElementTransitions(){
        TransitionSet set = new TransitionSet();

        // Create image transition
        Transition moveImage = TransitionInflater.from(this).inflateTransition(android.R.transition.move);
        moveImage.setPathMotion(new ArcMotion());
        moveImage.setDuration(MOVE_DURATION);

        set.addTransition(moveImage);
        return set;
    }

    /**
     * Implementation of the interface to delegate swap.
     * It removes the item from the list, giving opportunity for undo the operation through a
     * Snackbar message
     * @param viewHolder the view holder that has been swiped
     * @param direction  the direction of the swipe
     * @param position   the position of the item in the list
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position){
        //TODO implement take in charge  network functionality
        TodoItemListFragment listFragment = (TodoItemListFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_list_container);
        View view = findViewById(R.id.root_view);
        final int removedIndex = viewHolder.getAdapterPosition();
        final TodoItem removedItem = items.get(removedIndex);

        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        final TodoListAdapter adapter= listFragment.getAdapter();
        adapter.removeItem(removedIndex);

        Snackbar.make(view, "You took charge of the activity",
                Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int itemCount = adapter.getItemCount();
                        adapter.insertItem(removedItem, removedIndex);
                        if(removedIndex == 0 || removedIndex == itemCount){
                            recyclerView.scrollToPosition(removedIndex);
                        }
                    }
                }).show();
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
        //TODO display BRUNITTI activity with circular Reveal
        View view = findViewById(R.id.root_view);
        Snackbar.make(view, "Replace with Brunitti's Action", Snackbar.LENGTH_LONG).show();
    }
}
