package com.mrbell.hellobello;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private DatabaseReference mRootref,mChatdb;
    private String userid;
    private CircleImageView profileimg;

    private ImageButton btnadd,btnsend;
    private EditText edtmessage;

    private static final String TAG = "ChatActivity";
    private String mCurrentuserid;
    private RecyclerView chatRecycler;

    private List<Messagerretrive>list;
    private MessageAdapter adapter;
    public static final  int TOTAL_LOAD_PERONE=10;
    private int mCurrentpage=1;
    private SwipeRefreshLayout refreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mToolbar=findViewById(R.id.chatToolbar);
        mRootref= FirebaseDatabase.getInstance().getReference().child("Users");
        mChatdb= FirebaseDatabase.getInstance().getReference();
        setSupportActionBar(mToolbar);
        userid=getIntent().getStringExtra("id");
        String username=getIntent().getStringExtra("name");
        String imgthumb=getIntent().getStringExtra("imglink");

        //---------recycler initialization--------------
        chatRecycler=findViewById(R.id.chatrecycler);
        chatRecycler.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        chatRecycler.setLayoutManager(manager);


        refreshLayout=findViewById(R.id.swifeformessage);



        btnadd=findViewById(R.id.add);
        btnsend=findViewById(R.id.send);
        edtmessage=findViewById(R.id.messagebar);

        mCurrentuserid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        list=new ArrayList<>();
        laodmessage();
        adapter=new MessageAdapter(list,this);
        chatRecycler.setAdapter(adapter);

        Log.d(TAG, "onCreate: -----------------------------------------------------------username------"+username);

        getSupportActionBar().setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.custactionbarforchatactivity,null);
        getSupportActionBar().setCustomView(view);

        TextView custtitle = view.findViewById(R.id.displayname);
        final TextView lastseen= view.findViewById(R.id.lastseen);
        profileimg=view.findViewById(R.id.custbar);
        Glide.with(getApplicationContext()).load(imgthumb).into(profileimg);
        custtitle.setText(username);

        mRootref.child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String online = dataSnapshot.child("online").getValue().toString();
                String thmimg= dataSnapshot.child("thumb_img").getValue().toString();

                if(online.equals("true")){

                    lastseen.setText("online");
                }else{
                    GettimeAgo timeago = new GettimeAgo();
                    long lasttime = Long.parseLong(online);
                    String setvaluetime = timeago.getTimeago(lasttime,getApplicationContext());
                    if(setvaluetime==null){
                        lastseen.setText("last seen "+ "a second ago");
                    }else if(setvaluetime!=null){
                        lastseen.setText("last seen "+setvaluetime);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mChatdb.child("Chat").child(mCurrentuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild(userid)){
                    Map chatAddmap = new HashMap();

                    chatAddmap.put("seen",false);
                    chatAddmap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUsermap=new HashMap();
                    chatUsermap.put("Chat/"+mCurrentuserid +"/"+userid,chatAddmap);
                    chatUsermap.put("Chat/"+userid+"/"+mCurrentuserid,chatAddmap);



                    mChatdb.updateChildren(chatUsermap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if(databaseError==null){
                                Toast.makeText(ChatActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentpage++;
                list.clear();
                laodmessage();
            }
        });


    }

    private void sendMessage() {

         String message = edtmessage.getText().toString();


        if(!TextUtils.isEmpty(message)){


            String currentUserref= "Message/"+mCurrentuserid+"/"+userid;
            String otheruserref="Message/"+userid+"/"+mCurrentuserid;

            DatabaseReference pusid=mChatdb.child("message").child(mCurrentuserid).child(userid).push();

            String getPushid= pusid.getKey();

            Map messageMap= new HashMap();
            messageMap.put("message",message);
            messageMap.put("seen",false);
            messageMap.put("type","text");
            messageMap.put("time",ServerValue.TIMESTAMP);
            messageMap.put("from",mCurrentuserid);


            Map massageUsermap = new HashMap();

            massageUsermap.put(currentUserref+"/"+getPushid,messageMap);
            massageUsermap.put(otheruserref+"/"+getPushid,messageMap);

            edtmessage.setText("");

            mChatdb.updateChildren(massageUsermap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                    if(databaseError==null){
                        Toast.makeText(ChatActivity.this, "message sent", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

     private void laodmessage(){


         mChatdb.child("Message").child(mCurrentuserid).child(userid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
               if(dataSnapshot.exists()){
                   Messagerretrive message = dataSnapshot.getValue(Messagerretrive.class);

                   list.add(message);
                   adapter.notifyDataSetChanged();
                   chatRecycler.scrollToPosition(list.size()-1);
                   refreshLayout.setRefreshing(false);
               }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
     }


}
