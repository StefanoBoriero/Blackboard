package dima.it.polimi.blackboard.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.model.House;
import dima.it.polimi.blackboard.model.RoomMate;

/**
 * Created by simone on 17/01/2018.
 */

public class RoomMateListAdapter extends RecyclerView.Adapter<RoomMateListAdapter.ViewHolder> {
    private List<RoomMate> myRoomMates;


    public RoomMateListAdapter(List<RoomMate> roomMates, RoomMateListAdapterListener listener){
        myRoomMates = roomMates;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.room_mate, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RoomMate roomMate = myRoomMates.get(position);
        holder.roomMateName.setText(roomMate.getName());

        //TODO see if we want to click
        /**
        holder.itemView.setOnClickListener((v) ->
                mListener.onHouseClicked(roomMate, v, holder.getAdapterPosition())
        );
         **/

    }

    @Override
    public int getItemCount() {
        return myRoomMates.size();
    }



    public interface RoomMateListAdapterListener {
        //TODO use this if we want to click
        //void onRoomMateClicked(RoomMate roomMate, View roomMateView, int clickedPosition);
    }


    protected class ViewHolder extends RecyclerView.ViewHolder{
        ImageView roomMateImage;
        TextView roomMateName;

        private ViewHolder(View itemView) {
            super(itemView);
            roomMateName = itemView.findViewById(R.id.username);
            roomMateImage = itemView.findViewById(R.id.user_icon);
        }
    }
}
