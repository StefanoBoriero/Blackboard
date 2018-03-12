package dima.it.polimi.blackboard.activities;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.model.PersonalInfo;

public class InsertDetailsActivity extends AppCompatActivity {
    FirebaseFirestore db;
    ConstraintLayout myConstraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_details);
        db = FirebaseFirestore.getInstance();
        myConstraintLayout = findViewById(R.id.root_layout);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        FirebaseAuth.getInstance().signOut();
    }

    public void onSubmit(View v)
    {
        EditText nameEditText = findViewById(R.id.nameEditText);
        EditText surnameEditText = findViewById(R.id.surnameEditText);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        RadioButton selectedRadioButton = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());



        Map<String,Object> user = new HashMap<>();
        Map<String,Object> personalInfo = new HashMap<>();
        Map<String,Object> stats = new HashMap<>();
        personalInfo.put("name",nameEditText.getText().toString().trim());
        personalInfo.put("surname",surnameEditText.getText().toString().trim());
        personalInfo.put("Sex",selectedRadioButton.getText());
        stats.put("billing_task",0);
        stats.put("housekeeping_task",0);
        stats.put("shopping_task",0);
        stats.put("total_task",0);
        user.put("auth_id",FirebaseAuth.getInstance().getCurrentUser().getUid() );
        user.put("personal_info",personalInfo);
        user.put("stats", stats);



        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(user);

        finish();
        Intent i = new Intent(InsertDetailsActivity.this,MainActivity.class);
        startActivity(i);
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
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom()) {
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null) {
                    imm.hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
                }
                view.clearFocus();
                //give focus to constraint layout
                myConstraintLayout.requestFocus();
            }

        }

        return super.dispatchTouchEvent(ev);
    }


}
