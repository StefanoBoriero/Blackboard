package dima.it.polimi.blackboard.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.security.Permission;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.utils.GlideApp;

public class PhotoDialogActivity extends Activity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int GALLERY_INTENT = 2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_dialog);


        loadProfilePicture();
        setUpButtons();
    }

    private void loadProfilePicture()
    {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String lastEdit = readSharedPreferenceForCache();
        StorageReference reference = storage.getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString() + "/profile.jpg" + lastEdit);
        ImageView ivProfile = findViewById(R.id.expanded_image);
        final View progressBar =  findViewById(R.id.my_constraint_layout);
        GlideApp.with(getBaseContext())
                .load(reference)
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
                .error(R.drawable.empty_profile_pic_blue)
                .into(ivProfile);
    }

    private void setUpButtons()
    {
        ImageButton buttonCamera =  findViewById(R.id.buttonCamera);
        ImageButton buttonGallery =  findViewById(R.id.buttonGallery);

        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String permission;
                permission = android.Manifest.permission.CAMERA;
                if(!hasPermissions(permission)) {
                    ActivityCompat.requestPermissions(PhotoDialogActivity.this, new String[]{permission},REQUEST_IMAGE_CAPTURE);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
                }
                else
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        buttonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String permission = android.Manifest.permission.READ_EXTERNAL_STORAGE;
                if(!hasPermissions(permission)){
                    ActivityCompat.requestPermissions(PhotoDialogActivity.this,new String[]{permission},GALLERY_INTENT);
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent,GALLERY_INTENT);
                }
                else
                {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent,GALLERY_INTENT);
                }
            }
        });

        

    }

    private boolean hasPermissions( String... permissions)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null)
        {
            for(String permission: permissions)
            {
                if(ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED)
                    return false;
            }
        }
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);



        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK)
        {
            Uri uri = data.getData();
            FirebaseStorage store = FirebaseStorage.getInstance();
            StorageReference storageReference = store.getReference();
            String lastEdit = String.valueOf(System.currentTimeMillis());
            StorageReference userReference = storageReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString() + "/profile.jpg" + lastEdit);
            final View progressBar =  findViewById(R.id.my_constraint_layout);
            ImageView ivProfile = findViewById(R.id.expanded_image);
            ivProfile.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            userReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    SharedPreferences sharedPref = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("imageCaching", lastEdit);
                    editor.commit();
                    loadProfilePicture();
                    ivProfile.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    setResult(3);
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
        String lastEdit = String.valueOf(System.currentTimeMillis());
        StorageReference userReference = storageReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString() + "/profile.jpg" + lastEdit);
        final View progressBar =  findViewById(R.id.my_constraint_layout);
        ImageView ivProfile = findViewById(R.id.expanded_image);
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
                SharedPreferences sharedPref = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("imageCaching", lastEdit);
                editor.commit();
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                ivProfile.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                setResult(3);
                setResult(3);
                loadProfilePicture();
            }
        });
    }

    private String readSharedPreferenceForCache()
    {
        SharedPreferences sharedPref = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        return  sharedPref.getString("imageCaching","0");
    }
}
