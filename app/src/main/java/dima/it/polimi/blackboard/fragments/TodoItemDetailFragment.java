package dima.it.polimi.blackboard.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.model.Detail;
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
    private static final String ARG_TODO = "todoItem";
    private static final String ARG_TR_NAME = "transitionName";
    private static final String ARG_TR_ICON = "transitionNameIcon";
    private static final String ARG_POS = "position";



    private TodoItem todoTask;
    private String transitionName;
    private String transitionNameIcon;
    private int position;

    private View rootView;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            todoTask = getArguments().getParcelable(ARG_TODO);
            transitionNameIcon = getArguments().getString(ARG_TR_ICON);
            transitionName = getArguments().getString(ARG_TR_NAME);
            position = getArguments().getInt(ARG_POS);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        LinearLayout detailList = view.findViewById(R.id.content_detail);
        populateDetailList(detailList, inflater);

        return view;
    }

    private void populateDetailList(LinearLayout layout, LayoutInflater inflater){
        List<Detail> details = todoTask.getDetails();

        for(Detail d: details){
            Drawable icon = getResources().getDrawable(d.getIconResId());
            String content = d.getContent();
            TextView row = (TextView)inflater.inflate(R.layout.detail_row, layout, false);
            row.setText(content);
            row.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
            layout.addView(row);
        }
    }

    private void clearDetails(LinearLayout layout){
        layout.removeAllViews();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;
/*
        // Gets the text views to populate them
        TextView descriptionView = view.findViewById(R.id.description);
        descriptionView.setText(todoTask.getDescription());
*/
        TextView nameView = view.findViewById(R.id.item_name);
        nameView.setText(todoTask.getName());
        nameView.setTransitionName(transitionName);

        // Binds dynamically the shared element through transition name
        //titleView.setTransitionName(transitionName);
        View userIconView = view.findViewById(R.id.user_icon);
        userIconView.setTransitionName(transitionNameIcon);

        //Binds the button click action to parent activity
        View acceptBtn = view.findViewById(R.id.accept_button);
        acceptBtn.setOnClickListener((v)->
                mListener.onAcceptClick(todoTask, position)
        );
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

    public void updateFragment(TodoItem todoItem, int position){
        View rootView = getView();
        todoTask = todoItem;
        this.position = position;

        if(rootView != null) {
            LinearLayout layout = getView().findViewById(R.id.content_detail);
            clearDetails(layout);
            populateDetailList(layout, getLayoutInflater());
            /*
            TextView descriptionView = rootView.findViewById(R.id.description);
            descriptionView.setText(todoItem.getDescription());
            */
            TextView nameView = rootView.findViewById(R.id.item_name);
            nameView.setText(todoItem.getName());


            //Binds the button click action to parent activity
            View acceptBtn = rootView.findViewById(R.id.accept_button);
            acceptBtn.setOnClickListener((v) ->
                    mListener.onAcceptClick(todoTask, position)
            );
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
        void onAcceptClick(TodoItem todoItem, int position);
    }
}
