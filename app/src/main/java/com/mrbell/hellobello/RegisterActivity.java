package com.mrbell.hellobello;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText edtname,edtemail,edtpassword;
    private TextView tvLogin;
    private Button btnCreateAccount;

    private ProgressBar prgbar;
    private FirebaseAuth mAuth;

    private DatabaseReference dbrf,Userrf;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtname=findViewById(R.id.registerUsrename);
        edtemail=findViewById(R.id.registermail);
        edtpassword=findViewById(R.id.registerPassword);
        tvLogin=findViewById(R.id.registerLogintv);
        btnCreateAccount=findViewById(R.id.registerCreateAccount);

        prgbar=findViewById(R.id.registerProgressbar);
        mAuth=FirebaseAuth.getInstance();
        mToolbar=findViewById(R.id.registerToolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("Registration");


        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email=edtemail.getText().toString();
                String password=edtpassword.getText().toString();
                String displayname=edtname.getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(displayname)){
                    prgbar.setVisibility(View.VISIBLE);
                    CreateAccount(email,password,displayname);
                }
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        });
    }

    private void CreateAccount(String email, String password, final String displayname) {

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                   FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                   String userid=currentUser.getUid();
                   dbrf=FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                    Map<String,String>usermapfield = new HashMap<>();

                    usermapfield.put("name",displayname);
                    usermapfield.put("status","Hi there i am mr.bell using HelloBello");
                    usermapfield.put("image","default");
                    usermapfield.put("thumb_img","default");
                    usermapfield.put("deviceToken",deviceToken);
                    usermapfield.put("online","true");



                    dbrf.setValue(usermapfield);

                    /*Userrf= FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    Userrf.child("online").setValue("true");*/
                     Toast.makeText(RegisterActivity.this, "Registratiion successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                    finish();
                    prgbar.setVisibility(View.INVISIBLE);

                }else{
                    prgbar.setVisibility(View.INVISIBLE);

                    Toast.makeText(RegisterActivity.this, "registration failed", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
