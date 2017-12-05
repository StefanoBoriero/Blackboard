package dima.it.polimi.blackboard.helper;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.fragments.TodoItemDetailFragment;
import dima.it.polimi.blackboard.model.TodoItem;

/**
 * This class helps showing the details of a todoItem animating it
 * Created by Stefano on 04/12/2017.
 */

public class TodoDetailAnimatorHelper {
    private ObjectAnimator animatorExpand;
    private AnimatorSet animatorCollapse;
    private final int startingHeight;
    private final int finalHeight;
    private final float startingY;
    private final Context mContext;
    private final EndAnimationListener mListener;

    public TodoDetailAnimatorHelper(final View target, View parent, Context context, final TodoItem item,
                                    EndAnimationListener listener){
        startingHeight = target.getMeasuredHeight();
        finalHeight = parent.getMeasuredHeight();
        startingY = target.getY();
        mContext = context;
        mListener = listener;

        final Float targetElevation = context.getResources().getDimension(R.dimen.on_reveal_elevation);
        final Integer duration = context.getResources().getInteger(R.integer.on_reveal_duration);

        animatorExpand = ObjectAnimator.ofFloat(target, "elevation", targetElevation);
        animatorExpand.setDuration(duration);
        animatorExpand.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ValueAnimator expander = ValueAnimator.ofInt(startingHeight, finalHeight);
                expander.setDuration(duration);
                expander.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Integer value = (Integer)animation.getAnimatedValue();
                        target.getLayoutParams().height = value;
                        target.requestLayout();
                    }
                });

                ObjectAnimator translator = ObjectAnimator.ofFloat(target, "y", 0);
                translator.setDuration(duration);


                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(expander, translator);
                animatorSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mListener.onAnimationEnded(item);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                animatorSet.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


        ValueAnimator shrinker = ValueAnimator.ofInt(finalHeight, startingHeight);
        shrinker.setDuration(duration);
        shrinker.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer)animation.getAnimatedValue();
                target.getLayoutParams().height = value;
                target.requestLayout();
            }
        });

        ObjectAnimator translator = ObjectAnimator.ofFloat(target, "y", startingY);
        translator.setDuration(duration);


        animatorCollapse = new AnimatorSet();
        animatorCollapse.playTogether(shrinker, translator);
        animatorCollapse.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator flattener = ObjectAnimator.ofFloat(target, "elevation", 0);
                flattener.setDuration(duration);
                flattener.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void expand(){
        animatorExpand.start();
    }

    public void collapse(){
        animatorCollapse.start();
    }

    public interface EndAnimationListener{
        void onAnimationEnded(TodoItem item);
    }
}
