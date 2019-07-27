package com.mrbell.hellobello;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextInputLayout mStatus;
    private Button btnsave;

    private EditText edtstatus;

    private FirebaseUser currentUser;
    private DatabaseReference dbrff;

    private ProgressDialog mprogress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mToolbar=findViewById(R.id.statusToolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mStatus=findViewById(R.id.statusinpulayout);
        btnsave=findViewById(R.id.statuschangebtn);
        edtstatus=findViewById(R.id.stausonmind);
        mprogress=new ProgressDialog(this);

        Intent i = getIntent();
        Bundle b =i.getExtras();
        String sdata=b.getString("statusdata");
        edtstatus.setText(sdata);

        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        String userId=currentUser.getUid();
        dbrff= FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mprogress.setTitle("Saving Chang");
                mprogress.setMessage("Please wait till  we save the change");
                mprogress.show();

                String status = edtstatus.getText().toString();
                dbrff.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mprogress.dismiss();
                            Toast.makeText(StatusActivity.this, "new status posted", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
                            finish();
                        }else{
                            mprogress.dismiss();
                            Toast.makeText(StatusActivity.this, "error "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}
