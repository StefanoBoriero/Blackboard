package dima.it.polimi.blackboard.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.utils.GlideApp;

public class InsertDetailsActivity extends AppCompatActivity implements DialogInterface.OnClickListener {
    FirebaseFirestore db;
    ConstraintLayout myConstraintLayout;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int GALLERY_INTENT = 2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_details);
        db = FirebaseFirestore.getInstance();
        myConstraintLayout = findViewById(R.id.root_layout);

        //TODO take this away, only for emulaotr
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        setUpPhotoButton();
        loadProfilePicture();

    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        FirebaseAuth.getInstance().signOut();
        super.onBackPressed();
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
        findViewById(R.id.imageViewProfile).setOnClickListener(new View.OnClickListener() {
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
            final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);
            ImageView ivProfile = findViewById(R.id.imageViewProfile);
            ivProfile.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            userReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    loadProfilePicture();
                    ivProfile.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            });
        }

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
           Bundle bundle = data.getExtras();
           final Bitmap btm = (Bitmap)bundle.get("data");
           encodeBitmapAndSaveToFirebase(btm);
        }
    }

    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        FirebaseStorage store = FirebaseStorage.getInstance();
        StorageReference storageReference = store.getReference();
        StorageReference userReference = storageReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString() + "/profile.jpg");
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);
        ImageView ivProfile = findViewById(R.id.imageViewProfile);
        ivProfile.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);


        UploadTask uploadTask = userReference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                ivProfile.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                ivProfile.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                loadProfilePicture();
            }
        });
    }

    private void loadProfilePicture()
    {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString() + "/profile.jpg");
        ImageView ivProfile = findViewById(R.id.imageViewProfile);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);
        GlideApp.with(getBaseContext())
                .load(reference)
                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        ivProfile.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        ivProfile.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .error(R.drawable.empty_profile_blue_circle)
                .apply(RequestOptions.circleCropTransform())
                .into(ivProfile);
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
                    String permission;

                        if(which == 0)
                        {
                            permission = android.Manifest.permission.CAMERA;
                            if(!hasPermissions(permission)) {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{permission},REQUEST_IMAGE_CAPTURE);
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                getActivity().startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
                            }
                            else
                            {
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                getActivity().startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
                            }
                        }
                        if(which == 1)
                        {
                            permission = android.Manifest.permission.READ_EXTERNAL_STORAGE;
                            if(!hasPermissions(permission)){
                                ActivityCompat.requestPermissions(getActivity(),new String[]{permission},GALLERY_INTENT);
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                getActivity().startActivityForResult(intent,GALLERY_INTENT);
                            }
                            else
                            {
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                getActivity().startActivityForResult(intent,GALLERY_INTENT);
                            }

                        }
                }
            });
            return dialog.create();
        }


        private boolean hasPermissions( String... permissions)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null)
            {
               for(String permission: permissions)
               {
                   if(ContextCompat.checkSelfPermission(getActivity(),permission) != PackageManager.PERMISSION_GRANTED)
                       return false;
               }
            }
            return true;
        }
    }

}
