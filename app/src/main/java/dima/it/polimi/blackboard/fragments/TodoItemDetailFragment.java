package dima.it.polimi.blackboard.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.model.TodoItem;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TodoItemDetailFragment.OnTodoItemDetailInteraction} interface
 * to handle interaction events.
 * Use the {@link TodoItemDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TodoItemDetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TODO = "todoItem";
    private static final String ARG_TR_NAME = "transitionName";
    private static final String ARG_TR_ICON = "transitionNameIcon";


    private TodoItem todoTask;
    private String transitionName;
    private String transitionNameIcon;

    private OnTodoItemDetailInteraction mListener;

    public TodoItemDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param todoItem Item which information have to be displayed.
     * @return A new instance of fragment TodoItemDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment newInstance(TodoItem todoItem, String transitionNameIcon) {
        TodoItemDetailFragment fragment = new TodoItemDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TODO, todoItem);
        args.putString(ARG_TR_ICON, transitionNameIcon);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            todoTask = getArguments().getParcelable(ARG_TODO);
            transitionNameIcon = getArguments().getString(ARG_TR_ICON);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View out =  inflater.inflate(R.layout.content_todo_item_detail, container, false);
        return out;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Gets the text views to populate them
        TextView descriptionView = view.findViewById(R.id.item_description);
        descriptionView.setText(todoTask.getDescription());

        // Binds dynamically the shared element through transition name
        //titleView.setTransitionName(transitionName);
        View userIconView = view.findViewById(R.id.user_icon);
        userIconView.setTransitionName(transitionNameIcon);

        //Binds the button click action to parent activity
        Button acceptBtn = view.findViewById(R.id.accept_button);
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onAcceptClick(todoTask);
            }
        });
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
        void onAcceptClick(TodoItem todoItem);

        /**
         * Closes the detail screen
         */
        void onCloseClick();
    }
}
