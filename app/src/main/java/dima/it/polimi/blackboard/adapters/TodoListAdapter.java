package dima.it.polimi.blackboard.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.model.TodoItem;

/**
 *
 * Created by Stefano on 30/11/2017.
 */

public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.ViewHolder> {
    private Context mContext;
    private List<TodoItem> todoItems;
    private TodoListAdapterListener mListener;
    private ViewGroup parent;

    public TodoListAdapter(Context context, List<TodoItem> todoItems, TodoListAdapterListener listener){
        this.mContext = context;
        this.todoItems = todoItems;
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todo_item_row, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final TodoItem todoItem = todoItems.get(position);

        holder.todoItemName.setText(todoItem.getName());
        holder.todoItemType.setText(todoItem.getType());
        holder.todoItemName.setTransitionName(todoItem.getName());
        holder.userIcon.setTransitionName(todoItem.getName() + "Icon");
        //TODO get user icon and todoItem timestamp


        holder.itemView.findViewById(R.id.foreground_row)
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onTodoItemClicked(todoItem, v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return todoItems.size();
    }

    public void removeItem(int position){
        todoItems.remove(position);
        notifyItemRemoved(position);
    }

    public void insertItem(TodoItem item, int position){
        todoItems.add(position, item);
        notifyItemInserted(position);
    }

    public interface TodoListAdapterListener {
        void onTodoItemClicked(TodoItem todoItem, View todoItemView);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView todoItemName;
        private TextView todoItemType;
        private ImageView userIcon;
        private LinearLayout todoItemContainer;
        private RelativeLayout userIconContainer;

        public RelativeLayout viewForeground;
        public RelativeLayout viewBackground;

        public ViewHolder(View itemView) {
            super(itemView);
            viewForeground = itemView.findViewById(R.id.foreground_row);
            viewBackground = itemView.findViewById(R.id.background_row);
            todoItemName = itemView.findViewById(R.id.item_name);
            todoItemType = itemView.findViewById(R.id.item_type);
            userIcon = itemView.findViewById(R.id.user_icon);
            todoItemContainer = itemView.findViewById(R.id.todo_item_container);
            userIconContainer = itemView.findViewById(R.id.user_icon_container);
        }
    }
}
