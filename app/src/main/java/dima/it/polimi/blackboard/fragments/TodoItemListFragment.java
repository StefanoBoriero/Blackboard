package dima.it.polimi.blackboard.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
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
public class TodoItemListFragment extends Fragment implements TodoListAdapter.TodoListAdapterListener,
        TodoItemTouchHelper.TodoItemTouchHelperListener{


    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_TODO_ITEMS = "todo-items";

    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private TodoListAdapter adapter;
    private List<TodoItem> todoItems;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TodoItemListFragment() {
    }

    public static TodoItemListFragment newInstance(int columnCount, List<TodoItem> todoItems) {
        TodoItemListFragment fragment = new TodoItemListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);

        //TODO change this with network fetching
        args.putParcelableArrayList(ARG_TODO_ITEMS, (ArrayList)todoItems);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            todoItems = getArguments().getParcelableArrayList(ARG_TODO_ITEMS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_group_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        Context context = view.getContext();
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        adapter = new TodoListAdapter(getContext(),todoItems, this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getActivity(), LinearLayoutManager.VERTICAL));
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new TodoItemTouchHelper(0,
                ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT, this);
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
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TodoListAdapterListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public TodoListAdapter getAdapter(){
        return this.adapter;
    }


    @Override
    public void onTodoItemClicked(TodoItem todoItem, View view) {
        mListener.onItemClick(todoItem, view);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        mListener.onSwiped(viewHolder, direction, position);
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
        void onItemClick(TodoItem item, View view);
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }
}
