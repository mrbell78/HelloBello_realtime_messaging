package com.mrbell.hellobello;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserprofiledetailsActivity extends AppCompatActivity {

    private TextView Name,Status,Profilefrndcount;
    private ImageView profileimage;
    private Button btnSendrequest,btncancelRequest;
    private DatabaseReference dref,frnddbref,frndlistdb,notificationdb,rootDb;
    private ProgressDialog dialog;
    private PhotoView photoView;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentuser;
    private String mCurrentstate;
    private static final String TAG = "UserprofiledetailsActiv";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofiledetails);
        final String userprofileid = getIntent().getStringExtra("id");


        Name=findViewById(R.id.titlename);
        Status=findViewById(R.id.titlestatus);
        Profilefrndcount=findViewById(R.id.frndscount);
        //profileimage=findViewById(R.id.userprofileimage);
        photoView=findViewById(R.id.userprofileimage);
        btnSendrequest=findViewById(R.id.btnsendFrndrquest);
        btncancelRequest=findViewById(R.id.btcancelFrndrquest);
        dref= FirebaseDatabase.getInstance().getReference().child("Users").child(userprofileid);
        mAuth=FirebaseAuth.getInstance();
        mCurrentuser=FirebaseAuth.getInstance().getCurrentUser();
        frnddbref=FirebaseDatabase.getInstance().getReference().child("Friend_req");

        frndlistdb=FirebaseDatabase.getInstance().getReference().child("Friendslist");
        notificationdb=FirebaseDatabase.getInstance().getReference().child("Notification");
        rootDb=FirebaseDatabase.getInstance().getReference();
        mCurrentstate="not_frnd";

        dialog=new ProgressDialog(UserprofiledetailsActivity.this);
        dialog.setTitle("Loading User data");
        dialog.setMessage("Please wait.......... :)");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();


        if(userprofileid.equals(mCurrentuser.getUid())){
            btnSendrequest.setEnabled(true);
            btnSendrequest.setText("Delete my Account");
            mCurrentstate="deleteac";
        }
         dref.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 String name= dataSnapshot.child("name").getValue().toString();
                 String status= dataSnapshot.child("status").getValue().toString();
                 String image= dataSnapshot.child("image").getValue().toString();

                 Name.setText(name);
                 Status.setText(status);

                     Glide.with(getApplicationContext()).load(image).into(photoView);

                     frnddbref.child(mCurrentuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                             if(dataSnapshot.hasChild(userprofileid)){

                                 String req_type= dataSnapshot.child(userprofileid).child("request_type").getValue().toString();
                                 if(req_type.equals("received")){
                                     btnSendrequest.setEnabled(true);
                                     mCurrentstate="req_received";
                                     btnSendrequest.setText("Accept Friend Request");
                                     btncancelRequest.setVisibility(View.VISIBLE);
                                     dialog.dismiss();
                                     Log.d(TAG, "onDataChange: -----------------------------------------------------"+req_type);
                                 }else if(req_type.equals("sent")){
                                     mCurrentstate="req_sent";
                                     btnSendrequest.setEnabled(true);
                                     btnSendrequest.setText("Cancel Friend Request");
                                     Log.d(TAG, "onDataChange: -----------------------------------------------------"+req_type);
                                 }



                             }else {

                                 frndlistdb.child(mCurrentuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                     @Override
                                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                         String userid = frndlistdb.child(userprofileid).getKey();

                                         if (dataSnapshot.hasChild(userprofileid) && !userid.equals(mCurrentuser.getUid())) {

                                             btnSendrequest.setEnabled(true);
                                             btnSendrequest.setText("Unfriend this person");
                                             btncancelRequest.setVisibility(View.INVISIBLE);
                                             mCurrentstate="frind";

                                             Log.d(TAG, "onDataChange: ------------------------------------fsender------------------------------------------------"+userid);


                                         }
                                        // Log.d(TAG, "onDataChange: -----------------------------------------selfid-----------" + userid);
                                         Log.d(TAG, "onDataChange: -----------------------------------------currentuser-----------" + mCurrentuser.getUid());
                                         Log.d(TAG, "onDataChange: -----------------------------------------mCurrent state outside condition-----------" + mCurrentstate);


                                     }

                                     @Override
                                     public void onCancelled(@NonNull DatabaseError databaseError) {

                                     }
                                 });
                             }


                             dialog.dismiss();
                         }

                         @Override
                         public void onCancelled(@NonNull DatabaseError databaseError) {
                             dialog.dismiss();
                         }
                     });





             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });

        btncancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map declinefrnd=new HashMap();
                declinefrnd.put("Friend_req/"+mCurrentuser.getUid()+"/"+userprofileid,null);
                declinefrnd.put("Friend_req/"+userprofileid+"/"+mCurrentuser.getUid(),null);

                rootDb.updateChildren(declinefrnd, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if(databaseError==null){
                            btnSendrequest.setEnabled(true);
                            btnSendrequest.setText("Send Friend Request");
                            mCurrentstate="not_frnd";

                            Toast.makeText(UserprofiledetailsActivity.this, "u declined the request", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            btncancelRequest.setEnabled(false);
                            btncancelRequest.setVisibility(View.INVISIBLE);

                        }else{
                            String error =databaseError.getMessage();
                            Toast.makeText(UserprofiledetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
         btnSendrequest.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if(mCurrentstate.equals("not_frnd")){

                     btnSendrequest.setEnabled(false);
                     DatabaseReference newNotificationref = rootDb.child("notifications").child(userprofileid).push();
                     String notificationId=newNotificationref.getKey();

                     HashMap<String,String>notificationdata = new HashMap<>();
                     notificationdata.put("from",mCurrentuser.getUid());
                     notificationdata.put("type","request");

                     Map requestMap = new HashMap();
                     requestMap.put("Friend_req/"+mCurrentuser.getUid() + "/" + userprofileid + "/"+ "request_type","sent");
                     requestMap.put("Friend_req/"+userprofileid + "/"+ mCurrentuser.getUid() + "/"+"request_type","received");
                     requestMap.put("Friend_req/"+userprofileid + "/"+ "sender",FirebaseAuth.getInstance().getCurrentUser().getUid());
                     requestMap.put("notifications/"+userprofileid + "/"+ notificationId,notificationdata);
                     rootDb.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                         @Override
                         public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                             if(databaseError!=null){
                                 Toast.makeText(UserprofiledetailsActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                             }else{

                                 btnSendrequest.setEnabled(true);
                                 btnSendrequest.setText("Cancel Friend Request");
                                 mCurrentstate="req_sent";
                                 Toast.makeText(UserprofiledetailsActivity.this, "request sent", Toast.LENGTH_SHORT).show();
                                 dialog.dismiss();
                             }
                         }
                     });
                 }
                 //------------------cancel request--------------------------------------------

                 if(mCurrentstate.equals("req_sent")){

                     Map reqremove=new HashMap();

                     reqremove.put("Friend_req/"+mCurrentuser.getUid()+"/"+userprofileid,null);
                     reqremove.put("Friend_req/"+userprofileid+"/"+mCurrentuser.getUid(),null);
                     reqremove.put("Friend_req/"+userprofileid + "/"+ "sender",null);

                     rootDb.updateChildren(reqremove, new DatabaseReference.CompletionListener() {
                         @Override
                         public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                             btnSendrequest.setEnabled(true);
                             btnSendrequest.setText("Send Friend Request");
                             mCurrentstate="not_frnd";
                             Log.d(TAG, "onDataChange: -----------------------------------------------------"+mCurrentstate);
                             Toast.makeText(UserprofiledetailsActivity.this, "request removed", Toast.LENGTH_SHORT).show();
                             dialog.dismiss();
                         }
                     });

                 }
                 //----------------------------------request received---------------------------
                 if(mCurrentstate.equals("req_received")){
                     final String currentdatae= DateFormat.getDateInstance().format(new Date());
                     Map frindMap = new HashMap();

                     frindMap.put("Friendslist/"+mCurrentuser.getUid() + "/"+ userprofileid + "/date",currentdatae);
                     frindMap.put("Friendslist/"+userprofileid+"/"+mCurrentuser.getUid() + "/"+"date",currentdatae);

                     frindMap.put("Friend_req/"+mCurrentuser.getUid()+"/"+userprofileid,null);
                     frindMap.put("Friend_req/"+userprofileid+"/"+mCurrentuser.getUid(),null);

                     rootDb.updateChildren(frindMap, new DatabaseReference.CompletionListener() {
                         @Override
                         public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                             if(databaseError==null){
                                 btnSendrequest.setEnabled(true);
                                 btnSendrequest.setText("Unfriend this person");
                                 btncancelRequest.setVisibility(View.INVISIBLE);
                                 mCurrentstate="frind";
                                 dialog.dismiss();
                             }else {
                                 String error = databaseError.getMessage();
                                 Toast.makeText(UserprofiledetailsActivity.this, "Accept error "+error, Toast.LENGTH_SHORT).show();
                             }
                         }
                     });
                 }
                 //----------------------------------------unfriend----------------------------------------
                 if(mCurrentstate.equals("frind")){

                     Map removefrnd= new HashMap();
                     removefrnd.put("Friendslist/"+mCurrentuser.getUid() + "/"+ userprofileid ,null);
                     removefrnd.put("Friendslist/"+userprofileid+"/"+mCurrentuser.getUid(),null);

                     rootDb.updateChildren(removefrnd, new DatabaseReference.CompletionListener() {
                         @Override
                         public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                             if(databaseError==null){
                                 btnSendrequest.setEnabled(true);
                                 btnSendrequest.setText("Send Friend Request");
                                 mCurrentstate="not_frnd";

                                 Toast.makeText(UserprofiledetailsActivity.this, "u guys r no longer frnd", Toast.LENGTH_SHORT).show();
                                 dialog.dismiss();
                             }else{
                                 String eroor = databaseError.getMessage();
                                 Toast.makeText(UserprofiledetailsActivity.this, eroor, Toast.LENGTH_SHORT).show();
                             }

                         }
                     });

                 }
             }
         });



    }
}
