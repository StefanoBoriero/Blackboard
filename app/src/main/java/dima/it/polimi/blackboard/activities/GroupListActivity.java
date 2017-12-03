package dima.it.polimi.blackboard.activities;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.adapters.TodoListAdapter;
import dima.it.polimi.blackboard.fragments.ToDoTaskDetailFragment;
import dima.it.polimi.blackboard.fragments.TodoItemListFragment;
import dima.it.polimi.blackboard.helper.TodoItemTouchHelper;
import dima.it.polimi.blackboard.model.TodoItem;

/**
 * This activity presents the list of todo_items that have been added to the group but nobody has
 * taken in charge. It displays at first a fragment with the list in a RecyclerView, and on click of
 * one item displays the details in another fragment.
 */
public class GroupListActivity extends AppCompatActivity implements TodoItemListFragment.OnListFragmentInteractionListener,
ToDoTaskDetailFragment.OnTodoItemDetailInteraction{

    private List<TodoItem> todoItemList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TodoListAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
/*
        recyclerView = findViewById(R.id.recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        // TODO implement refreshing
        // swipeRefreshLayout.setOnRefreshListener(this);

        createSampleTodoItems();

        mAdapter = new TodoListAdapter(this, todoItemList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
*/

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content, TodoItemListFragment.newInstance(1))
                .commit();

    }

    private void createSampleTodoItems(){
        for(int i=0; i<31; i++){
            TodoItem item = new TodoItem("Clean " + i,
                    "Housing", "The house has to be cleaned");
            todoItemList.add(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_list, menu);
        return true;
    }


    /**
     * This method is called when an item in the list displayed by the fragment is clicked.
     * The activity creates a new fragment containing the details of the item clicked and shows it
     * @param todoItem the item clicked
     * @param view the view clicked
     */
    @Override
    public void onItemClick(TodoItem todoItem, View view) {
        //TODO implement detail fragment display
        view.setSelected(true);
        elevateAndExpand(view, findViewById(R.id.content));
        /*
        TextView itemName = view.findViewById(R.id.item_name);
        ImageView userIcon = view.findViewById(R.id.user_icon);


        String transitionName = itemName.getTransitionName();
        String transitionNameIcon = userIcon.getTransitionName();

        Fragment detailFragment = ToDoTaskDetailFragment.newInstance(todoItem, transitionName,
                transitionNameIcon);

        getSupportFragmentManager().beginTransaction()
                .addSharedElement(itemName, transitionName)
                .addSharedElement(userIcon, transitionNameIcon)
                .replace(R.id.content, detailFragment)
                .commit();
*/
    }

    private void elevateAndExpand(final View target, View parent){
        final int startingHeight = target.getMeasuredHeight();
        final int finalHeight = parent.getMeasuredHeight();

        final Float targetElevation = getResources().getDimension(R.dimen.on_reveal_elevation);
        final Integer duration = getResources().getInteger(R.integer.on_reveal_duration);

        ObjectAnimator elevator = ObjectAnimator.ofFloat(target, "elevation", targetElevation);
        elevator.setDuration(duration);
        elevator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ValueAnimator expander = ValueAnimator.ofInt(startingHeight, finalHeight);
                expander.setDuration(duration);
                expander.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Integer value = (Integer)animation.getAnimatedValue();
                        target.getLayoutParams().height = value;
                        target.requestLayout();
                    }
                });

                ObjectAnimator translator = ObjectAnimator.ofFloat(target, "y", 0);
                translator.setDuration(duration);


                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(expander, translator);
                animatorSet.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        elevator.start();
/*
        ValueAnimator expander = ValueAnimator.ofInt(startingHeight, finalHeight);
        expander.setDuration(duration);
        expander.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer)animation.getAnimatedValue();
                target.getLayoutParams().height = value;
                target.requestLayout();
            }
        });

        ObjectAnimator translator = ObjectAnimator.ofFloat(target, "y", 0);
        translator.setDuration(duration);


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(elevator, expander, translator);
        //animatorSet.playTogether();
        animatorSet.start();
*/
    }

    @Override
    public void onSwipeLeft(){
        //TODO implement take in charge or delete functionalities
        View view = findViewById(R.id.root_view);
        Snackbar.make(view, "OH SHIT YOU SWIPED LEFT",
                Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public void onSwipeRight(){
        View view = findViewById(R.id.root_view);
        Snackbar.make(view, "OH SHIT YOU SWIPED RIGHT",
                Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }


    @Override
    public void onAcceptClick(TodoItem todoItem) {
        //onBackPressed();
    }

    @Override
    public void onCloseClick() {
        //onBackPressed();
    }
}
