package dima.it.polimi.blackboard.adapters;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.Query;

import java.util.List;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.model.TodoItem;

/**
 * Adapter class for a list of todoItems
 * Created by Stefano on 30/11/2017.
 */

public class TodoListAdapter extends FirestoreAdapter<TodoListAdapter.ViewHolder>/*RecyclerView.Adapter<TodoListAdapter.ViewHolder>*/
    {
    private TodoListAdapterListener mListener;
    private ViewGroup parent;

    public TodoListAdapter(Query query, TodoListAdapterListener listener){
        super(query);
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
        final TodoItem todoItem = getFilteredSnapshot(position).toObject(TodoItem.class);
        holder.todoItemName.setText(todoItem.getName());
        holder.todoItemType.setText(todoItem.getType());
        holder.todoItemName.setTransitionName(todoItem.getName() + "Name" + todoItem.getId());
        holder.userIcon.setTransitionName(todoItem.getName() + "Icon" + todoItem.getId());
        holder.timestampView.setText(todoItem.getMyCreatedOn());
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
            case ("Housekeeping"):
                    return res.getDrawable(R.drawable.ic_home_black_24dp);
            case("Bill"):
                    return res.getDrawable(R.drawable.ic_payment_black_24dp);
            case ("Shopping"):
                    return res.getDrawable(R.drawable.ic_shopping_cart_black_24dp);
            default:
                return null;
        }
    }

    public TodoItem getItem(int position){
        return getFilteredSnapshot(position).toObject(TodoItem.class);
    }

    public void insertItem(TodoItem item, int position){
       super.insertItem(position);
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
