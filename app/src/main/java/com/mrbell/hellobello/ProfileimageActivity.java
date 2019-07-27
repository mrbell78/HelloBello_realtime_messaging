package com.mrbell.hellobello;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

public class ProfileimageActivity extends AppCompatActivity {

    private static final String TAG = "ProfileimageActivity";
    private Toolbar mToolbar;
    private ImageView img;
    private PhotoView photoview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profileimage);
        mToolbar=findViewById(R.id.profileimgtoolbar);
        photoview=findViewById(R.id.imgview);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile Image");

        Intent intent = getIntent();
        String image_path= intent.getStringExtra("img");
        Uri fileUri = Uri.parse(image_path);
        Log.d(TAG, "onCreate: ----------------------------------------------------------------------"+image_path);

        Glide.with(ProfileimageActivity.this).load(fileUri).into(photoview);



    }
}
