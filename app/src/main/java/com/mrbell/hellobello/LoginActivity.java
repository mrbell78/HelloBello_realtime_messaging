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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText edtmail,edtpassword;
    private Button btnLogin;
    private TextView regigerAccounttv;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserdabase;
    private ProgressBar progressBar;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtmail=findViewById(R.id.loginmail);
        edtpassword=findViewById(R.id.loginpassword);
        btnLogin=findViewById(R.id.loginLogin);
        regigerAccounttv=findViewById(R.id.loginRegisterac);
        mAuth=FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.loginprogressbar);
        mUserdabase= FirebaseDatabase.getInstance().getReference().child("Users");
        mToolbar=findViewById(R.id.loginToolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login Account");
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email=edtmail.getText().toString();
                String password=edtpassword.getText().toString();
                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                    progressBar.setVisibility(View.VISIBLE);
                    login(email,password);
                }else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(LoginActivity.this, "mail or password missing", Toast.LENGTH_SHORT).show();
                }

            }
        });

        regigerAccounttv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));

                 }
        });
    }

    private void login(String email, String password) {



            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        String deviceToken = FirebaseInstanceId.getInstance().getToken();
                        String currentUser= mAuth.getCurrentUser().getUid();

                        mUserdabase.child(currentUser).child("deviceToken").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));

                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });


                    }else{
                        Toast.makeText(LoginActivity.this, "login failed", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
            });
    }
}
