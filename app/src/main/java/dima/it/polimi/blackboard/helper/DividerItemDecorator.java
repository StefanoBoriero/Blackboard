package dima.it.polimi.blackboard.helper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * This class draws separator between TodoItems in the RecyclerView
 * Created by Stefano on 30/11/2017.
 */

public class DividerItemDecorator extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };

    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;
    private Drawable mDivider;
    private int mOrientation;

    public DividerItemDecorator(Context context, int orientation){
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        //Gets the drawable resource at index 0
        mDivider = a.getDrawable(0);
        a.recycle();
        setOrientation(orientation);
    }

    public void setOrientation(int orientation){
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw new IllegalArgumentException("invalid orientation");
        }

        mOrientation = orientation;
    }

    /**
     * Checks the orientation of the device and calls the adequate drawing function
     * @param c Canvas to be drawn
     * @param parent RecyclerView containing the items
     * @param state TODO understand this parameter
     */
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state){
        if(mOrientation == VERTICAL_LIST){
            drawVertical(c, parent);
        }
        else{
            drawHorizontal(c, parent);
        }
    }

    /**
     * Draws the divider in a vertical fashion
     * @param c
     * @param parent
     */
    public void drawVertical(Canvas c, RecyclerView parent){
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for(int i=0; i<childCount; i++){
            //Gets the item view
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)child
                    .getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = child.getTop() + params.topMargin;

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    /**
     * Draws the divider in a horizontal fashion
     * @param c
     * @param parent
     */
    public void drawHorizontal(Canvas c, RecyclerView parent){
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    //TODO would be really nice to understand this
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){
        if (mOrientation == VERTICAL_LIST) {
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
        } else {
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
        }
    }
}
