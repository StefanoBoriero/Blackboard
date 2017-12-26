package dima.it.polimi.blackboard.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.adapters.PaymentListAdapter;
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
    private static final String ARG_TODO_ITEMS = "todo-items";

    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private PaymentListAdapter adapter;
    private List<PaymentItem> paymentItems;
    private CollapsingToolbarLayout balanceToolbar;

    // Attributes for onStop() onStart() consistency
    private CharSequence savedQuery;
    private int position;

    private AppCompatActivity parentActivity;
    private View rootView;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PaymentListFragment() {
    }

    public static PaymentListFragment newInstance(int columnCount, List<PaymentItem> paymentItems) {
        PaymentListFragment fragment = new PaymentListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);


        //TODO change this with network fetching
        args.putParcelableArrayList(ARG_TODO_ITEMS, (ArrayList)paymentItems);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            paymentItems = getArguments().getParcelableArrayList(ARG_TODO_ITEMS);
            adapter = new PaymentListAdapter(getContext(),paymentItems, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_list, container, false);


        parentActivity = ((AppCompatActivity)getActivity());
        rootView = parentActivity.findViewById(R.id.root_view);

        // Setting up the RecyclerView adapter and helpers
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
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
}
