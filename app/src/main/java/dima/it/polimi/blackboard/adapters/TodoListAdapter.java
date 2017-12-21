package dima.it.polimi.blackboard.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.model.TodoItem;

/**
 * Adapter class for a list of todoItems
 * Created by Stefano on 30/11/2017.
 */

public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.ViewHolder>
    implements Filterable{
    private Context mContext;
    private List<TodoItem> todoItems;
    private List<TodoItem> todoItemsFiltered;
    private TodoListAdapterListener mListener;
    private ViewGroup parent;

    public TodoListAdapter(Context context, List<TodoItem> todoItems, TodoListAdapterListener listener){
        this.mContext = context;
        this.todoItems = todoItems;
        this.todoItemsFiltered = todoItems;
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
        final TodoItem todoItem = todoItemsFiltered.get(position);

        holder.todoItemName.setText(todoItem.getName());
        holder.todoItemType.setText(todoItem.getType());
        holder.todoItemName.setTransitionName(todoItem.getName() + "Name" + todoItem.getId());
        holder.userIcon.setTransitionName(todoItem.getName() + "Icon" + todoItem.getId());
        //TODO get user icon and todoItem timestamp


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onTodoItemClicked(todoItem, v, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return todoItemsFiltered.size();
    }

    public TodoItem getItem(int position){
        return todoItemsFiltered.get(position);
    }
    public void removeItem(int position){
        todoItemsFiltered.remove(position);
        notifyItemRemoved(position);
    }

    public void insertItem(TodoItem item, int position){
        todoItemsFiltered.add(position, item);
        notifyItemInserted(position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String expression = constraint.toString();
                if(expression.isEmpty()){
                    todoItemsFiltered = todoItems;
                }
                else {
                    List<TodoItem> filteredItems = new ArrayList<>();
                    for (TodoItem item : todoItems) {
                        if(item.getName().toLowerCase().contains(expression.toLowerCase())
                                || item.getType().toLowerCase().contains(expression.toLowerCase())){
                            filteredItems.add(item);
                        }
                    }
                    todoItemsFiltered = filteredItems;
                }
                FilterResults results = new FilterResults();
                results.values = todoItemsFiltered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                todoItemsFiltered = (List<TodoItem>)results.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface TodoListAdapterListener {
        void onTodoItemClicked(TodoItem todoItem, View todoItemView, int clickedPosition);
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
