package com.mrbell.hellobello;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;


public class FriendsFragment extends Fragment {

    private RecyclerView mFrndlistrv;
    private DatabaseReference mfrndlistdb,mUserdb;
    private FirebaseAuth mAuth;
    private String mCurrentuser;
    private View mMainview;
    private FirebaseRecyclerOptions<Frndlist> option;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainview=inflater.inflate(R.layout.fragment_friends, container, false);

        mFrndlistrv=mMainview.findViewById(R.id.frndsfragmentrecyclerview);
        mAuth=FirebaseAuth.getInstance();
        mCurrentuser=mAuth.getCurrentUser().getUid();
        mfrndlistdb= FirebaseDatabase.getInstance().getReference().child("Friendslist").child(mCurrentuser);
        mfrndlistdb.keepSynced(true);
        mUserdb=FirebaseDatabase.getInstance().getReference().child("Users");
        mUserdb.keepSynced(true);

        mFrndlistrv.setHasFixedSize(true);
        mFrndlistrv.setLayoutManager(new LinearLayoutManager(getContext()));


        return mMainview;

    }

    @Override
    public void onStart() {
        super.onStart();
        option= new FirebaseRecyclerOptions.Builder<Frndlist>().setQuery(mfrndlistdb,Frndlist.class).build();

        FirebaseRecyclerAdapter<Frndlist,FrindlistViewholder> fadapter = new FirebaseRecyclerAdapter<Frndlist, FrindlistViewholder>(option) {
            @Override
            protected void onBindViewHolder(@NonNull final FrindlistViewholder holder, int position, @NonNull Frndlist model) {
               holder.tvStatus.setText("Friend Science "+model.getDate());

                final String userid= getRef(position).getKey();

                mUserdb.child(userid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                            final String name = dataSnapshot.child("name").getValue().toString();
                            String userthum= dataSnapshot.child("thumb_img").getValue().toString();
                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            final String thumimg= dataSnapshot.child("thumb_img").getValue().toString();

                            holder.tvName.setText(name);
                           if(isAdded()){

                               Glide.with(FriendsFragment.this).load(userthum).into(holder.imgview);
                           }
                            if(userOnline.equals("true")){
                                holder.imgview.setBorderColor(Color.parseColor("#4AAF4B"));
                                holder.onlinestate.setVisibility(View.VISIBLE);
                                holder.onlinestate.setImageResource(R.drawable.onlineind);

                            }else {
                                holder.onlinestate.setVisibility(View.INVISIBLE);
                                holder.imgview.setBorderColor(Color.parseColor("#fb2710"));
                            }



                                holder.view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        CharSequence option[] = new CharSequence[]{"See Profile","Send Message"};

                                        final AlertDialog.Builder albuilder = new AlertDialog.Builder(getContext());

                                        /*Rect displayRectangle = new Rect();
                                        Window window = getActivity().getWindow();
                                        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);*/


                                        View alview = getLayoutInflater().inflate(R.layout.alterdialog,null);

                                        Button btnprofile=alview.findViewById(R.id.seeprofile);
                                        Button btnsendmessage=alview.findViewById(R.id.SendMessage);



                                        albuilder.setView(alview);
                                        final AlertDialog dialog = albuilder.create();
                                        dialog.show();

                                        btnprofile.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                startActivity(new Intent(getContext(),UserprofiledetailsActivity.class).putExtra("id",userid));
                                                dialog.dismiss();


                                            }
                                        });

                                        btnsendmessage.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                startActivity(new Intent(getContext(),ChatActivity.class).putExtra("id",userid).putExtra("name",name).putExtra("imglink",thumimg));
                                                dialog.dismiss();
                                            }
                                        });




                                        Toast.makeText(getContext(), "item is clicked", Toast.LENGTH_SHORT).show();
                                    }
                                });

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




            }

            @NonNull
            @Override
            public FrindlistViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.firebaselayoutforuser,viewGroup,false);
                FrindlistViewholder holder = new FrindlistViewholder(view);
                return holder;
            }
        };

        fadapter.startListening();
        mFrndlistrv.setAdapter(fadapter);

    }

    public static class FrindlistViewholder extends RecyclerView.ViewHolder{

        CircleImageView imgview;
        TextView tvName,tvStatus;
        ImageView onlinestate;
        View view;
        public FrindlistViewholder(@NonNull View itemView) {
            super(itemView);

            view=itemView;

            imgview=view.findViewById(R.id.listprofileimg);
            tvName=view.findViewById(R.id.listName);
            tvStatus=view.findViewById(R.id.tvStatus);
            onlinestate=view.findViewById(R.id.onlineindicator);

        }





    }

}
