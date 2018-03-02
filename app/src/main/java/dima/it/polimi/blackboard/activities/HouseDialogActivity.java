package dima.it.polimi.blackboard.activities;

import android.app.Activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


import java.util.List;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.adapters.RoomMateListAdapter;
import dima.it.polimi.blackboard.model.RoomMate;
import dima.it.polimi.blackboard.utils.DataGeneratorUtil;

public class HouseDialogActivity extends Activity implements RoomMateListAdapter.RoomMateListAdapterListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_dialog);

        List<RoomMate> roomMates = DataGeneratorUtil.generateRoomMates(5);

        RecyclerView rv = findViewById(R.id.recycler_view_room_mates);
        RecyclerView.Adapter adapter = new RoomMateListAdapter(roomMates,this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);


    }
}
