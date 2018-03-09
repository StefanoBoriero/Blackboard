package dima.it.polimi.blackboard.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import dima.it.polimi.blackboard.fragments.TodoItemListFragment;

/**
 * Broadcast receiver that disables realtime functionalities when the battery is low
 * Created by Stefano on 09/03/2018.
 */

public class BatteryStatusReceiver extends BroadcastReceiver {
    private TodoItemListFragment myFragment;

    public BatteryStatusReceiver(){
        //Empty constructor for manifest
    }

    public BatteryStatusReceiver(TodoItemListFragment fragment){
        this.myFragment = fragment;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if ((Intent.ACTION_BATTERY_LOW).equals(intent.getAction())){
            myFragment.disableRealTimeUpdate();
        }
        else if(Intent.ACTION_BATTERY_OKAY.equals(intent.getAction())){
            myFragment.enableRealTimeUpdate();
        }
    }
}
