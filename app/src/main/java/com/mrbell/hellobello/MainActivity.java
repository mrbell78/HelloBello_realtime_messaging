package com.mrbell.hellobello;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;


    private TabLayout mTab;
    private ViewPager mpager;
    private DatabaseReference Userrf;


    private ViewpagerAdapter viewpagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        mToolbar=findViewById(R.id.mainToolbrac);

        mpager=findViewById(R.id.mainViewpager);

        mTab=findViewById(R.id.mainTab);
        viewpagerAdapter=new ViewpagerAdapter(getSupportFragmentManager());


        mpager.setAdapter(viewpagerAdapter);

        mTab.setupWithViewPager(mpager);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("HelloBello");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser muser= FirebaseAuth.getInstance().getCurrentUser();
        if(muser==null){
            startActivity(new Intent(getApplicationContext(),StartActivity.class));
            finish();
        }else {

            Userrf= FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            Userrf.child("online").setValue("true");
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser mCurrentuserstate=FirebaseAuth.getInstance().getCurrentUser();
        if(mCurrentuserstate!=null){

            //Userrf.child("online").setValue(ServerValue.TIMESTAMP);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menue,menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       super.onOptionsItemSelected(item);

        switch (item.getItemId()){
            case R.id.settingsmenu:
                sentToSettings();
                return true;
            case R.id.logoutmenu:
                logout();
                return true;
            case R.id.allusers:
                sentToAlluser();
                return true;
                default:return false;
        }
    }

    private void sentToAlluser() {
        FirebaseUser mCurrentuserstate=FirebaseAuth.getInstance().getCurrentUser();
        if(mCurrentuserstate!=null) {
            Userrf.child("online").setValue("true");
            startActivity(new Intent(getApplicationContext(), AllusersActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }

    }

    private void sentToSettings() {
        FirebaseUser mCurrentuserstate=FirebaseAuth.getInstance().getCurrentUser();
        if(mCurrentuserstate!=null) {
            Userrf.child("online").setValue("true");
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        }
    }

    private void logout() {

        FirebaseUser mCurrentuserstate=FirebaseAuth.getInstance().getCurrentUser();
        if(mCurrentuserstate!=null) {
            Userrf.child("online").setValue(ServerValue.TIMESTAMP);

            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(),StartActivity.class));
            finish();
        }

    }
}
