package dima.it.polimi.blackboard.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import dima.it.polimi.blackboard.R;

/**
 * This class handles the login procedure
 * Created by Stefano on 29/12/2017.
 */

public class LoginActivity extends AppCompatActivity {
    final public static int ANIMATION_DELAY = 800;
    ConstraintSet mConstraintSet2 = new ConstraintSet(); // create a Constraint Set
    ConstraintLayout mConstraintLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = this;
        mConstraintSet2.clone(context, R.layout.activity_login); // get constraints from layout
        setContentView(R.layout.activity_login_splash);
        mConstraintLayout = findViewById(R.id.root_layout);

        new Handler().postDelayed(() -> {
            Transition transition = new ChangeBounds();
            transition.setInterpolator(new OvershootInterpolator());
            transition.setDuration(ANIMATION_DELAY);
            TransitionManager.beginDelayedTransition(mConstraintLayout, transition);
            mConstraintSet2.applyTo(mConstraintLayout);
        }, ANIMATION_DELAY);
    }

    public void onLogin(View view){
        // TODO check username and password
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
    }
}
