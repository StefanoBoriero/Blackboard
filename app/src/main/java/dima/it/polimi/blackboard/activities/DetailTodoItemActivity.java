package dima.it.polimi.blackboard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_item_detail);

        Intent intent = getIntent();
        todoItem = intent.getParcelableExtra(getResources().getString(R.string.todo_item));

        createFragment();
        displayFragment();
    }

    private void createFragment(){
        detailFragment = TodoItemDetailFragment.newInstance(todoItem, "eheheh", "ahahaha");
    }

    private void displayFragment(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_placeholder, detailFragment)
                .commit();
    }


    @Override
    public void onAcceptClick(TodoItem todoItem) {

    }

    @Override
    public void onCloseClick() {

    }
}
