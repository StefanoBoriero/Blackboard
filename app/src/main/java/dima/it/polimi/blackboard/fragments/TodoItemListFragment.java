package dima.it.polimi.blackboard.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.support.annotation.NonNull;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.activities.DoubleFragmentActivity;
import dima.it.polimi.blackboard.activities.HouseListActivity;
import dima.it.polimi.blackboard.adapters.FirestoreAdapter;
import dima.it.polimi.blackboard.adapters.TodoListAdapter;
import dima.it.polimi.blackboard.exceptions.AlreadyRemovedException;
import dima.it.polimi.blackboard.helper.TodoItemTouchHelper;
import dima.it.polimi.blackboard.model.TodoItem;
import dima.it.polimi.blackboard.receivers.BatteryStatusReceiver;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class TodoItemListFragment extends Fragment implements TodoListAdapter.TodoListAdapterListener,
        TodoItemTouchHelper.TodoItemTouchHelperListener, SwipeRefreshLayout.OnRefreshListener, FirestoreAdapter.OnCompleteListener{


    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String USER_ID = "user-id";
    private static final String ARG_DEFAULT_HOUSE = "house-list";
    private static final String TAG = "ITEM_LIST";
    private static final String CURRENT_SELECTED_INDEX = "current-index";
    private static final String CURRENT_HOUSE = "current-house";
    private static final String IS_DOUBLE = "is-double";

    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private FirestoreAdapter.OnCompleteListener mOnCompleteListener;
    private RecyclerView recyclerView;
    private ItemTouchHelper swipeHelper;
    private TodoListAdapter adapter;

    private View rootView;
    private View oldHighlighted;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String authId;
    private String house;
    private boolean myList;
    private Query myQuery;

    private int toHighlightIndex = 0;
    private boolean isDouble;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TodoItemListFragment() {
    }

    public static TodoItemListFragment newInstance(int columnCount, List<TodoItem> todoItems, String defaultHouse) {
        TodoItemListFragment fragment = new TodoItemListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(ARG_DEFAULT_HOUSE, defaultHouse);



        //TODO change this with network fetching
        //args.putParcelableArrayList(ARG_TODO_ITEMS, new ArrayList<>(todoItems));
        fragment.setArguments(args);
        return fragment;
    }

    public static TodoItemListFragment newInstance(int columnCount, boolean isDouble, String defaultHouse){
        TodoItemListFragment fragment = new TodoItemListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putBoolean(IS_DOUBLE, isDouble);
        args.putString(ARG_DEFAULT_HOUSE, defaultHouse);

        String authId;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            authId = user.getUid();
            args.putString(USER_ID, authId);
        }


        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            isDouble = getArguments().getBoolean(IS_DOUBLE);
            house = getArguments().getString(ARG_DEFAULT_HOUSE);
        }

        if(savedInstanceState!=null){
            toHighlightIndex = savedInstanceState.getInt(CURRENT_SELECTED_INDEX);
            house = savedInstanceState.getString(CURRENT_HOUSE);
            isDouble = savedInstanceState.getBoolean(IS_DOUBLE);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);


        AppCompatActivity parentActivity = ((AppCompatActivity)getActivity());
        if(parentActivity != null) {
            rootView = parentActivity.findViewById(R.id.root_view);
        }
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
            myList = false;
        }
        else{
            swipeMessage = getResources().getString(R.string.my_list_swipe_msg);
            myList = true;
        }
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new TodoItemTouchHelper(0,
                ItemTouchHelper.LEFT , this, swipeMessage);

        swipeHelper = new ItemTouchHelper(itemTouchHelperCallback);
        swipeHelper.attachToRecyclerView(recyclerView);

        // Setting up the refresh layout
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        String message;
        int iconId;
        if(myList){
            message = "Good job, you've done everything!\nLook for new things in your house list";
            iconId = R.drawable.ic_done_all_black_24dp;
        }
        else{
            message = "Great, everything has been taken care of!\nAdd something to do";
            iconId = R.drawable.ic_add_box_black_24dp;
        }
        View emptyMessage = view.findViewById(R.id.empty_message);
        TextView m = emptyMessage.findViewById(R.id.message);
        ImageView i = emptyMessage.findViewById(R.id.imageView6);

        m.setText(message);
        i.setImageResource(iconId);

        if(isDouble) {
            setSelectedItem(toHighlightIndex);
        }

        setHouse(house);
        return view;
    }

    //Remembering the current selected item
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(CURRENT_SELECTED_INDEX, toHighlightIndex);
        outState.putString(CURRENT_HOUSE, house);
        outState.putBoolean(IS_DOUBLE, isDouble);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        new BatteryStatusReceiver(adapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
            if(context instanceof FirestoreAdapter.OnCompleteListener){
                mOnCompleteListener = (FirestoreAdapter.OnCompleteListener) context;
            }
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

    public void setHouse(String house){
        this.house = house;
        prepareQuery();
        if(adapter == null){
            //We're initialing the fragment the first time or after rotation
            if(isDouble) {
                adapter = new TodoListAdapter(myQuery, this, mOnCompleteListener, toHighlightIndex);
            }
            else{
                adapter = new TodoListAdapter(myQuery, this, mOnCompleteListener);
            }
            recyclerView.setAdapter(adapter);
        }else{
            //We're changing the house
            oldHighlighted = null;
            toHighlightIndex = 0;
        }
        this.adapter.setQuery(myQuery);
    }

    public void setSelectedItem(int index){

        if(oldHighlighted != null){
            oldHighlighted.findViewById(R.id.selected_flag).setBackground(null);
        }else{
            oldHighlighted = recyclerView.getChildAt(toHighlightIndex);
            if(oldHighlighted != null){
                oldHighlighted.findViewById(R.id.selected_flag).setBackground(null);
            }
        }
        toHighlightIndex = index;
        View toHighlight = recyclerView.getChildAt(toHighlightIndex);
        if(toHighlight != null) {
            oldHighlighted = toHighlight;
            highlight(toHighlight);
        }
    }

    public void disableSwipe(){
        swipeHelper.attachToRecyclerView(null);
    }

    public void enableSwipe(){
        swipeHelper.attachToRecyclerView(recyclerView);
    }

    public void setSearchView(SearchView searchView){
        DoubleFragmentActivity parent = (DoubleFragmentActivity)getActivity();
        if(parent != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    adapter.getFilter().filter(query, parent);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    adapter.getFilter().filter(newText, parent);
                    return false;
                }
            });
        }
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
        mListener.onItemSwipe(position, direction);
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

    public void changeHouse(String newHouse){
        this.house = newHouse;
        prepareQuery();
        this.adapter.setQuery(myQuery);
    }

    public void setAuthId(String authId){
        this.authId = authId;
    }

    private void prepareQuery(){
        CollectionReference houseItems = db.collection("houses")
                .document(house)
                .collection("items");
        if(getActivity() instanceof HouseListActivity) {
            myQuery = houseItems.whereEqualTo("taken", false);
        }
        else{
            myQuery = houseItems.whereEqualTo("taken", true).whereEqualTo("takenBy", authId)
                .whereEqualTo("completed", false);
        }

    }

    @Override
    public void onComplete(boolean empty){
        View rootView = getView();
        if(rootView != null) {
            View emptyMessage = rootView.findViewById(R.id.empty_message);
            if(empty){
                emptyMessage.setVisibility(View.VISIBLE);
            }
            else{
                emptyMessage.setVisibility(View.INVISIBLE);
            }
       }
    }

    public void emptyFragment(){
        View rootView = getView();
        if(rootView != null) {
            View emptyMessage = rootView.findViewById(R.id.empty_message);
            emptyMessage.setVisibility(View.VISIBLE);
        }
    }

    public void fillFragment(){
        View rootView = getView();
        if(rootView != null) {
            View emptyMessage = rootView.findViewById(R.id.empty_message);
            emptyMessage.setVisibility(View.INVISIBLE);
        }
    }

    public boolean contains(TodoItem item) throws AlreadyRemovedException{
        return adapter.contains(item);
    }

    public int getRemainingItems(){
        return adapter.getItemCount();
    }

    private void highlight(View view){
        Drawable selected = getResources().getDrawable(R.color.colorAccent);
        view.findViewById(R.id.selected_flag).setBackground(selected);
    }

    public void stopListening(){
        adapter.stopListening();
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
        void onItemSwipe(int swipedPosition, int direction);
    }
}
