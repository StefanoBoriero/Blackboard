package dima.it.polimi.blackboard.activities;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dima.it.polimi.blackboard.R;

public class AddHouseDialogActivity extends Activity {
    View inputText ;
    private LinearLayout myLinearLayout;
    List<LinearLayout> emailLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_house_dialog);

        LinearLayout sv = findViewById(R.id.scrollView);
        myLinearLayout = findViewById(R.id.root_layout);
        this.setTitle("Create group");

        emailLL = new ArrayList<>();
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        emailLL.add((LinearLayout) inflater.inflate(R.layout.content_text_input, sv));
        //sv.addView(inputText);
        Button addButton = findViewById(R.id.addButton);
        Button createButton = findViewById(R.id.createButton);


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailLL.add((LinearLayout)inflater.inflate(R.layout.content_text_input, sv));
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseAuth auth = FirebaseAuth.getInstance();
            @Override
            public void onClick(View view) {
                EditText houseNameET = findViewById(R.id.nameEditText);
                String houseName = houseNameET.getText().toString().trim();
                Map<String,Object> house = new HashMap<>();
                Map<String,Object> roommates = new HashMap<>();
                roommates.put("creator",auth.getCurrentUser().getUid());
                List<String> persons = new ArrayList<>();
                persons.add(auth.getCurrentUser().getUid());
                String houseId = houseName + System.currentTimeMillis();
                house.put("name",houseName);
                for(LinearLayout LL: emailLL) {
                    String e_mail = ((EditText) LL.findViewById(R.id.nameEditText)).getText().toString();
                    if (!TextUtils.isEmpty(e_mail)) {
                        db.collection("e-mail").document(e_mail).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    String uid = (String) task.getResult().getData().get("uid");
                                    persons.add(uid);
                                    db.collection("users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                Map<String, Object> userParams = task.getResult().getData();
                                                List<String> houses = (ArrayList<String>) userParams.get("houses");
                                                houses.add(houseId);
                                                userParams.put("houses", houses);
                                                db.collection("users").document(uid).set(userParams);
                                            }
                                        }
                                    });
                                    roommates.put("roommates", persons);
                                    house.put("roommates", roommates);
                                    db.collection("houses").document(houseId).set(house);
                                }

                            }

                        });

                    }


                }
                db.collection("users").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Map<String, Object> userParams = task.getResult().getData();
                            List<String> houses = (ArrayList<String>) userParams.get("houses");
                            houses.add(houseId);
                            userParams.put("houses", houses);
                            db.collection("users").document(auth.getCurrentUser().getUid()).set(userParams);
                        }
                    }
                });
                onBackPressed();
            }
        });
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
                myLinearLayout.requestFocus();
            }

        }

        return super.dispatchTouchEvent(ev);
    }
}
