package dima.it.polimi.blackboard.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;

import dima.it.polimi.blackboard.R;

/**
 * Created by simone on 20/12/2017.
 */

public class GUIUtils {

    public static void animateRevealShow(final Context ctx, final View view, final int startRadius,
                                         @ColorRes final int color, int x, int y, final OnRevealAnimationListener listener) {
        float finalRadius = (float) Math.hypot(view.getWidth(), view.getHeight());

        Animator anim =
                ViewAnimationUtils.createCircularReveal(view, x, y, startRadius, finalRadius);
        anim.setDuration(ctx.getResources().getInteger(R.integer.on_reveal_duration));
        anim.setStartDelay(100);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setBackgroundColor(ctx.getResources().getColor(color));
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.VISIBLE);
                listener.onRevealShow();
            }
        });
        anim.start();
    }

    public static void animateRevealHide(final Context ctx, final View view, @ColorRes int color,
                                         final int finalRadius, OnRevealAnimationListener listener) {
        int cx = (view.getWidth()) / 2;
        int cy = (view.getHeight()) / 2;
        int initialRadius = view.getWidth();

        Animator anim =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, finalRadius);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                view.setBackgroundColor(ctx.getResources().getColor(color));
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.INVISIBLE);
                listener.onRevealHide();
            }
        });
        anim.setDuration(ctx.getResources().getInteger(R.integer.on_reveal_duration));
        anim.start();
    }
}
