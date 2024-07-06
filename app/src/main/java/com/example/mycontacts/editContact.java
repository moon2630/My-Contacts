package com.example.mycontacts;

import static com.google.common.io.Files.getFileExtension;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

public class editContact extends AppCompatActivity {

    private ImageView profileIv;
    private TextInputEditText nameEt, phoneEt, emailEt, noteEt;
    private Button button;

    //String variable
    private String name, phone, email, note;

    //action bar
    private ActionBar actionBar;

    //permission constant
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 200;
    private static final int IMAGE_FROM_GALLERY_CODE = 300;
    private static final int IMAGE_FROM_CAMERA_CODE = 400;

    //string array of permission
    private String[] cameraPermission;
    private String[] storagePermission;
    StorageReference storageReference;
    private DatabaseReference databaseReference;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    //image uri var
    private Uri imageUri;
    private String node_id;
    private String uri;
    private FirebaseUser fuser;


    TextView back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);


        //init permission
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission. WRITE_EXTERNAL_STORAGE};


        //init view
        back = findViewById(R.id.close);
        profileIv = findViewById(R.id.image);
        nameEt = findViewById(R.id.name);
        phoneEt = findViewById(R.id.phone);
        emailEt = findViewById(R.id.email);
        noteEt = findViewById(R.id.note);
        button = findViewById(R.id.savedata);
        storageReference = storage.getReference();
        Intent i=getIntent();
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        node_id=i.getStringExtra("node_id");
        databaseReference= FirebaseDatabase.getInstance().getReference("ContactsList").child(node_id);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userinfo u = snapshot.getValue(userinfo.class);
                nameEt.setText(u.getName());
                emailEt.setText(u.getEmail());
                phoneEt.setText(u.getNumber());
                noteEt.setText(u.getNote());
                uri=u.getImageurl();
                Glide.with(getApplicationContext()).load(u.getImageurl()).into(profileIv);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),list.class);
                startActivity(i);
                finish();


            }
        });

        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerDialog();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameup = nameEt.getText().toString();
                String emailup = emailEt.getText().toString();
                String numup = phoneEt.getText().toString();
                String note = noteEt.getText().toString();
                imageUri=getUri();

                StorageReference image = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(String.valueOf(imageUri)));
                image.putFile(Uri.parse(String.valueOf(imageUri))).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                updatadata(nameup,emailup,numup,note, String.valueOf(imageUri));

                            }
                        });
                    }
                });
            }
        });
    }

    private void updatadata(String name ,String email ,String number,String note,String imag) {
        HashMap<String, Object> u = new HashMap<>();
        u.put("email",email);
        u.put("name",name);
        u.put("note",note);
        u.put("number",number);
        u.put("imageurl",imag);
        databaseReference.updateChildren(u).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(editContact.this, "Successfully Updated...", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(getApplicationContext(), list.class));
               finish();

            }
        });
    }

    private void showImagePickerDialog() {

        //option for dialog
        String options[] ={"Camera","Gallery"};


        //alert dialog builder
        AlertDialog.Builder builder= new AlertDialog.Builder(this);

        //set title
        builder.setTitle("choose an option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //handle item click
                if (which == 0){
                    //camera selected
                    if (!checkCameraPermission()){
                        //request camera permission
                        requestStoragePermission();

                    }else {
                        pickFromCamera();
                    }

                }else if (which == 1){
                    //gallery selected

                    if (!checkStoragePermission()){
                        //request storage permission
                        requestStoragePermission();
                    }else {
                        pickFromGallery();
                    }
                }
            }
        }).create().show();
    }


    private void pickFromGallery() {
        //intent for taking image from gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");

        startActivityForResult(galleryIntent,IMAGE_FROM_GALLERY_CODE);
    }

    private void pickFromCamera() {
        //content values for image  info

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"IMAGE_TITLE");
        values.put(MediaStore.Images.Media.DESCRIPTION,"IMAGE_DETAILS");

        //save imageUri

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        //intent to open camera

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);

        startActivityForResult(cameraIntent,IMAGE_FROM_CAMERA_CODE);
    }


    //check camera permission
    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean resul1 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return  result & resul1;
    }

    //request for camera permission
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_PERMISSION_CODE);
    }

    //check storage permission
    private boolean checkStoragePermission() {

        boolean resul1 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return resul1;
    }

    //request for camera permission
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_PERMISSION_CODE);
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
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_FROM_GALLERY_CODE){
                //picked image from gallery
                //crop image
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(this);

            }else if (requestCode == IMAGE_FROM_CAMERA_CODE){
                //picked image from camera
                //crop image
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(this);

            }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                //crop image received
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                imageUri = result.getUri();
                profileIv.setImageURI(imageUri);

            }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Toast.makeText(getApplicationContext(), "something wrong", Toast.LENGTH_SHORT).show();

            }

        }
    }

    public Uri getUri() {
        Uri imageUri = null;
        // Assuming you have an ImageView called 'imageView' in your layout
        //ImageView imageView = findViewById(R.id.);
        Drawable drawable = profileIv.getDrawable();

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
}



























