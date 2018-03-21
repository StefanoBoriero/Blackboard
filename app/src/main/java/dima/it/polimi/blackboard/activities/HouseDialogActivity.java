package dima.it.polimi.blackboard.activities;

import android.app.Activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.adapters.HouseListAdapter;
import dima.it.polimi.blackboard.adapters.RoomMateListAdapter;
import dima.it.polimi.blackboard.model.RoomMate;
import dima.it.polimi.blackboard.utils.DataGeneratorUtil;
import dima.it.polimi.blackboard.utils.UserDecoder;

public class HouseDialogActivity extends Activity implements RoomMateListAdapter.RoomMateListAdapterListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_dialog);

        TextView houseName = findViewById(R.id.house_name);

        String name = getIntent().getStringExtra("house");
        String id = getIntent().getStringExtra("houseId");
        houseName.setText(name);
        this.setTitle("House information");

        RecyclerView rv = findViewById(R.id.recycler_view_room_mates);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("houses").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> house = document.getData();
                    Map<String, Object> mapRoomates = (Map<String, Object>) house.get("roommates");
                    List<String> roomMates = (List<String>) mapRoomates.get("roommates");
                    List<RoomMate> mates = new ArrayList<>();
                    for(String s : roomMates)
                    {
                        RoomMate roomMate = new RoomMate(s);
                        mates.add(roomMate);
                    }
                    RecyclerView.Adapter adapter = new RoomMateListAdapter(mates, HouseDialogActivity.this);
                    rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    rv.setAdapter(adapter);
                }
            }
        });

    }


}
