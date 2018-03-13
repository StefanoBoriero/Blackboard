package dima.it.polimi.blackboard.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.model.PersonalInfo;

public class InsertDetailsActivity extends AppCompatActivity implements DialogInterface.OnClickListener {
    FirebaseFirestore db;
    ConstraintLayout myConstraintLayout;
    private static final int GALLERY_INTENT = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_details);
        db = FirebaseFirestore.getInstance();
        myConstraintLayout = findViewById(R.id.root_layout);

        setUpPhotoButton();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        FirebaseAuth.getInstance().signOut();
    }

    public void onSubmit(View v)
    {
        EditText nameEditText = findViewById(R.id.nameEditText);
        EditText surnameEditText = findViewById(R.id.surnameEditText);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        RadioButton selectedRadioButton = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());




        Map<String,Object> user = new HashMap<>();
        Map<String,Object> personalInfo = new HashMap<>();
        Map<String,Object> stats = new HashMap<>();
        personalInfo.put("name",nameEditText.getText().toString().trim());
        personalInfo.put("surname",surnameEditText.getText().toString().trim());
        personalInfo.put("Sex",selectedRadioButton.getText());
        stats.put("billing_task",0);
        stats.put("housekeeping_task",0);
        stats.put("shopping_task",0);
        stats.put("total_task",0);
        user.put("auth_id",FirebaseAuth.getInstance().getCurrentUser().getUid() );
        user.put("personal_info",personalInfo);
        user.put("stats", stats);




        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(user);


        finish();
        Intent i = new Intent(InsertDetailsActivity.this,MainActivity.class);
        startActivity(i);
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
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null) {
                    imm.hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
                }
                view.clearFocus();
                //give focus to constraint layout
                myConstraintLayout.requestFocus();
            }

        }

        return super.dispatchTouchEvent(ev);
    }



    private void setUpPhotoButton()
    {
        findViewById(R.id.user_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InsertDetailsActivity.ProfilePictureDialog.mListener = this;
                DialogFragment houseListDialog = ProfilePictureDialog.newInstance();
                houseListDialog.show(getFragmentManager(), "dialog");
            }
        });
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

    public static class ProfilePictureDialog extends DialogFragment {
        public static View.OnClickListener mListener;

        public static InsertDetailsActivity.ProfilePictureDialog newInstance() {
            InsertDetailsActivity.ProfilePictureDialog fragment = new InsertDetailsActivity.ProfilePictureDialog();
            return fragment;
        }

        @Override
        public Dialog onCreateDialog(final Bundle savedInstanceState) {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            final CharSequence[] items = {"Take Photo", "Choose From Gallery", "Cancel"};
            dialog.setTitle("Add a photo");
            dialog.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                        if(which == 0)
                        {
                            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                            startActivity(intent);
                        }
                        if(which == 1)
                        {
                            Intent intent = new Intent(Intent.ACTION_PICK);

                            intent.setType("image/*");
                            startActivityForResult(intent,GALLERY_INTENT);
                        }
                }
            });
            return dialog.create();
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data)
        {
            super.onActivityResult(requestCode,resultCode,data);

            if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK)
            {

                Uri uri = data.getData();
                FirebaseStorage store = FirebaseStorage.getInstance();
                StorageReference storageReference = store.getReference();
                StorageReference userReference = storageReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString() + "/profile.jpg");
                userReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    }
                });
            }
        }
    }
}
