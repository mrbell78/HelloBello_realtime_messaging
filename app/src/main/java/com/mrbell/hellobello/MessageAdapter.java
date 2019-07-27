package com.mrbell.hellobello;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.Customviewholder> {

    private List<Messagerretrive> messagestuff;
    private Context context;
    private DatabaseReference mRoot;

    public MessageAdapter(List<Messagerretrive> messagestuff, Context context) {
        this.messagestuff = messagestuff;
        this.context = context;
    }

    @NonNull
    @Override
    public Customviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.messageshowerlayout,viewGroup,false);

        Customviewholder holder = new Customviewholder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final Customviewholder customviewholder, int i) {



        String message = messagestuff.get(i).getMessage();
         String mCurrentuserid= FirebaseAuth.getInstance().getCurrentUser().getUid();
         String fromUser=messagestuff.get(i).getFrom();



        if(fromUser.equals(mCurrentuserid)){

            RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.setMargins(190,0,20,0);
            customviewholder.cardView.setLayoutParams(params);
            customviewholder.cardView.setCardBackgroundColor(Color.WHITE);
            customviewholder.message.setTextColor(Color.BLACK);
            customviewholder.imgview.setVisibility(View.INVISIBLE);


        }else{

            DatabaseReference userdbforpic=FirebaseDatabase.getInstance().getReference().child("Users");

            userdbforpic.child(fromUser).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        String thumpic = dataSnapshot.child("thumb_img").getValue().toString();
                        String onlinestate=dataSnapshot.child("online").getValue().toString();

                        if(onlinestate.equals("true")){
                            customviewholder.imgview.setBorderColor(Color.parseColor("#4AAF4B"));
                            Glide.with(context).load(thumpic).into(customviewholder.imgview);
                        }else {
                            customviewholder.imgview.setBorderColor(Color.parseColor("#fb2710"));
                            Glide.with(context).load(thumpic).into(customviewholder.imgview);
                        }


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.setMargins(170,0,190,0);

           customviewholder.cardView.setLayoutParams(params);
            customviewholder.cardView.setCardBackgroundColor(Color.parseColor("#dd2c00"));
            customviewholder.message.setTextColor(Color.WHITE);
            customviewholder.imgview.setVisibility(View.VISIBLE);
        }
        customviewholder.message.setText(message);


    }

    @Override
    public int getItemCount() {
        return messagestuff.size();
    }

    public class Customviewholder extends RecyclerView.ViewHolder {
        View mView;
        CircleImageView imgview;
        TextView message;
        CardView cardView;
        public Customviewholder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;

            imgview=mView.findViewById(R.id.crmessageprofile);
            message=mView.findViewById(R.id.messageshow);
            cardView=mView.findViewById(R.id.carmessagershower);


        }
    }
}
