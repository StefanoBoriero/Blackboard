package dima.it.polimi.blackboard.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import dima.it.polimi.blackboard.R;

/**
 * This class handles the login procedure
 * Created by Stefano on 29/12/2017.
 */

public class LoginActivity extends AppCompatActivity {
    private final static String TAG = "login-activity";
    private final static int ANIMATION_DELAY = 800;
    private ConstraintSet mConstraintSet2 = new ConstraintSet(); // create a Constraint Set
    private ConstraintLayout mConstraintLayout;
    private View contentLogin;
    private GoogleApiClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private SignInButton googleButton;

    private EditText userNameEditText;
    private EditText passwordEditText;
    private ProgressBar progressBar;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = this;
        mConstraintSet2.clone(context, R.layout.activity_login); // get constraints from layout
        setContentView(R.layout.activity_login_splash);
        mConstraintLayout = findViewById(R.id.root_layout);
        contentLogin = findViewById(R.id.content_login);
        googleButton = findViewById(R.id.sign_in_button);
        userNameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);

        mAuth = FirebaseAuth.getInstance();

        //If the user is already logged in, send him to main activity


        passwordEditText.setOnEditorActionListener( (v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onLogin(v);
                handled = true;
            }
            return handled;
        });


        checkLogin();
        setUpGoogleLogin();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() != null)
                {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("users").whereEqualTo("auth_id", firebaseAuth.getCurrentUser().getUid()).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if(task.getResult().getDocuments().isEmpty())
                                        {
                                            finish();
                                            Intent i = new Intent(LoginActivity.this,InsertDetailsActivity.class);
                                            startActivity(i);
                                        }
                                        else
                                        {
                                            finish();
                                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(i);
                                        }
                                    }
                                    else {
                                        //TODO add error message
                                    }
                                }
                            });
                }

            }
        };


    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void checkLogin()
    {
        if(mAuth.getCurrentUser() != null)
        {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").whereEqualTo("auth_id", mAuth.getCurrentUser().getUid()).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if(task.getResult().getDocuments().isEmpty())
                                {
                                    mAuth.signOut();
                                    handleTransition();
                                }
                                else
                                {
                                    finish();
                                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(i);
                                }
                            }
                            else {
                                //TODO add error message
                            }
                        }
                    });
        }
        else
        {
            handleTransition();
        }

    }

    //this method sets up login with email and password
    private void userNormalLogin()
    {
        String email = userNameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if(TextUtils.isEmpty(email))
        {
            userNameEditText.setError("Please enter e-mail address");

            //We don't complete the request
            return;
        }

        if(TextUtils.isEmpty(password))
        {
            passwordEditText.setError("Please enter password");

            //We don't complete the request
            return;
        }

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful())
                {
                    try {
                        throw task.getException();
                    }
                    catch(FirebaseAuthInvalidCredentialsException e) {
                        Toast.makeText(LoginActivity.this,"Invalid username or password",Toast.LENGTH_SHORT).show();
                    }catch(Exception e) {
                        Toast.makeText(LoginActivity.this,"Generic error",Toast.LENGTH_SHORT).show();
                    }

                }
                else{
                    sendToken();
                }

            }
        });
    }

    private void sendToken(){
        FirebaseInstanceId instanceId = FirebaseInstanceId.getInstance();
        String currentToken = instanceId.getToken();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            String uid = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users").document(uid).update("token", currentToken).addOnFailureListener(e ->
                    Log.w(TAG, "Error updating document", e)
            );
        }
    }

    //this method is used to set up the google login
    private void setUpGoogleLogin()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = new GoogleApiClient.Builder(getApplicationContext()).enableAutoManage(this,
                new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginActivity.this,"Ops, connection failed",Toast.LENGTH_LONG);
                    }
                }).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
                startActivityForResult(signInIntent, 0);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 0) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                sendToken();
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately

                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();

                        } else {
                            // If sign in fails, display a message to the user.

                        }

                        // ...
                    }
                });
    }


    //handle clicks
    public void onLogin(View view){
        userNormalLogin();
    }

    public void onClick(View v)
    {
        Intent intent = new Intent(LoginActivity.this, SignInActivity.class);
        startActivity(intent);
    }



    // the following three methods handles UI events like touches outside an editText or transitions
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();

        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom()) {
                InputMethodManager softKeyboard = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                if(softKeyboard != null) {
                    softKeyboard.hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
                }
                view.clearFocus();
                //give focus to constraint layout
                mConstraintLayout.requestFocus();
            }

        }

        return super.dispatchTouchEvent(ev);
    }

    private void  handleTransition()
    {
        new Handler().postDelayed(() -> {
            Transition transition = new ChangeBounds();
            transition.setInterpolator(new OvershootInterpolator());
            transition.setDuration(ANIMATION_DELAY);
            transition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {

                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    transition.removeListener(this);
                    initViews();
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
            TransitionManager.beginDelayedTransition(mConstraintLayout, transition);


            mConstraintSet2.applyTo(mConstraintLayout);
        }, ANIMATION_DELAY);
    }

    private void initViews() {
        new Handler(Looper.getMainLooper()).post(() -> {
            Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            animation.setDuration(200);
            contentLogin.startAnimation(animation);
            contentLogin.setVisibility(View.VISIBLE);
        });
    }
}
