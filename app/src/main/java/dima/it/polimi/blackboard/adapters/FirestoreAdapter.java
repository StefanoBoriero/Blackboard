package dima.it.polimi.blackboard.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Filter;
import android.widget.Filterable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dima.it.polimi.blackboard.model.TodoItem;

/**
 * Abstract adapter that gets data from Firestore
 * Created by Stefano on 10/03/2018.
 */

public abstract class FirestoreAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> implements EventListener<QuerySnapshot>, Filterable, Filter.FilterListener {

    private static final String TAG = "FirestoreAdapter";

    private Query mQuery;
    private ListenerRegistration mRegistration;

    private List<DocumentSnapshot> mSnapshots;
    private List<DocumentSnapshot> mFilteredSnapshots;
    private DocumentSnapshot lastRemoved;
    private boolean removedByMe;
    private boolean insertedByMe;
    private boolean firstTime;
    private boolean liveUpdate;

    private OnCompleteListener mListener;
    private String filter="";

    FirestoreAdapter(Query query, OnCompleteListener listener){
        mQuery = query;
        mSnapshots = new ArrayList<>();
        //mFilteredSnapshots = new ArrayList<>();
        mListener = listener;
        firstTime = true;
    }

    FirestoreAdapter(Query query){
        mQuery = query;
        mSnapshots = new ArrayList<>();
        firstTime = true;
        //mFilteredSnapshots = new ArrayList<>();
    }

    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e){
        if(e != null){
            Log.w(TAG, "onEvent:error", e);
            return;
        }

        Log.d(TAG, "onEvent:numChanges:" + documentSnapshots.getDocumentChanges().size());
        for(DocumentChange change: documentSnapshots.getDocumentChanges()){
            switch (change.getType()){
                case ADDED:
                    onDocumentAdded(change);
                    break;
                case MODIFIED:
                    onDocumentModified(change);
                    break;
                case REMOVED:
                    onDocumentRemoved(change);
                    break;

            }
        }
        if(mFilteredSnapshots == null){
            firstTime = false;
            mFilteredSnapshots = mSnapshots;
        }
        if(mListener != null) {
            mListener.onComplete(mFilteredSnapshots.isEmpty());
        }
    }

    @Override
    public void onFilterComplete(int i) {
        if(mListener != null) {
            Log.d(TAG, "Completed sync: numDocuments:" + mFilteredSnapshots.size());
            mListener.onComplete(mFilteredSnapshots.isEmpty());
        }

    }

    private void onDocumentAdded(DocumentChange change){
        mSnapshots.add(change.getNewIndex(), change.getDocument());
        notifyItemInserted(change.getNewIndex());
        if(insertedByMe){
            insertedByMe = false;
        }
        else{
            if(mListener != null && !firstTime){
                mListener.addedByOther(change.getNewIndex());
            }
        }
    }

    private void onDocumentModified(DocumentChange change){
        if (change.getOldIndex() == change.getNewIndex()) {
            // Item changed but remained in same position
            try {
                mSnapshots.set(change.getOldIndex(), change.getDocument());
                notifyItemChanged(change.getOldIndex());
            }
            catch (IndexOutOfBoundsException e){
                Log.e(TAG, e.getMessage());
            }
        } else {
            // Item changed and changed position
            mSnapshots.remove(change.getOldIndex());
            mSnapshots.add(change.getNewIndex(), change.getDocument());
            notifyItemMoved(change.getOldIndex(), change.getNewIndex());
        }
    }

    private void onDocumentRemoved(DocumentChange change){
        if(removedByMe){
            removedByMe = false;
            mSnapshots.remove(lastRemoved);
        }
        else{
            int oldIndex = change.getOldIndex();
            mSnapshots.remove(oldIndex);
            notifyItemRemoved(oldIndex);
            if(mListener != null){
                mListener.deleteByOther(oldIndex);
            }
        }
    }

    public void startListening(){
        if (mQuery != null && mRegistration == null) {
            mSnapshots.clear();
            if (mFilteredSnapshots != null){
                mFilteredSnapshots.clear();
            }
            notifyDataSetChanged();
            mRegistration = mQuery.addSnapshotListener(this);
        }
    }

    public void startListeningOnBatteryOk(){
        if (mQuery != null && mRegistration == null) {
            mSnapshots.clear();
            if (mFilteredSnapshots != null){
                mFilteredSnapshots.clear();
            }
            notifyDataSetChanged();
            mRegistration = mQuery.addSnapshotListener(this);
            if(mListener != null) {
                mListener.resetOnFirst();
            }
        }
    }

    public void stopListening(){
        if (mRegistration != null) {
            mRegistration.remove();
            mRegistration = null;
        }
    }

    public void setLiveUpdate(boolean live) {
        this.liveUpdate = live;
    }

    public void setQuery(Query query) {
        stopListening();
        mSnapshots.clear();
        firstTime = true;
        if (mFilteredSnapshots != null){
            mFilteredSnapshots.clear();
        }
        notifyDataSetChanged();

        mQuery = query;
        if(liveUpdate) {
            startListening();
        }
        else{
            forceRefresh();
        }
    }

    @Override
    public int getItemCount() {
        if(mFilteredSnapshots == null){
            return 0;
        }
        return mFilteredSnapshots.size();
    }

    DocumentSnapshot getSnapshot(int index) {
        return mSnapshots.get(index);
    }

    DocumentSnapshot getFilteredSnapshot(int index) throws IndexOutOfBoundsException{
        return mFilteredSnapshots.get(index);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String expression = constraint.toString();
                filter = expression;
                if(expression.isEmpty()){
                    mFilteredSnapshots = mSnapshots;
                }
                else {
                    List<DocumentSnapshot> filteredItems = new ArrayList<>();
                    for (DocumentSnapshot doc : mSnapshots) {
                        String name = (String)doc.getData().get("name");
                        String type = (String)doc.getData().get("type");
                        if(name.toLowerCase().contains(expression.toLowerCase())
                                || type.toLowerCase().contains(expression.toLowerCase())){
                            filteredItems.add(doc);
                        }
                    }
                    mFilteredSnapshots = filteredItems;
                }
                FilterResults results = new FilterResults();
                results.values = mFilteredSnapshots;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFilteredSnapshots = (List<DocumentSnapshot>)results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void removeItem(int position){
        try {
            lastRemoved = mFilteredSnapshots.remove(position);
            removedByMe = true;
            notifyItemRemoved(position);
        }
        catch (IndexOutOfBoundsException e){
            Log.d("FirestoreAdapter", e.getMessage());
        }

    }

    public boolean isListening(){
        return mRegistration != null;
    }

    public void forceRefresh(){
        mSnapshots.clear();
        firstTime = true;
        notifyDataSetChanged();
        if (mFilteredSnapshots != null){
            mFilteredSnapshots.clear();
        }

        mQuery.get().addOnCompleteListener( querySnapshotTask -> {
            if(querySnapshotTask.isSuccessful()){
                QuerySnapshot snap = querySnapshotTask.getResult();
                for(DocumentChange change: snap.getDocumentChanges()){
                    switch (change.getType()){
                        case ADDED:
                            onDocumentAdded(change);
                            break;
                        case MODIFIED:
                            onDocumentModified(change);
                            break;
                        case REMOVED:
                            onDocumentRemoved(change);
                            break;
                    }
                }
                if(mFilteredSnapshots == null){
                    firstTime = false;
                    mFilteredSnapshots = mSnapshots;
                }
                if(mListener != null) {
                    mListener.onComplete(mFilteredSnapshots.isEmpty());
                }
            }
        });
    }

    void insertItem(int position){

        mFilteredSnapshots.add(position, lastRemoved);
        notifyItemInserted(position);
    }

    public interface OnCompleteListener{
        void onComplete(boolean emptyResult);
        void deleteByOther(int position);
        void addedByOther(int position);
        void resetOnFirst();
    }
}
