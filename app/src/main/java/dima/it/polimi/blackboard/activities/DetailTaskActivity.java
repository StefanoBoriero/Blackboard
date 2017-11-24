package dima.it.polimi.blackboard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dima.it.polimi.blackboard.R;

/**
 * This activity presents the details of a task.
 * Furthermore allows the user to take in charge the task.
 * Created by Stefano on 23/11/2017.
 */

public class DetailTaskActivity extends AppCompatActivity {
    //TODO extract the text view style in a proprer @style/xxx.xml file
    private JsonObject todoTaskJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_task_detail);

        Intent intent = getIntent();
        todoTaskJSON = new JsonParser().parse(intent.getStringExtra( "JSON_task"))
                .getAsJsonObject();
        String title = todoTaskJSON.get( getResources().getString(R.string.title_field))
                .getAsString();

        TextView tv = findViewById(R.id.textView);

        tv.setText(title);

    }
}
