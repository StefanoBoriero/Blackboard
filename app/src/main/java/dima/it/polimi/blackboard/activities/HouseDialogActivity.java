package dima.it.polimi.blackboard.activities;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.adapters.HouseListAdapter;
import dima.it.polimi.blackboard.adapters.RoomMateListAdapter;
import dima.it.polimi.blackboard.model.PaymentItem;
import dima.it.polimi.blackboard.model.RoomMate;
import dima.it.polimi.blackboard.utils.DataGeneratorUtil;
import dima.it.polimi.blackboard.utils.UserDecoder;

public class HouseDialogActivity extends Activity implements RoomMateListAdapter.RoomMateListAdapterListener {

    private String id;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_dialog);

        TextView houseName = findViewById(R.id.house_name);

        String name = getIntent().getStringExtra("house");
        id = getIntent().getStringExtra("houseId");
        houseName.setText(name);
        this.setTitle("House information");

        RecyclerView rv = findViewById(R.id.recycler_view_room_mates);


        db = FirebaseFirestore.getInstance();
        db.collection("houses").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> house = document.getData();
                    Map<String, Object> mapRoomates = (Map<String, Object>) house.get("roommates");
                    ArrayList<String> roomMates = (ArrayList<String>) mapRoomates.get("roommates");
                    List<RoomMate> mates = new ArrayList<>();
                    for(String s : roomMates)
                    {
                        RoomMate roomMate = new RoomMate(s);
                        mates.add(roomMate);
                    }
                    RecyclerView.Adapter adapter = new RoomMateListAdapter(mates, HouseDialogActivity.this);
                    rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    rv.setAdapter(adapter);
                    setUpButtons(roomMates);
                }
            }
        });



    }


    private void setUpButtons(ArrayList<String> roommates)
    {
        View addButton = findViewById(R.id.add_member_view);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HouseDialogActivity.this,AddMemberDialogActivity.class);
                intent.putExtra("roommates",roommates);
                intent.putExtra("houseId",id);
                startActivity(intent);
            }
        });

        View leaveButton = findViewById(R.id.leave_view);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        leaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("houses").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            Map<String,Object> houseParams = task.getResult().getData();
                            Map<String,Object> roomMatesMap = (Map<String, Object>) houseParams.get("roommates");
                            Map<String,Object> joinedAtRoomates = (Map<String, Object>) roomMatesMap.get("joinedTime");
                            roommates.remove(userId);
                            roomMatesMap.put("roommates",roommates);
                            houseParams.put("roommates",roomMatesMap);
                            db.collection("houses").document(id).set(houseParams);
                            db.collection("houses").document(id).collection("payments").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful())
                                    {
                                        double exitBalance = 0;
                                        for(DocumentChange dc: task.getResult().getDocumentChanges()) {
                                            PaymentItem newItem = dc.getDocument().toObject(PaymentItem.class);
                                            Date paymentDate = newItem.getPerformedOn();
                                            Date joinedAt = Calendar.getInstance().getTime();
                                            if(joinedAtRoomates != null)
                                                joinedAt = (Date) joinedAtRoomates.get(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                                            if( paymentDate != null && paymentDate.after(joinedAt)) {
                                                double numberOfPersonsAtPaymentTime = newItem.getNumberOfRoommates();
                                                if (newItem.getPerformedBy().equals(FirebaseAuth.getInstance().getCurrentUser().getUid().toString())) {
                                                    double payment = newItem.getPrice() * ((numberOfPersonsAtPaymentTime - 1) / (numberOfPersonsAtPaymentTime));
                                                    exitBalance = exitBalance + payment;
                                                } else {
                                                    double payment = newItem.getPrice() / (numberOfPersonsAtPaymentTime);
                                                    exitBalance = exitBalance - payment;
                                                }
                                            }
                                            }
                                        String finalId = FirebaseAuth.getInstance().getCurrentUser().getDisplayName() + System.currentTimeMillis();
                                        String finalName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName() + " left";
                                        Date date = Calendar.getInstance().getTime();
                                        if(exitBalance != 0)
                                            {
                                                PaymentItem finalPayment = new PaymentItem(finalId,finalName,-exitBalance,date,roommates.size());
                                                db.collection("houses").document(id).collection("payments").document(finalId).set(finalPayment);
                                            }

                                        }

                                    }
                            });
                            onBackPressed();
                        }
                    }
                });



                db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            Map<String,Object> userParams = task.getResult().getData();
                            List<String> houses = (List<String>) userParams.get("houses");
                            houses.remove(id);
                            userParams.put("houses",houses);
                            db.collection("users").document(userId).set(userParams);
                        }
                    }
                });
            }
        });


    }
}
