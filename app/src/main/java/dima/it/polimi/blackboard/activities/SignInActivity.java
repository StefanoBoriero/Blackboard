package dima.it.polimi.blackboard.activities;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import dima.it.polimi.blackboard.R;

public class SignInActivity extends AppCompatActivity {

    private ConstraintLayout myConstraintLayout;
    private EditText repeatEditText;
    private EditText userNameEditText;
    private EditText passwordEditText;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_sign_in);
        myConstraintLayout = findViewById(R.id.root_layout);
        repeatEditText = findViewById(R.id.repeat_password);
        userNameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        firebaseAuth = FirebaseAuth.getInstance();


        repeatEditText.setOnEditorActionListener( (v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onSignUp(v);
                handled = true;
            }
            return handled;
        });


    }

    private void registerUser()
    {
        String email = userNameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String repeatPassword = repeatEditText.getText().toString().trim();

        if(TextUtils.isEmpty(email))
        {
            userNameEditText.setError("Please enter e-mail address");

            //We don''t send the request
            return;
        }

        if(TextUtils.isEmpty(password))
        {
            passwordEditText.setError("Please enter password");

            //We don''t send the request
            return;
        }

        if(!TextUtils.equals(password,repeatPassword))
        {
            repeatEditText.setError("The two passwords must match");

            //We don''t send the request
            return;
        }

        //first controls have been passed
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(SignInActivity.this,"You registered successfully",Toast.LENGTH_SHORT).show();
                    finish();
                    Intent i = new Intent(SignInActivity.this, InsertDetailsActivity.class);
                    startActivity(i);
                }
                else
                {

                    try {
                        throw task.getException();
                    }
                    catch(FirebaseAuthUserCollisionException e) {
                        userNameEditText.setError("E-mail already used");
                    }
                    catch(FirebaseAuthWeakPasswordException e) {
                        passwordEditText.setError("Password must be at least 6 characters long");
                    }
                    catch(FirebaseAuthInvalidCredentialsException e) {
                        userNameEditText.setError("Please enter an e-mail address");
                    }catch(Exception e) {
                        Toast.makeText(SignInActivity.this,"Generic error",Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }


    public void onSignUp(View view){

        registerUser();


    }

    //code used to get editText out of focus when a touch is performed outside the editText
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
}
