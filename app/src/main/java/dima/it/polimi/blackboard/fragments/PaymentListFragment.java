package dima.it.polimi.blackboard.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import dima.it.polimi.blackboard.R;
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
            adapter = new PaymentListAdapter(this.getContext(),this);
            db = FirebaseFirestore.getInstance();
            user = FirebaseAuth.getInstance().getCurrentUser();
            prepareQuery();
            enableRealTimeUpdate();

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
        this.adapter = new PaymentListAdapter(this.getContext(),this);
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
        myListener = myPaymentsQuery.addSnapshotListener( (querySnapshot, error) ->
        {
            if (error != null) {
                return;
            }

            for(DocumentChange dc: querySnapshot.getDocumentChanges()){
                if(dc.getType() == DocumentChange.Type.ADDED){
                    PaymentItem newItem = dc.getDocument().toObject(PaymentItem.class);
                    if((type.equals("positive") && newItem.getPerformedBy().equals(user.getUid()) || (type.equals("negative") && !newItem.getPerformedBy().equals(user.getUid()))))
                        insertPayment(newItem);
                }
            }

        });
    }

    public void disableRealTimeUpdate(){
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
    }




}
