package dima.it.polimi.blackboard.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import dima.it.polimi.blackboard.R;

public class NewToDoTaskActivity extends AppCompatActivity {

    public Button typeButton;
    public EditText editText;
    public LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_to_do_task);
        typeButton = (Button) findViewById(R.id.typeButton);
        editText = (EditText) findViewById(R.id.nameEditText);

        typeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerForContextMenu(typeButton);
                openContextMenu(typeButton);
            }
        });
        editText.clearFocus();

    }

    //We override dispatchTouchEvent in order to take away the focus from
    //the editText when clicking outside the editable field
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();

        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                ((InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
            view.clearFocus();
        }

        return super.dispatchTouchEvent(ev);
    }
    //this creates a context menu to uuse when selecting the type of a task
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
