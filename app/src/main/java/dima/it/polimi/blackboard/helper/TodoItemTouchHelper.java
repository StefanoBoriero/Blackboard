package dima.it.polimi.blackboard.helper;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;


import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.adapters.TodoListAdapter;

/**
 * This class handles the swipe movement for items in the item list
 * Created by Stefano on 01/12/2017.
 */

public class TodoItemTouchHelper extends ItemTouchHelper.SimpleCallback {
    private TodoItemTouchHelperListener listener;
    private String msg;

    public TodoItemTouchHelper(int dragDirs, int swipeDirs, TodoItemTouchHelperListener listener, String msg) {
        super(dragDirs, swipeDirs);

        this.msg = msg;
        this.listener = listener;
    }
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    /*
    Method called at the beginning of an interaction with an item
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            final View foregroundView = ((TodoListAdapter.ViewHolder) viewHolder).viewForeground;

            // Set the hidden background to VISIBLE. The background is by default set to INVISIBLE for visual transition issues
            final View backgroundView = ((TodoListAdapter.ViewHolder) viewHolder).viewBackground;
            backgroundView.setVisibility(View.VISIBLE);
            TextView swipeMessageView = backgroundView.findViewById(R.id.background_swipe_message);
            swipeMessageView.setText(msg);

            getDefaultUIUtil().onSelected(foregroundView);
        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
        final View foregroundView = ((TodoListAdapter.ViewHolder) viewHolder).viewForeground;
        getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive);
    }

    /*
    Method called at the end of interaction with an item
     */
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final View foregroundView = ((TodoListAdapter.ViewHolder) viewHolder).viewForeground;

        // Set the visibility of the background back to INVISIBLE
        final View backgroundView = ((TodoListAdapter.ViewHolder) viewHolder).viewBackground;
        backgroundView.setVisibility(View.INVISIBLE);
        getDefaultUIUtil().clearView(foregroundView);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        final View foregroundView = ((TodoListAdapter.ViewHolder) viewHolder).viewForeground;

        getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
    }

    /**
     * Interface to delegate the consequences of a complete swipe
     */
    public interface TodoItemTouchHelperListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }
}