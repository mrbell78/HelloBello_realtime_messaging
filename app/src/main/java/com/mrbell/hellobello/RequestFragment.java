package com.mrbell.hellobello;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {
    private static final String TAG = "RequestFragment";
    private FirebaseRecyclerOptions<Modelclassforfrndrequest> options;
    private DatabaseReference frndrequestdb,Userdb,senderUserdb;
    private String userid;
    private RecyclerView recyclerView;
    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view= inflater.inflate(R.layout.fragment_request, container, false);

        frndrequestdb=FirebaseDatabase.getInstance().getReference().child("Friend_req").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        final String mCurrentUser= FirebaseAuth.getInstance().getCurrentUser().getUid();

        recyclerView=view.findViewById(R.id.requestrecyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        Userdb=FirebaseDatabase.getInstance().getReference().child("Users");


        frndrequestdb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("sender")){

                    userid = dataSnapshot.getValue().toString();
                  //  senderUserdb=FirebaseDatabase.getInstance().getReference().child("Friend_req").child(userid);
                    Log.d(TAG, "onDataChange: -------------------------------------u have frnd request the id is--------------------------------"+userid);


                }else{
                    Log.d(TAG, "onDataChange: ------------------------------------------------u dont have any frnd request-----------------------------");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Log.d(TAG, "onCreateView: ------------------------confirm mCurrentuserid--------------------------------------------"+mCurrentUser);

        return view;


    }


    @Override
    public void onStart() {
        super.onStart();

        options = new FirebaseRecyclerOptions.Builder<Modelclassforfrndrequest>().setQuery(frndrequestdb,Modelclassforfrndrequest.class).build();

        FirebaseRecyclerAdapter<Modelclassforfrndrequest,Userviewholderforreq> adapter = new FirebaseRecyclerAdapter<Modelclassforfrndrequest, Userviewholderforreq>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final Userviewholderforreq holder, int position, @NonNull Modelclassforfrndrequest model) {

                final String userid=model.getSender();

                if(userid!=null){
                    Log.d(TAG, "onBindViewHolder: ------------------------------------modeluser------------------------------"+userid);


                   /* Userdb.child(userid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                String name = dataSnapshot.child("name").getValue().toString();
                                String thumbimg=dataSnapshot.child("thumb_img").getValue().toString();
                                holder.tvName.setText(name);
                                if(isAdded()){
                                    Glide.with(getContext()).load(thumbimg).into(holder.imgview);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });*/
                }



            }

            @NonNull
            @Override
            public Userviewholderforreq onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.frndlist_request,viewGroup,false);
                Userviewholderforreq holder = new Userviewholderforreq(view);
                return holder;
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    public static class Userviewholderforreq extends RecyclerView.ViewHolder{

        CircleImageView imgview;
        TextView tvName;
        Button btnAccept,btnCancel;

        View view;

        public Userviewholderforreq(@NonNull View itemView) {
            super(itemView);

            view=itemView;

            imgview=view.findViewById(R.id.listprofileimg);
            tvName=view.findViewById(R.id.tvName);
            btnAccept=view.findViewById(R.id.confirmrequest);
            btnCancel=view.findViewById(R.id.cancelrequest);
        }
    }
}
