package dima.it.polimi.blackboard.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import dima.it.polimi.blackboard.R;


/**
 * This Activity shows the user the list of activities his group has defined.
 * It also allows him to view more details about an existing one and pick it.
 * Lastly, the user can add a new task to the list
 * Created by Stefano on 22/11/2017.
 */

public class GroupListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

    }
}
