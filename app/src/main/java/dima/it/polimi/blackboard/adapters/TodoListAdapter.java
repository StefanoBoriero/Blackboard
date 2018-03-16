package dima.it.polimi.blackboard.adapters;

import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.fragments.TodoItemListFragment;
import dima.it.polimi.blackboard.model.TodoItem;
import dima.it.polimi.blackboard.utils.GlideApp;

/**
 * Adapter class for a list of todoItems
 * Created by Stefano on 30/11/2017.
 */

public class TodoListAdapter extends FirestoreAdapter<TodoListAdapter.ViewHolder>/*RecyclerView.Adapter<TodoListAdapter.ViewHolder>*/
    {
    private TodoListAdapterListener mListener;
    private ViewGroup parent;
    private int initialHighlighted;
    private boolean isDouble;

    public TodoListAdapter(Query query, TodoListAdapterListener listener, OnCompleteListener completeListener){
        super(query, completeListener);
        this.mListener = listener;
        this.isDouble = false;
    }

    public TodoListAdapter(Query query, TodoListAdapterListener listener, OnCompleteListener completeListener, int initialHighlighted){
        super(query, completeListener);
        this.mListener = listener;
        this.initialHighlighted = initialHighlighted;
        this.isDouble = true;
        }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todo_item_row, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final TodoItem todoItem = getFilteredSnapshot(position).toObject(TodoItem.class);
        holder.itemView.setId(position);
        holder.todoItemName.setText(todoItem.getName());
        holder.todoItemType.setText(todoItem.getType());
        holder.todoItemName.setTransitionName(todoItem.getName() + "Name" + todoItem.getId());
        holder.userIcon.setTransitionName(todoItem.getName() + "Icon" + todoItem.getId());
        DateFormat df = new SimpleDateFormat("MMM dd", Locale.US);
        holder.timestampView.setText(df.format(todoItem.getCreatedOn()));
        holder.todoItemType.setCompoundDrawablesWithIntrinsicBounds(resolveIcon(todoItem.getType()), null, null, null);
        checkSuggestion(todoItem, holder.suggestionStar);

        if(isDouble) {
            if (position == initialHighlighted) {
                View toHighlight = holder.itemView;
                Drawable selected = new ColorDrawable(0xFF448AFF);
                toHighlight.findViewById(R.id.selected_flag).setBackground(selected);
            }
            else{
                View toNotHighlight = holder.itemView;
                toNotHighlight.findViewById(R.id.selected_flag).setBackground(null);

            }
        }

        // TODO: 11/03/2018 Refactor this code to get the correct image 
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference().child(todoItem.getCreatedBy() + "/profile.jpg");
        GlideApp.with( (TodoItemListFragment)mListener)
                .load(reference)
                .error(R.drawable.empty_profile_blue_circle)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.userIcon);

        holder.itemView.setOnClickListener((v) ->
                mListener.onTodoItemClicked(todoItem, v, holder.getAdapterPosition())
        );
    }

    private void checkSuggestion(TodoItem item, ImageView suggestionStar){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            String uid = user.getUid();
            String suggestedTo = item.getSuggestedTo();
            if(uid.equals(suggestedTo)){
                suggestionStar.setImageResource(R.drawable.ic_star_yellow_24dp);
            }
            else{
                suggestionStar.setImageResource(R.drawable.ic_star_border_black_24dp);
            }
        }
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
        TodoItem item;
        try{
            item = getFilteredSnapshot(position).toObject(TodoItem.class);
        }
        catch (IndexOutOfBoundsException e){
            return null;
        }
        return item;
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
        private ImageView suggestionStar;
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
            suggestionStar = itemView.findViewById(R.id.suggestion_star);
        }
    }
}
