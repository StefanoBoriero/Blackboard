package dima.it.polimi.blackboard.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.activities.BalanceActivity;
import dima.it.polimi.blackboard.adapters.PaymentListAdapter;
import dima.it.polimi.blackboard.adapters.TodoListAdapter;
import dima.it.polimi.blackboard.model.PaymentItem;


/**
 * A fragment representing a list of Payments.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class PaymentListFragment extends Fragment implements PaymentListAdapter.PaymentListAdapterListener,
         SwipeRefreshLayout.OnRefreshListener{


    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_TYPE = "type";

    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private PaymentListAdapter adapter;
    private FirebaseFirestore db;
    private Query myPaymentsQuery;
    private FirebaseUser user;
    private ListenerRegistration myListener;
    private String house;
    private RecyclerView recyclerView;
    private String type;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PaymentListFragment() {
    }

    public static PaymentListFragment newInstance(int columnCount, String type, String house) {
        PaymentListFragment fragment = new PaymentListFragment();
        Bundle args = new Bundle();
        args.putString("house",house);
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
       List<PaymentItem> paymentItems;

        super.onCreate(savedInstanceState);


            //type = getArguments().getString(ARG_TYPE);
            if(savedInstanceState != null && savedInstanceState.getString("house") != null)
                this.house = savedInstanceState.getString("house");
        if(savedInstanceState != null && savedInstanceState.getString("type") != null)
            this.type = savedInstanceState.getString("type");
            adapter = new PaymentListAdapter(this.getContext(),this,type);
            db = FirebaseFirestore.getInstance();
            user = FirebaseAuth.getInstance().getCurrentUser();
            if(house != null) {
                prepareQuery();
                enableRealTimeUpdate();
            }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_payment_list, container, false);

        // Setting up the RecyclerView adapter and helpers
        recyclerView = view.findViewById(R.id.recycler_view);
        Context context = view.getContext();
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getActivity(), LinearLayoutManager.VERTICAL));
        // Setting up the refresh layout
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        disableRealTimeUpdate();
        mListener = null;

    }




    @Override
    public void onRefresh() {
        //TODO implement refreshing through Firebase. Add setter for network source
        mListener.onRefresh();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onRefresh();
    }

    public void changeHouse(String selectedHouse)
    {
        this.house = selectedHouse;
        this.adapter = new PaymentListAdapter(this.getContext(),this,type);
        this.recyclerView.setAdapter(adapter);
        prepareQuery();
        disableRealTimeUpdate();
        enableRealTimeUpdate();
    }

    private void insertPayment(PaymentItem item)
    {
        adapter.insertItem(item, 0);
    }

    private void prepareQuery(){


        CollectionReference housePayments = db.collection("houses")
                .document(house)
                .collection("payments");


            myPaymentsQuery = housePayments;



    }

    public void enableRealTimeUpdate(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("houses").document(house).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            Map<String,Object> roommates = (Map<String, Object>) task.getResult().getData().get("roommates");
                            List<String> roommatesList = (List<String>) roommates.get("roommates");
                            Map<String,Object> joinedAtRoomates = (Map<String, Object>) roommates.get("joinedTime");
                            myListener = myPaymentsQuery.addSnapshotListener( (querySnapshot, error) ->
                            {
                                if (error != null) {
                                    return;
                                }
                                Date joinedAt = Calendar.getInstance().getTime();
                                if(joinedAtRoomates != null)
                                    joinedAt = (Date) joinedAtRoomates.get(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                                for(DocumentChange dc: querySnapshot.getDocumentChanges()){
                                    PaymentItem newItem = dc.getDocument().toObject(PaymentItem.class);
                                    Date paymentDate = newItem.getPerformedOn();
                                    if( paymentDate != null && paymentDate.after(joinedAt)) {
                                        //calculate the number of persons that were in the group when the payment has been issued
                                        double numberOfPersonsAtPaymentTime = 0;
                                        for (Map.Entry<String, Object> entry : joinedAtRoomates.entrySet())
                                        {
                                            if(((Date)entry.getValue()).before(paymentDate))
                                                numberOfPersonsAtPaymentTime++;
                                        }
                                        if (dc.getType() == DocumentChange.Type.ADDED) {
                                            if ((type.equals("positive") && newItem.getPerformedBy().equals(user.getUid()) || (type.equals("negative") && !newItem.getPerformedBy().equals(user.getUid())))) {
                                                insertPayment(newItem);
                                                if (newItem.getPerformedBy().equals(user.getUid())) {
                                                    double payment = newItem.getPrice() * ((numberOfPersonsAtPaymentTime - 1) / (numberOfPersonsAtPaymentTime));
                                                    BalanceActivity.refreshBalanceColor(payment);
                                                } else {
                                                    double payment = newItem.getPrice() / (numberOfPersonsAtPaymentTime);
                                                    BalanceActivity.refreshBalanceColor(-payment);
                                                }
                                            }

                                        } else if (dc.getType() == DocumentChange.Type.REMOVED) {
                                            double oldPrice = newItem.getPrice();
                                            if (newItem.getPerformedBy().equals(user.getUid()) && type.equals("positive")) {
                                                double payment = oldPrice * ((numberOfPersonsAtPaymentTime - 1) / (numberOfPersonsAtPaymentTime));
                                                BalanceActivity.refreshBalanceColor(-payment);
                                            } else if (!newItem.getPerformedBy().equals(user.getUid()) && type.equals("negative")) {
                                                double payment = oldPrice / (numberOfPersonsAtPaymentTime);
                                                BalanceActivity.refreshBalanceColor(payment);
                                            }
                                            adapter.removeItem(newItem.getId());
                                        }
                                    }
                                }

                            });
                        }
                    }
                });

    }

    public void disableRealTimeUpdate(){
        if(myListener != null)
            myListener.remove();
    }

    public void setHouse(String house){
        this.house = house;
    }
    public void setType(String type){
        this.type = type;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putString("house",house);
        savedInstanceState.putString("type",type);

    }




    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (getUserVisibleHint()) {
            switch (item.getItemId()) {
                case R.id.firstOption:
                        String id = adapter.getItem(item.getGroupId()).getId();
                        db.collection("houses").document(house).collection("payments").document(id).delete();
                        adapter.removeItem(item.getGroupId());
                    break;
                case R.id.secondOption:
                    // do nothing
                    break;
            }
            return true;
        }
        return false;
    }

}
