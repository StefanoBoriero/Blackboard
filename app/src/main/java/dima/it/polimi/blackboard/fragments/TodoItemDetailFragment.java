package dima.it.polimi.blackboard.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import dima.it.polimi.blackboard.adapters.DetailAdapter;
import dima.it.polimi.blackboard.model.TodoItem;
import dima.it.polimi.blackboard.utils.GlideApp;
import dima.it.polimi.blackboard.utils.UserDecoder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TodoItemDetailFragment.OnTodoItemDetailInteraction} interface
 * to handle interaction events.
 * Use the {@link TodoItemDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TodoItemDetailFragment extends Fragment {
    private static final int FADE_DURATION = 300;
    private static final int FADE_DELAY = 150;
    private static final String ARG_TODO = "todoItem";
    private static final String ARG_TR_NAME = "transitionName";
    private static final String ARG_TR_ICON = "transitionNameIcon";
    private static final String ARG_POS = "position";
    public static final String ACTION_TAKEN = "taken";
    public static final String ACTION_DELETED = "deleted";
    private static final String CURRENT_TASK = "current_task";
    private static final String TAG = "detail-frag";

    private TodoItem todoTask;
    private String transitionName;
    private String transitionNameIcon;
    private int position;

    private OnTodoItemDetailInteraction mListener;

    public TodoItemDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param todoItem Item which information have to be displayed.
     * @param transitionNameIcon Transition name for shared element transitions
     * @return A new instance of fragment TodoItemDetailFragment.
     */
    public static Fragment newInstance(TodoItem todoItem, String transitionNameIcon, String transitionNameName, int position) {
        TodoItemDetailFragment fragment = new TodoItemDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TODO, todoItem);
        args.putString(ARG_TR_ICON, transitionNameIcon);
        args.putString(ARG_TR_NAME, transitionNameName);
        args.putInt(ARG_POS, position);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Factory method for larger devices, where transitions names are not needed
     * @param todoItem the item to display
     * @param position the position of the item in the list
     * @return A new instance of fragment TodoItemDetailFragment.
     */
    public static Fragment newInstance(TodoItem todoItem, int position){
        TodoItemDetailFragment fragment = new TodoItemDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TODO, todoItem);
        args.putString(ARG_TR_ICON, "");
        args.putString(ARG_TR_NAME, "");
        args.putInt(ARG_POS, position);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    public static TodoItemDetailFragment newInstance(){
        return new TodoItemDetailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            todoTask = savedInstanceState.getParcelable(CURRENT_TASK);
        }
        if (getArguments() != null) {
            todoTask = getArguments().getParcelable(ARG_TODO);
            transitionNameIcon = getArguments().getString(ARG_TR_ICON);
            transitionName = getArguments().getString(ARG_TR_NAME);
            position = getArguments().getInt(ARG_POS);
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(CURRENT_TASK, todoTask);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(todoTask != null) {
            RecyclerView rv = view.findViewById(R.id.recycler_view_detail);
            populateRecyclerView(rv);

            TextView nameView = view.findViewById(R.id.item_name);
            nameView.setText(todoTask.getName());
            nameView.setTransitionName(transitionName);

            TextView addedBy = view.findViewById(R.id.added_by);
            String username = UserDecoder.getInstance().getNameFromId(todoTask.getCreatedBy());
            String s = "Added by " + username;
            addedBy.setText(s);


            // Binds dynamically the shared element through transition name
            ImageView userIconView = view.findViewById(R.id.user_icon);
            userIconView.setTransitionName(transitionNameIcon);

            Context parentActivity = getActivity();
            if (parentActivity != null) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                DocumentReference userReference = FirebaseFirestore.getInstance().collection("users").document(todoTask.getCreatedBy());
                userReference.get().addOnCompleteListener((task) -> {
                    List<CharSequence> myHouses = new ArrayList<>();
                    if (task.isSuccessful()) {
                        {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Map<String, Object> userParam = document.getData();
                                String lastEdit = (String) userParam.get("lastEdit");
                                StorageReference reference = storage.getReference().child(todoTask.getCreatedBy() + "/profile" + lastEdit);
                                GlideApp.with(this)
                                        .load(reference)
                                        .error(R.drawable.empty_profile_blue_circle)
                                        .apply(RequestOptions.circleCropTransform())
                                        .into(userIconView);
                            }
                        }

                    } else {
                        //TODO display error message
                    }
                });
            }
            //Binds the button click action to parent activity
            View acceptBtn = view.findViewById(R.id.accept_button);
            acceptBtn.setOnClickListener((v) ->
                    mListener.onAcceptClick(todoTask, position, ACTION_TAKEN)
            );

            View deleteBtn = view.findViewById(R.id.delete);
            deleteBtn.setOnClickListener((v) ->
                    mListener.onAcceptClick(todoTask, position, ACTION_DELETED));
        }
        else{
            emptyFragment();
        }
    }

    /**
     * Populates the recyclerView with the current content
     * @param recyclerView the recycler to be populated
     */
    private void populateRecyclerView(RecyclerView recyclerView){
        Context context = this.getActivity();
        if(context != null){
            if(todoTask != null) {
                RecyclerView.Adapter adapter = new DetailAdapter(todoTask.getDetails());
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
                recyclerView.setAdapter(adapter);
            }
        }
    }

    /**
     * Fades out the RecyclerView and calls the update method when animation finishes
     * @param recyclerView the recycler to be updated
     */
    private void updateRecyclerView(RecyclerView recyclerView){
        recyclerView.animate()
                .alpha(0f)
                .setDuration(FADE_DURATION)
                .withEndAction(()-> showRecyclerView(recyclerView))
                .start();
    }

    /**
     * Updates the content of the RecyclerView and shows it
     * @param recyclerView the recycler to be updated
     */
    private void showRecyclerView(RecyclerView recyclerView){
        populateRecyclerView(recyclerView);
        recyclerView.animate()
                .alpha(1f)
                .setDuration(FADE_DURATION)
                .setStartDelay(FADE_DELAY)
                .start();
    }



    //TODO extract this method in Utility class OR put iconResId in todoItem
    private Drawable resolveIcon(String type){
        Resources res = getResources();
        switch (type) {
            case ("Housing"):
                return res.getDrawable(R.drawable.ic_home_black_24dp);
            case("Billing"):
                return res.getDrawable(R.drawable.ic_payment_black_24dp);
                /*
            case ("Shopping"):
                return res.getDrawable(R.drawable.ic_shopping_cart_black_24dp);
                */
            default:
                return res.getDrawable(R.drawable.ic_shopping_cart_black_24dp);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTodoItemDetailInteraction) {
            mListener = (OnTodoItemDetailInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTodoItemDetailInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This method is called when the device is a large one. It updates the content of the detail
     * fragment by fading out the RecyclerView, changing its content and fading it in back
     * @param todoItem the new item which details are to be shown
     * @param position the position of the new item
     */
    public void updateFragment(TodoItem todoItem, int position){
        if(todoTask != null){
            if(todoItem.getId().equals(todoTask.getId())){
                //We are already showing the correct information
                return;
            }
        }
        View rootView = getView();
        todoTask = todoItem;
        this.position = position;

        if(rootView != null) {
            fillFragment();
            Log.d(TAG, "Updating the content of detail fragment");
            rootView.findViewById(R.id.content).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.empty_message).setVisibility(View.INVISIBLE);
            TextView nameView = rootView.findViewById(R.id.item_name);
            nameView.setText(todoItem.getName());

            TextView addedBy = rootView.findViewById(R.id.added_by);
            String username = UserDecoder.getInstance().getNameFromId(todoItem.getCreatedBy());
            String s = "Added by " + username;
            addedBy.setText(s);

            RecyclerView rv = rootView.findViewById(R.id.recycler_view_detail);
            updateRecyclerView(rv);

            //Binds the button click action to parent activity
            View acceptBtn = rootView.findViewById(R.id.accept_button);
            acceptBtn.setOnClickListener((v) ->
                    mListener.onAcceptClick(todoTask, position, ACTION_TAKEN)
            );

            View deleteBtn = rootView.findViewById(R.id.delete);
            deleteBtn.setOnClickListener((v) ->
                    mListener.onAcceptClick(todoTask, position, ACTION_DELETED));

            ImageView userIconView = rootView.findViewById(R.id.user_icon);

            Context parentActivity = getActivity();
            if(parentActivity != null) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                DocumentReference userReference = FirebaseFirestore.getInstance().collection("users").document(todoTask.getCreatedBy());
                userReference.get().addOnCompleteListener((task) -> {
                    List<CharSequence> myHouses = new ArrayList<>();
                    if (task.isSuccessful()) {
                        {
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()){
                            Map<String, Object> userParam = document.getData();
                            String lastEdit = (String)userParam.get("lastEdit");
                            StorageReference reference = storage.getReference().child(todoTask.getCreatedBy() + "/profile" + lastEdit);
                            GlideApp.with( this)
                                    .load(reference)
                                    .error(R.drawable.empty_profile_blue_circle)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(userIconView);
                            }
                        }

                    } else {
                        //TODO display error message
                    }
                });
            }
        }
    }

    /**
     * Shows a message if there's no item to show details
     */
    public void emptyFragment(){
        Log.d(TAG, "Emptying the detail view");
        View view = getView();
        if(view != null){
            view.findViewById(R.id.content).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.empty_message).setVisibility(View.VISIBLE);
        }
    }

    public void fillFragment(){
        View view = getView();
        if(view != null){
            view.findViewById(R.id.content).setVisibility(View.VISIBLE);
            view.findViewById(R.id.empty_message).setVisibility(View.INVISIBLE);
        }
    }

    public int getPosition(){
        return position;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnTodoItemDetailInteraction {

        /**
         * The user accepted the item
         * @param todoItem the item accepted
         */
        void onAcceptClick(TodoItem todoItem, int position, String action);
    }
}
