package dima.it.polimi.blackboard.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.adapters.TodoListAdapter;
import dima.it.polimi.blackboard.helper.TodoItemTouchHelper;
import dima.it.polimi.blackboard.model.TodoItem;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class TodoItemListFragment extends Fragment implements ToDoTaskDetailFragment.OnFragmentInteractionListener{

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private TodoListAdapter.TodoListAdapterListener mListener;
    private TodoItemTouchHelper.TodoItemTouchHelperListener swipeListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TodoItemListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static TodoItemListFragment newInstance(int columnCount) {
        TodoItemListFragment fragment = new TodoItemListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_group_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        //TODO eliminate the following lines
        List<TodoItem> items = new ArrayList<>();
        for(int i=0; i<31; i++){
            TodoItem item = new TodoItem("Clean " + i,
                    "Housing", "The house has to be cleaned");
            items.add(item);
        }

        Context context = view.getContext();
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        recyclerView.setAdapter(new TodoListAdapter(getContext(),items, mListener));

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new TodoItemTouchHelper(0,
                ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT, swipeListener
        );
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        /*TODO fetch data on swipe refresh
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
         swipeRefreshLayout.setOnRefreshListener(this);
        */

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TodoListAdapter.TodoListAdapterListener) {
            mListener = (TodoListAdapter.TodoListAdapterListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TodoListAdapterListener");
        }

        if (context instanceof TodoItemTouchHelper.TodoItemTouchHelperListener) {
            swipeListener = (TodoItemTouchHelper.TodoItemTouchHelperListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TodoItemTouchHelperListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     *
     * @param uri
     */
    @Override
    public void onFragmentInteraction(Uri uri) {

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
        // TODO: Update argument type and name
        void onListFragmentInteraction(TodoItem item, View view);
    }
}
