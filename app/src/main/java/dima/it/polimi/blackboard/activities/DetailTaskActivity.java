package dima.it.polimi.blackboard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import dima.it.polimi.blackboard.R;

/**
 * This activity presents the details of a task.
 * Furthermore allows the user to take in charge the task.
 * Created by Stefano on 23/11/2017.
 */

public class DetailTaskActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_task_detail);

        Intent intent = getIntent();
        String title = intent.getStringExtra( getResources().getString(R.string.task_name));
        TextView tv = findViewById(R.id.textView);
        tv.setText(title);

    }
}
