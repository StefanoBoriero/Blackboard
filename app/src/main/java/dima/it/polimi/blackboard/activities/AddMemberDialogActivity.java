package dima.it.polimi.blackboard.activities;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.model.RoomMate;

public class AddMemberDialogActivity extends Activity {
    private FirebaseFirestore db ;
    private String houseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member_dialog);
        this.setTitle("Add member");
        db = FirebaseFirestore.getInstance();

        ArrayList<String> roomMates = getIntent().getStringArrayListExtra("roommates");
        houseId = getIntent().getStringExtra("houseId");
        Button addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText emailET = findViewById(R.id.emailET);
                String email = emailET.getText().toString().toLowerCase();
                if(TextUtils.isEmpty(email))
                    emailET.setError("Please enter a valid e-mail address");
                else {
                    db.collection("e-mail").document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (!task.getResult().exists()) {
                                    //the mail is not registered to Blackboard
                                    emailET.setError("Please enter a valid e-mail address");
                                } else {
                                    //the mail is valid and is not already in the group
                                    String userId = (String) task.getResult().get("uid");
                                    if (roomMates.contains(userId)) {
                                        //the member is already in the group
                                        emailET.setError("Please enter valid e-mail address");
                                    } else {
                                        addMember(userId);
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void addMember(String userId)
    {
        db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> userParams = task.getResult().getData();
                    List<String> houses;
                    if(userParams.get("houses")!= null) {
                        houses = (ArrayList<String>) userParams.get("houses");
                    }
                    else
                    {
                        houses = new ArrayList<>();
                    }
                    houses.add(houseId);
                    userParams.put("houses", houses);
                    db.collection("users").document(userId).set(userParams);
                }
            }
        });

        db.collection("houses").document(houseId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    Map<String,Object> houseParams = task.getResult().getData();
                    Map<String,Object> roomMatesMap = (Map<String, Object>) houseParams.get("roommates");
                    Map<String,Object> dateJoined = (Map<String, Object>) roomMatesMap.get("joinedTime");
                    List<String> roomMates = (List<String>) roomMatesMap.get("roommates");

                    dateJoined.put(userId, Calendar.getInstance().getTime());
                    roomMates.add(userId);
                    roomMatesMap.put("roommates",roomMates);
                    roomMatesMap.put("joinedTime",dateJoined);
                    houseParams.put("roommates",roomMatesMap);
                    db.collection("houses").document(houseId).set(houseParams);
                    setResult(2);
                    onBackPressed();
                }
            }
        });
    }
}
