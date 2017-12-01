package dima.it.polimi.blackboard.helper;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * This class handles the swipe movement for items in the item list
 * Created by Stefano on 01/12/2017.
 */

public class TodoItemTouchHelper extends ItemTouchHelper.SimpleCallback {
    private TodoItemTouchHelperListener listener;

    public TodoItemTouchHelper(int dragDirs, int swipeDirs, TodoItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);

        this.listener = listener;
    }
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
    }

    public interface TodoItemTouchHelperListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }
}
