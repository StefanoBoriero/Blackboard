package dima.it.polimi.blackboard.adapters;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
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
    private List<TodoItem> todoItems;
    private List<TodoItem> todoItemsFiltered;
    private TodoListAdapterListener mListener;
    private ViewGroup parent;

    public TodoListAdapter(List<TodoItem> todoItems, TodoListAdapterListener listener){
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
        holder.timestampView.setText(todoItem.getTimestamp());
        holder.todoItemType.setCompoundDrawablesWithIntrinsicBounds(resolveIcon(todoItem.getType()), null, null, null);
        //TODO get user icon


        holder.itemView.setOnClickListener((v) ->
                mListener.onTodoItemClicked(todoItem, v, holder.getAdapterPosition())
        );
    }

    //TODO extract this method in Utility class OR put iconResId in todoItem
    private Drawable resolveIcon(String type){
        Resources res = parent.getResources();
        switch (type) {
            case ("Housing"):
                    return res.getDrawable(R.drawable.ic_home_black_24dp);
            case("Billing"):
                    return res.getDrawable(R.drawable.ic_payment_black_24dp);
            case ("Shopping"):
                    return res.getDrawable(R.drawable.ic_shopping_cart_black_24dp);
            default:
                return null;
        }
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
    @SuppressWarnings("unchecked")
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
        private TextView timestampView;
        public ConstraintLayout viewForeground;
        public ConstraintLayout viewBackground;

        private ViewHolder(View itemView) {
            super(itemView);
            viewForeground = itemView.findViewById(R.id.foreground_row);
            viewBackground = itemView.findViewById(R.id.background_row);
            todoItemName = itemView.findViewById(R.id.item_name);
            todoItemType = itemView.findViewById(R.id.item_type);
            userIcon = itemView.findViewById(R.id.user_icon);
            timestampView = itemView.findViewById(R.id.timestamp);
        }
    }
}
