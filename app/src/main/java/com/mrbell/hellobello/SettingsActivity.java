package com.mrbell.hellobello;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference dbrf;
    private FirebaseUser currentUser;

    private TextView titleName,statustitle,changeImage,changeStatus;
    private CircleImageView profileimg;

    public static final int Gallery_Pick=1;
    Uri imgUri;
    private StorageReference mStorage;
    private static final String TAG = "SettingsActivity";
    String userId;
    private ProgressDialog mproges;
    private byte[] thumbdata;

    String orginalimagelink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        titleName=findViewById(R.id.settingnametitletv);
        statustitle=findViewById(R.id.settingsStatus);
        profileimg=findViewById(R.id.settingsppimg);
        mproges=new ProgressDialog(this);

        changeImage=findViewById(R.id.settings_changeimgtv);
        changeStatus=findViewById(R.id.settings_changeStatustv);

        currentUser=FirebaseAuth.getInstance().getCurrentUser();
        userId=currentUser.getUid();
        dbrf= FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        dbrf.keepSynced(true);

        mStorage= FirebaseStorage.getInstance().getReference();

        mproges.setTitle("Change Image");
        mproges.setMessage("please wait......");
        mproges.setCanceledOnTouchOutside(false);
        mproges.show();
        dbrf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String name = dataSnapshot.child("name").getValue().toString();
                    String status= dataSnapshot.child("status").getValue().toString();
                    final String image= dataSnapshot.child("image").getValue().toString();
                    String thum_img=dataSnapshot.child("thumb_img").getValue().toString();
                    orginalimagelink=image;
                    titleName.setText(name);
                    statustitle.setText(status);
                    Log.d(TAG, "onDataChange: "+thum_img);
                    if(!image.equals("default"))
                        //Glide.with(SettingsActivity.this).load(image).into(profileimg);
                    Picasso.with(SettingsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(profileimg, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(SettingsActivity.this).load(image).into(profileimg);

                        }
                    });
                    mproges.dismiss();
                }else{
                    Toast.makeText(SettingsActivity.this, "No data exist on this app", Toast.LENGTH_SHORT).show();
                    mproges.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        profileimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(),ProfileimageActivity.class).putExtra("img",orginalimagelink).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });

        changeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String statusdata = statustitle.getText().toString();
                startActivity(new Intent(getApplicationContext(),StatusActivity.class).putExtra("statusdata",statusdata));
            }
        });

        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


               if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                   if(ContextCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){

                       ActivityCompat.requestPermissions(SettingsActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                   }else{

                       imagePicker();

                   }
               }else{
                   imagePicker();

               }
            }
        });


    }

    private void imagePicker() {
       CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(this);

        /*Intent galleryIntent= new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"),Gallery_Pick);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            mproges.show();
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                final Uri resulturi =     result.getUri();
                File orginalthumfile =  new File(resulturi.getPath());
                try {
                    Bitmap thumb_bitmap =  new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(orginalthumfile);

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos);
                    thumbdata= bos.toByteArray();

                } catch (IOException e) {

                    e.printStackTrace();
                }


                final StorageReference filepath = mStorage.child("allprofileimage").child(userId+".jpg");

                final StorageReference thumbnailimagepath = mStorage.child("allprofileimage").child("thumimg").child(userId+".jpg");
                filepath.putFile(resulturi).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        if(taskSnapshot!=null){
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final Uri downloaduri=uri;
                                    imgUri=downloaduri;

                                    UploadTask uploadTask = thumbnailimagepath.putBytes(thumbdata);
                                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                                            if(task.isSuccessful()){
                                                thumbnailimagepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {

                                                        Uri thumimguri = uri;
                                                        if(task.isSuccessful()){
                                                            Map imgdatawiththum= new HashMap();
                                                            imgdatawiththum.put("image",imgUri.toString());
                                                            imgdatawiththum.put("thumb_img",thumimguri.toString());

                                                            dbrf.updateChildren(imgdatawiththum).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful()){
                                                                        mproges.dismiss();
                                                                        Toast.makeText(SettingsActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                                                                    }else{
                                                                        mproges.dismiss();
                                                                        Toast.makeText(SettingsActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });


                                                        }

                                                    }
                                                });

                                            }
                                        }
                                    });

                                }
                            });
                        }else{
                            Toast.makeText(SettingsActivity.this, "uploadfailed", Toast.LENGTH_SHORT).show();
                            mproges.dismiss();
                        }
                    }
                });
            }else if(resultCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
        }
    }
}
