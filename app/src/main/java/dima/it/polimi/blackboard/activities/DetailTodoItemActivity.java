package dima.it.polimi.blackboard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.view.View;

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
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_item_detail);

        Intent intent = getIntent();
        todoItem = intent.getParcelableExtra(getResources().getString(R.string.todo_item));
        String iconTransitionName = intent.getStringExtra(getResources().getString(R.string.icon_tr_name));
        String nameTransitionName = intent.getStringExtra(getResources().getString(R.string.name_tr_name));
        position = intent.getIntExtra(getResources().getString(R.string.position),0);

        createFragment(todoItem, iconTransitionName, nameTransitionName);
        displayFragment();

        getWindow().getSharedElementEnterTransition().addListener(new Transition.TransitionListener() {

            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                View contentDetail = findViewById(R.id.content_detail);
                contentDetail.animate().alpha(1.0f).setDuration(300).start();

            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }

        });

    }

    private void createFragment(TodoItem todoItem, String iconTransitionName, String nameTransitionName){
        detailFragment = TodoItemDetailFragment.newInstance(todoItem, iconTransitionName, nameTransitionName, position);
    }

    private void displayFragment(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_placeholder, detailFragment)
                .commit();
    }


    @Override
    public void onAcceptClick(TodoItem todoItem, int position) {
        Intent resultData = new Intent();
        resultData.putExtra(getResources().getString(R.string.position), position);
        setResult(RESULT_OK, resultData);
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        View contentDetail = findViewById(R.id.content_detail);
        contentDetail.animate()
                .alpha(0.0f)
                .setDuration(300)
                .start();
        super.onBackPressed();
    }
}
