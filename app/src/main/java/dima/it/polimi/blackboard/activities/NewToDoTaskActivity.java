package dima.it.polimi.blackboard.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;

import dima.it.polimi.blackboard.R;

public class NewToDoTaskActivity extends AppCompatActivity {

    public Button typeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_to_do_task);
        typeButton = (Button) findViewById(R.id.typeButton);

        typeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerForContextMenu(typeButton);
                openContextMenu(typeButton);
            }
        });
    }

    @Override
    public void onCreateContextMenu (ContextMenu menu, View
            v, ContextMenu.ContextMenuInfo menuInfo){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.task_type_selection_menu,menu);
    }


    public boolean onContextItemSelected (MenuItem item){
        // TODO Auto-generated method stub


    return true;
    }


}
