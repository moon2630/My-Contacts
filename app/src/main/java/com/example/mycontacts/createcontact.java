package com.example.mycontacts;

import static com.google.common.io.Files.getFileExtension;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;


public class createcontact extends AppCompatActivity {

    private CircleImageView profileIv;



    TextView back,addImage;
    FirebaseUser fuser;
    TextInputEditText email_txt, name_txt, number_txt, note_txt;
    AppCompatButton save_data;
    StorageReference storageReference;
    DatabaseReference database;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    //permission constant
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 200;
    private static final int IMAGE_FROM_GALLERY_CODE = 300;
    private static final int IMAGE_FROM_CAMERA_CODE = 400;

    //string array of permission
    private String[] cameraPermission;
    private String[] storagePermission;

    //image uri var
    private Uri imageUri;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createcontact);


        addImage = findViewById(R.id.addpicture);

        profileIv = findViewById(R.id.image);
        back = findViewById(R.id.close);
        name_txt = findViewById(R.id.name);
        number_txt = findViewById(R.id.phone);
        email_txt = findViewById(R.id.email);
        note_txt = findViewById(R.id.note);
        save_data = findViewById(R.id.savedata);

        database = FirebaseDatabase.getInstance().getReference("ContactsList");
        storageReference = storage.getReference();
        fuser = FirebaseAuth.getInstance().getCurrentUser();


        //init permission
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showImagePickerDialog();
            }
        });

        save_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userinfo u = new userinfo();
                u.setUser_id(fuser.getUid());
                u.setName(name_txt.getText().toString());
                u.setNumber(number_txt.getText().toString());
                u.setEmail(email_txt.getText().toString());
                u.setNote(note_txt.getText().toString());
                u.setDate(getCurrentDate());
                imageUri = getUri();
                Log.d("image", "image uri is " + imageUri);
                uploadToFirebase(imageUri, u);
                startActivity(new Intent(getApplicationContext(), list.class));
                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), list.class);
                startActivity(i);
                finish();
            }
        });
    }

    public Uri getUri() {
        Uri imageUri = null;
        // Assuming you have an ImageView called 'imageView' in your layout
        ImageView imageView = findViewById(R.id.image);
        Drawable drawable = imageView.getDrawable();

        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        FileOutputStream fileOutputStream;
        try {
            File file = new File(getCacheDir(), "image.png");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            imageUri = Uri.fromFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("image", "image uri in method:" + imageUri);
        return imageUri;

    }

    private void uploadToFirebase(Uri uri, userinfo u) {
        storage = FirebaseStorage.getInstance();
        StorageReference upload = storage.getReference(System.currentTimeMillis() + "." + getFileExtension(String.valueOf(uri)));
        upload.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                upload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        u.setImageurl(uri.toString());

                        //push node create thy and u data j 6 te databse ma save thse
                        database.push().setValue(u);
                        Toast.makeText(createcontact.this, "Contact Saved Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

            }
        });
    }

    private void showImagePickerDialog() {

        //option for dialog
        String options[] = {"Camera", "Gallery"};

        //alert dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //set title
        builder.setTitle("choose an option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //handle item click
                if (which == 0) {
                    //camera selected
                    if (!checkCameraPermission()) {
                        //request camera permission
                        requestCameraPermission();

                    } else {
                        pickFromCamera();
                    }

                } else if (which == 1) {
                    //gallery selected

                    if (!checkStoragePermission()) {
                        //request storage permission
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }

                }

            }
        }).create().show();
    }

    //  activityResultLauncher code
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        imageUri = data.getData();
                        profileIv.setImageURI(imageUri);
                    } else {
                        Toast.makeText(createcontact.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void pickFromGallery() {
        //intent for taking image from gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/ *");
        // activityResultLauncher.launch(galleryIntent);

        startActivityForResult(galleryIntent, IMAGE_FROM_GALLERY_CODE);
    }

    private void pickFromCamera() {
        //content values for image  info

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "IMAGE_TITLE");
        values.put(MediaStore.Images.Media.DESCRIPTION, "IMAGE_DETAILS");

        //save imageUri

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //intent to open camera

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        startActivityForResult(cameraIntent, IMAGE_FROM_CAMERA_CODE);
    }


    //check camera permission
    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean resul1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result & resul1;
    }

    //request for camera permission
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_PERMISSION_CODE);
    }

    //check storage permission
    private boolean checkStoragePermission() {

        boolean resul1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return resul1;
    }

    //request for camera permission
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_PERMISSION_CODE:
                if (grantResults.length > 0) {

                    //if all permission allowed return type ,otherwise false

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && storageAccepted) {
                        //both permission granted

                        pickFromCamera();
                    } else {
                        //request not granted
                        Toast.makeText(getApplicationContext(), "Camera & Storage Permission Needed..", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_PERMISSION_CODE:
                if (grantResults.length > 0) {

                    //if all permission allowed return true , otherwise false
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (storageAccepted) {
                        //permission granted
                        pickFromGallery();
                    } else {
                        //permission not granted
                        Toast.makeText(getApplicationContext(), "Storage Permission Needed..", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_FROM_GALLERY_CODE) {
                //picked image from gallery
                //crop image
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(this);


            } else if (requestCode == IMAGE_FROM_CAMERA_CODE) {
                //picked image from camera
                //crop image
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(this);

            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                //crop image received
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                imageUri = result.getUri();
                profileIv.setImageURI(imageUri);

            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(getApplicationContext(), "something wrong", Toast.LENGTH_SHORT).show();

            }
        }
    }

    public String getCurrentDate(){
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }
}
