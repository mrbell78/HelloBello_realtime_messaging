package com.mrbell.hellobello;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    private Button btnLoinac,btnRegisterac;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

      btnLoinac=findViewById(R.id.aclogin);
      btnRegisterac=findViewById(R.id.acRegister);


      btnRegisterac.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              startActivity(new Intent(getApplicationContext(),RegisterActivity.class));

          }
      });

      btnLoinac.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              startActivity(new Intent(getApplicationContext(),LoginActivity.class));

          }
      });
    }
}
