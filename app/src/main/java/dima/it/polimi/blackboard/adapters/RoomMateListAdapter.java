package dima.it.polimi.blackboard.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.activities.HouseDialogActivity;
import dima.it.polimi.blackboard.fragments.TodoItemListFragment;
import dima.it.polimi.blackboard.model.RoomMate;
import dima.it.polimi.blackboard.utils.GlideApp;
import dima.it.polimi.blackboard.utils.UserDecoder;

/**Adapter used to create the roomMate list
 * Created by simone on 17/01/2018.
 */

public class RoomMateListAdapter extends RecyclerView.Adapter<RoomMateListAdapter.ViewHolder> {
    private final List<RoomMate> myRoomMates;
    private RoomMateListAdapterListener mListener;


    public RoomMateListAdapter(List<RoomMate> roomMates, RoomMateListAdapterListener listener){
        myRoomMates = roomMates;
        this.mListener = listener;

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
        holder.roomMateName.setText(UserDecoder.getInstance().getNameFromId(roomMate.getName()));



        FirebaseStorage storage = FirebaseStorage.getInstance();
        DocumentReference userReference = FirebaseFirestore.getInstance().collection("users").document(roomMate.getName());
        userReference.get().addOnCompleteListener((task) -> {
            List<CharSequence> myHouses = new ArrayList<>();
            if (task.isSuccessful()) {
                {
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> userParam = document.getData();
                    String lastEdit = (String)userParam.get("lastEdit");
                    Map<String, Object> personal_info = (Map<String, Object>) userParam.get("personal_info");
                    String name = (String) personal_info.get("name");
                    holder.roomMateName.setText(name);
                    StorageReference reference = storage.getReference().child(roomMate.getName() + "/profile" + lastEdit);
                    GlideApp.with((HouseDialogActivity)mListener)
                            .load(reference)
                            .placeholder(R.drawable.empty_profile_blue_circle)
                            .error(R.drawable.empty_profile_blue_circle)
                            .apply(RequestOptions.circleCropTransform())
                            .into(holder.roomMateImage);
                }

            } else {
                //TODO display error message
            }
        });

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
        final ImageView roomMateImage;
        final TextView roomMateName;

        private ViewHolder(View itemView) {
            super(itemView);
            roomMateName = itemView.findViewById(R.id.username);
            roomMateImage = itemView.findViewById(R.id.user_icon);
        }
    }
}
