package dima.it.polimi.blackboard.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.model.TodoItem;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ToDoTaskDetailFragment.OnTodoItemDetailInteraction} interface
 * to handle interaction events.
 * Use the {@link ToDoTaskDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ToDoTaskDetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TODO = "todoItem";
    private static final String ARG_TR_NAME = "transitionName";
    private static final String ARG_TR_ICON = "transitionNameIcon";


    private TodoItem todoTask;
    private String transitionName;
    private String transitionNameIcon;

    private OnTodoItemDetailInteraction mListener;

    public ToDoTaskDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param todoTask Item which information have to be displayed.
     * @param transitionName Transition name to set the shared element transition.
     * @return A new instance of fragment ToDoTaskDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment newInstance(TodoItem todoTask, String transitionName, String transitionNameIcon) {
        ToDoTaskDetailFragment fragment = new ToDoTaskDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TODO, todoTask);
        args.putString(ARG_TR_NAME, transitionName);
        args.putString(ARG_TR_ICON, transitionNameIcon);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            todoTask = getArguments().getParcelable(ARG_TODO);
            transitionName = getArguments().getString(ARG_TR_NAME);
            transitionNameIcon = getArguments().getString(ARG_TR_ICON);
        }

        //postponeEnterTransition();
        Transition enterTransition = TransitionInflater.from(getContext())
                .inflateTransition(R.transition.detail_transition);
        setSharedElementEnterTransition(enterTransition);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View out =  inflater.inflate(R.layout.fragment_todo_task_detail, container, false);

        return out;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Gets the text views to populate them
        ImageView userIconView = view.findViewById(R.id.user_icon);
        TextView titleView = view.findViewById(R.id.title_text_view);
        TextView typeView = view.findViewById(R.id.type_text_view);
        TextView descriptionView = view.findViewById(R.id.description_text_view);

        titleView.setText(todoTask.getName());
        typeView.setText(todoTask.getType());
        descriptionView.setText(todoTask.getDescription());

        // Binds dynamically the shared element through transition name
        titleView.setTransitionName(transitionName);
        userIconView.setTransitionName(transitionNameIcon);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTodoItemDetailInteraction) {
            mListener = (OnTodoItemDetailInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
