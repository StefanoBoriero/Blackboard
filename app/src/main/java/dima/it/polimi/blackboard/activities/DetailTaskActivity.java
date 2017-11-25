package dima.it.polimi.blackboard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
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
    private String title;
    private String type;
    private String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_task_detail);

        Intent intent = getIntent();
        todoTaskJSON = new JsonParser().parse(intent.getStringExtra( "JSON_task"))
                .getAsJsonObject();
        extractInfo();


        CollapsingToolbarLayout ctl = findViewById(R.id.detail_collapsing_toolbar);
        TextView tvInfo = findViewById(R.id.detail_additional_info);
        TextView tvDescription = findViewById(R.id.detail_description);

        ctl.setTitle(title);
        tvInfo.setText("Type: " + type);
        tvDescription.setText(description);

    }

    private void extractInfo(){
        this.title = todoTaskJSON.get( getResources().getString(R.string.title_field))
                .getAsString();

        this.type = todoTaskJSON.get( getResources().getString(R.string.type_field))
                .getAsString();

        this.description = todoTaskJSON.get( getResources().getString(R.string.description_field))
                .getAsString();
    }


}
