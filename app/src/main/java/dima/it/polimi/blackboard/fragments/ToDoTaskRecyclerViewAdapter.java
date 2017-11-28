package dima.it.polimi.blackboard.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.fragments.ToDoTaskListFragment.OnListFragmentInteractionListener;
import dima.it.polimi.blackboard.model.ToDoTaskParcelable;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ToDoTaskParcelable} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ToDoTaskRecyclerViewAdapter extends RecyclerView.Adapter<ToDoTaskRecyclerViewAdapter.ViewHolder> {

    private final List<ToDoTaskParcelable> todoList;
    private final OnListFragmentInteractionListener mListener;

    public ToDoTaskRecyclerViewAdapter(List<ToDoTaskParcelable> items, OnListFragmentInteractionListener listener) {
        todoList = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_todo_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ToDoTaskParcelable todoTask = todoList.get(position);

        holder.mItem = todoTask;
        holder.nameView.setText(todoTask.getName());
        holder.typeView.setText(todoTask.getType());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private final TextView nameView;
        private final TextView typeView;
        private ToDoTaskParcelable mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            nameView = view.findViewById(R.id.task_name);
            typeView = view.findViewById(R.id.task_type);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + typeView.getText() + "'";
        }
    }
}
