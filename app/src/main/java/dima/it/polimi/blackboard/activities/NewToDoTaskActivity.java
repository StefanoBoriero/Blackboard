package dima.it.polimi.blackboard.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.utils.GUIUtils;
import dima.it.polimi.blackboard.utils.OnRevealAnimationListener;

public class NewToDoTaskActivity extends AppCompatActivity {

    public Button typeButton;
    public EditText editText;
    public TextView costView;
    public EditText costEditText;
    private FloatingActionButton mFab;
    private RelativeLayout container_layout;



    private ConstraintLayout myConstraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        LinearLayout linearLayout;
        RelativeLayout myRelativeLayout;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_to_do_task);

        //initialize views
        typeButton = (Button) findViewById(R.id.typeButton);
        editText = (EditText) findViewById(R.id.nameEditText);
        myConstraintLayout = (ConstraintLayout) findViewById(R.id.constraintLayout);
        costEditText = (EditText) findViewById(R.id.costEditText);
        costView = (TextView) findViewById(R.id.costTextView);
        mFab = (FloatingActionButton) findViewById((R.id.activity_contact_fab));
        mFab.setTransitionName("revealCircular");
        linearLayout = findViewById(R.id.newLinLayout);
        myRelativeLayout = findViewById((R.id.newRelativeLayout));
        container_layout = findViewById(R.id.container_layout);

        //initialize context menu
        typeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerForContextMenu(typeButton);
                openContextMenu(typeButton);
            }
        });
        editText.clearFocus();

        //hide cost field
        costEditText.setVisibility(View.GONE);
        costView.setVisibility(View.GONE);



        setupEnterAnimation();

    }

    //We override dispatchTouchEvent in order to take away the focus from
    //the editText when clicking outside the editable field
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();

        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom()) {
                ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
                view.clearFocus();
                //give focus to constraint layout
                myConstraintLayout.requestFocus();
            }

        }

        return super.dispatchTouchEvent(ev);
    }

    //this creates a context menu to use when selecting the type of a task
    @Override
    public void onCreateContextMenu (ContextMenu menu, View
            v, ContextMenu.ContextMenuInfo menuInfo){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.task_type_selection_menu,menu);
    }


    public boolean onContextItemSelected (MenuItem item){

        typeButton.setText(item.toString());

        if(item.toString().equals(getString(R.string.bills)))
        {
            //show cost field
            costEditText.setVisibility(LinearLayout.VISIBLE);
            costView.setVisibility(LinearLayout.VISIBLE);
        }
        else
        {
            //hide cost field
            costEditText.setVisibility(LinearLayout.GONE);
            costEditText.setText("0.00");
            costView.setVisibility(LinearLayout.GONE);

        }
    return true;
    }



    //setUp the initial animation for this activity
    private void setupEnterAnimation() {
        Transition transition = TransitionInflater.from(this)
                .inflateTransition(R.transition.changebounds_with_arcmotion);
        getWindow().setSharedElementEnterTransition(transition);
        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
    }

    private void animateRevealShow() {
        View view = container_layout;
        int cx = (myConstraintLayout.getWidth()) / 2;
        int cy = (myConstraintLayout.getHeight()) / 2;
        GUIUtils.animateRevealShow(this, view, mFab.getWidth() / 2, R.color.colorAccent,
                cx, cy, new OnRevealAnimationListener() {
                    @Override
                    public void onRevealHide() {

                    }

                    @Override
                    public void onRevealShow() {
                        initViews();
                    }
                });
    }

    //fade in the views
    private void initViews() {
        new Handler(Looper.getMainLooper()).post(() -> {
            Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            animation.setDuration(200);
            myConstraintLayout.startAnimation(animation);
            myConstraintLayout.setVisibility(View.VISIBLE);
            container_layout.setBackground(getResources().getDrawable(R.drawable.background_addition));
            mFab.setVisibility(View.GONE);
        });
    }

    //when the back button is pressed, we need to start the back animation
    @Override
    public void onBackPressed() {
        GUIUtils.animateRevealHide(this, myConstraintLayout, R.color.colorAccent, mFab.getWidth() / 2,
                new OnRevealAnimationListener() {
                    @Override
                    public void onRevealHide() {
                        mFab.setVisibility(View.VISIBLE);
                        backPressed();
                    }

                    @Override
                    public void onRevealShow() {

                    }
                });
    }

    private void backPressed() {
        super.onBackPressed();

    }

    //saves the state of the application when changing orientation
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("type", typeButton.getText().toString());
        boolean activateCost = false;
        if(costEditText.getVisibility() == LinearLayout.VISIBLE)
            activateCost = true;
        savedInstanceState.putBoolean("activated_cost",activateCost);
        savedInstanceState.putString("cost", (costEditText.getText().toString()));
    }

    //used to keep the state of the application when changing the orientation
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        myConstraintLayout.setVisibility(View.VISIBLE);
        container_layout.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        mFab.setVisibility(View.INVISIBLE);
        container_layout.setBackground(getResources().getDrawable(R.drawable.background_addition));

        typeButton.setText(savedInstanceState.getString("type"));
        if(savedInstanceState.getBoolean("activated_cost"))
        {
            //show cost field
            costEditText.setVisibility(LinearLayout.VISIBLE);
            costView.setVisibility(LinearLayout.VISIBLE);
            costEditText.setText(savedInstanceState.getString("cost"));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_account, menu);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //TODO create string
        getSupportActionBar().setTitle("Add activity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}