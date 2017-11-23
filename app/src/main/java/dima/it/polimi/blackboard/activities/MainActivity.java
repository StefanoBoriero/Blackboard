package dima.it.polimi.blackboard.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import dima.it.polimi.blackboard.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void launchGroupList(View view){
        Intent intent = new Intent(MainActivity.this, GroupListActivity.class);
        startActivity(intent);
    }
}
