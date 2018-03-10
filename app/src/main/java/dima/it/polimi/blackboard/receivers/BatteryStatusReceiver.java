package dima.it.polimi.blackboard.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import dima.it.polimi.blackboard.adapters.FirestoreAdapter;
import dima.it.polimi.blackboard.fragments.TodoItemListFragment;

/**
 * Broadcast receiver that disables realtime functionalities when the battery is low
 * Created by Stefano on 09/03/2018.
 */

public class BatteryStatusReceiver extends BroadcastReceiver {
    private FirestoreAdapter mAdapter;

    public BatteryStatusReceiver(){
        //Empty constructor for manifest
    }

    public BatteryStatusReceiver(FirestoreAdapter adapter){
        this.mAdapter = adapter;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if ((Intent.ACTION_BATTERY_LOW).equals(intent.getAction())){
            mAdapter.stopListening();
        }
        else if(Intent.ACTION_BATTERY_OKAY.equals(intent.getAction())){
            mAdapter.startListening();
        }
    }
}
