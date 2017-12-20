package dima.it.polimi.blackboard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.transition.ArcMotion;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.fragments.TodoItemDetailFragment;
import dima.it.polimi.blackboard.model.TodoItem;

/**
 * This activity presents the details of a task.
 * Furthermore allows the user to take in charge the task.
 * Created by Stefano on 23/11/2017.
 */

public class DetailTodoItemActivity extends AppCompatActivity implements TodoItemDetailFragment.OnTodoItemDetailInteraction{
    private TodoItem todoItem;
    private Fragment detailFragment;
    private static final int MOVE_DURATION = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_item_detail);

        Intent intent = getIntent();
        todoItem = intent.getParcelableExtra(getResources().getString(R.string.todo_item));
        String iconTransitionName = intent.getStringExtra(getResources().getString(R.string.icon_tr_name));
        String nameTransitionName = intent.getStringExtra(getResources().getString(R.string.name_tr_name));

        createFragment(todoItem, iconTransitionName, nameTransitionName);
        displayFragment();
        setTransitions();
    }

    private void createFragment(TodoItem todoItem, String iconTransitionName, String nameTransitionName){
        detailFragment = TodoItemDetailFragment.newInstance(todoItem, iconTransitionName, nameTransitionName);
    }

    private void displayFragment(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_placeholder, detailFragment)
                .commit();
    }

    private void setTransitions(){
        Transition moveImage = TransitionInflater.from(this).inflateTransition(android.R.transition.move);
        moveImage.setPathMotion(new ArcMotion());
        moveImage.setInterpolator(new AccelerateDecelerateInterpolator());
        moveImage.setDuration(MOVE_DURATION);

    }

    @Override
    public void onAcceptClick(TodoItem todoItem) {

    }

    @Override
    public void onCloseClick() {

    }
}
