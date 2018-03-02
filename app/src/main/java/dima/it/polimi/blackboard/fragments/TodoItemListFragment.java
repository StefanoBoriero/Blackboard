package dima.it.polimi.blackboard.fragments;

import android.content.Context;
import android.os.Bundle;

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
import dima.it.polimi.blackboard.activities.HouseListActivity;
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
        TodoItemTouchHelper.TodoItemTouchHelperListener, SwipeRefreshLayout.OnRefreshListener{


    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_TODO_ITEMS = "todo-items";

    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private ItemTouchHelper swipeHelper;
    private TodoListAdapter adapter;


    private View rootView;

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
        args.putParcelableArrayList(ARG_TODO_ITEMS, new ArrayList<>(todoItems));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            List<TodoItem> todoItems = getArguments().getParcelableArrayList(ARG_TODO_ITEMS);
            adapter = new TodoListAdapter(todoItems, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);


        AppCompatActivity parentActivity = ((AppCompatActivity)getActivity());
        rootView = parentActivity.findViewById(R.id.root_view);

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
        String swipeMessage;
        if(parentActivity instanceof HouseListActivity){
            swipeMessage = getResources().getString(R.string.house_list_swipe_msg);
        }
        else{
            swipeMessage = getResources().getString(R.string.my_list_swipe_msg);
        }
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new TodoItemTouchHelper(0,
                ItemTouchHelper.LEFT, this, swipeMessage);
        swipeHelper = new ItemTouchHelper(itemTouchHelperCallback);
        swipeHelper.attachToRecyclerView(recyclerView);

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
    public void onTodoItemClicked(TodoItem todoItem, View view, int clickedPosition) {
        mListener.onItemClick(todoItem, view, clickedPosition);
    }

    public void disableSwipe(){
        swipeHelper.attachToRecyclerView(null);
    }

    public void enableSwipe(){
        swipeHelper.attachToRecyclerView(recyclerView);
    }

    public void setSearchView(SearchView searchView){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    /**
     * Implementation of the interface to delegate swap.
     * It removes the item from the list, giving opportunity for undo the operation through a
     * Snackbar message
     * @param viewHolder the view holder that has been swiped
     * @param direction  the direction of the swipe
     * @param position   the position of the item in the list filtered
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        mListener.onItemSwipe(position);
    }

    /**
     * Removes an item from the adapter
     * @param position position of the item to remove
     * @return the item removed
     */
    public TodoItem removeItem(int position){
        final TodoItem removedItem = adapter.getItem(position);
        adapter.removeItem(position);
        return removedItem;
    }

    /**
     * Inserts an item at a given position
     * @param todoItem the item to insert
     * @param position the position where to insert
     */
    public void insertItem(TodoItem todoItem, int position){
        //TODO change this rootView element
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        int itemCount = adapter.getItemCount();
        adapter.insertItem(todoItem, position);
        if(position == 0 || position == itemCount){
            recyclerView.scrollToPosition(position);
        }
    }

    public TodoItem getItem(int position){
        // todo throw finished items exception
        return adapter.getItem(position);
    }

    /**
     * Returns the view of the row at the given position
     * @param position the position in the list
     * @return the view of the item at the given position
     */
    public View getViewHolder(int position){
        return recyclerView.getLayoutManager().getChildAt(position);
    }

    @Override
    public void onRefresh() {
        //TODO implement refreshing through Firebase. Add setter for network source
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
        void onItemClick(TodoItem item, View view, int clickedPosition);
        void onItemSwipe(int swipedPosition);
    }
}
