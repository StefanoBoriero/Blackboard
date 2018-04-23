package dima.it.polimi.blackboard.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.components.Legend;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.model.DayResume;

/**
 * Adapter class to show a DayResume inside a RecyclerView
 * Created by Stefano on 30/01/2018.
 */

public class DayResumeAdapter extends FirestoreAdapter<DayResumeAdapter.ViewHolder>{
    private static final int ITEM_VIEW_TYPE_FOOTER = 2;
    private static final int ITEM_VIEW_TYPE_HEADER = 1;
    private static final int ITEM_VIEW_TYPE_DAY= 0;
    private static final int ITEM_VIEW_TYPE_MESSAGE = 3;
    private static final int ITEM_VIEW_NULL = -1;
    private List<DocumentSnapshot> startingPivots = new ArrayList<>();
    private RecyclerView recyclerView;

    private int weekNumber = 0;

    public DayResumeAdapter(Query query){
        super(query);
    }

    public DayResumeAdapter(Query query, OnCompleteListener listener){
        super(query, listener);
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case ITEM_VIEW_TYPE_DAY:
                itemView = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.day_resume, parent, false);
                break;
            case ITEM_VIEW_TYPE_FOOTER:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.load_more_footer, parent, false);
                break;
            case ITEM_VIEW_TYPE_MESSAGE:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message_first_week_layout, parent, false);
                break;
            default:
                //TODO adjust header
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.load_more_footer, parent, false);
        }
        return new DayResumeAdapter.ViewHolder(itemView);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        this.recyclerView = null;
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int delta;
        if(weekNumber==0){
            delta = 1;
        }
        else{
            delta = 1;
        }
        int itemViewType = getItemViewType(position);
        if(itemViewType == ITEM_VIEW_TYPE_DAY) {
            DayResume day = getSnapshot(position - delta).toObject(DayResume.class);
            holder.bind(day);
        }
        else if(itemViewType == ITEM_VIEW_TYPE_FOOTER){
            ((TextView)holder.itemView.findViewById(R.id.load_message)).setText(R.string.prev_week);
            holder.itemView.setOnClickListener((v)->{
                startingPivots.add(weekNumber,getSnapshot(0));
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null) {
                    hideRecyclerViewWhileLoading();
                    String id = user.getUid();
                    CollectionReference days = db.collection("users").document(id).collection("days");
                    Query query = days.orderBy("day", Query.Direction.DESCENDING).limit(7)
                            .startAfter(getSnapshot(super.getItemCount()-1));
                    weekNumber++;
                    super.setQuery(query);
                }
            });
        }
        else if(itemViewType == ITEM_VIEW_TYPE_HEADER){
            ((TextView)holder.itemView.findViewById(R.id.load_message)).setText(R.string.subsequent_week);
            holder.itemView.setOnClickListener((v)->{
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null) {
                    hideRecyclerViewWhileLoading();
                    weekNumber--;
                    String id = user.getUid();
                    CollectionReference days = db.collection("users").document(id).collection("days");
                    Query query = days.orderBy("day", Query.Direction.DESCENDING).limit(7)
                            .startAt(startingPivots.get(weekNumber));
                    super.setQuery(query);
                }
            });
        }

        else if(itemViewType == ITEM_VIEW_TYPE_MESSAGE){
            TextView message = holder.itemView.findViewById(R.id.message);
            String msg = "Welcome back! \n it has been a while since you were here..";
                message.setText(msg);
        }
    }

    public void goBackOneWeek(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            hideRecyclerViewWhileLoading();
            weekNumber--;
            String id = user.getUid();
            CollectionReference days = db.collection("users").document(id).collection("days");
            Query query = days.orderBy("day", Query.Direction.DESCENDING).limit(7)
                    .startAt(startingPivots.get(weekNumber));
            super.setQuery(query);
        }
    }

    @Override
    public int getItemCount() {
        //We return 2 items more, one for the header and one for the footer
        return super.getItemCount() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        int delta;
        if(weekNumber == 0){
            delta = 1;
        }
        else{
            delta = 1;
        }
        int items = this.getItemCount();
        if(items == 2){
            //There's no day to show :(
            return ITEM_VIEW_NULL;
        }
        else if(position > items-delta){
            return ITEM_VIEW_NULL;
        }
        else if(position == items - delta){
            return ITEM_VIEW_TYPE_FOOTER;
        }
        else if(position == 0 && weekNumber > 0){
            return ITEM_VIEW_TYPE_HEADER;
        }
        else if(position == 0 && weekNumber == 0){
            return ITEM_VIEW_TYPE_MESSAGE;
        }
        else{
            return ITEM_VIEW_TYPE_DAY;
        }
    }

    private void hideRecyclerViewWhileLoading(){
        if(recyclerView != null){
            recyclerView.setVisibility(View.INVISIBLE);
        }
    }

    public DayResume[] getDayResumeArray(){
        int i;
        DayResume[] data = new DayResume[super.getItemCount()];

        for(i=0; i<super.getItemCount(); i++){
            data[i] = getSnapshot(i).toObject(DayResume.class);
        }

        return data;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView completed;
        private TextView added;
        private TextView day;

        private ViewHolder(View itemView) {
            super(itemView);
        }

        private void bind(DayResume day){
            this.completed = itemView.findViewById(R.id.completed_tasks);
            this.added = itemView.findViewById(R.id.added_tasks);
            this.day= itemView.findViewById(R.id.day);

            String completed = DayResume.COMPLETED_MESSAGE + day.getCompletedItems();
            this.completed.setText(completed);

            String added = DayResume.ADDED_MESSAGE + day.getCreatedItems();
            this.added.setText(added);

            DateFormat df = new SimpleDateFormat("EEE, MMM dd", Locale.US);
            this.day.setText(df.format(day.getDay()));
        }
    }
}
