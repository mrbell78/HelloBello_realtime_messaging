package com.mrbell.hellobello;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllusersActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView recyclerView;
    private DatabaseReference dbrf,selfdatabaserf;
    private FirebaseRecyclerOptions<Modeluserclass> option;
    private FirebaseUser mUser;
    private FirebaseRecyclerAdapter<Modeluserclass,UserViewholder> fadapter;
    private  static final String TAG = "AllusersActivity";

    private List<Modeluserclass>list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        mUser=FirebaseAuth.getInstance().getCurrentUser();
        setContentView(R.layout.activity_allusers);
        mToolbar=findViewById(R.id.alluserToolbar);
        recyclerView=findViewById(R.id.alluserlist);
        dbrf= FirebaseDatabase.getInstance().getReference().child("Users");

        dbrf.keepSynced(true);




        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        list=new ArrayList<>();
        RecyclerView.LayoutManager manager =new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
      // SampleAdapter adapter = new SampleAdapter(list);


    }

    @Override
    protected void onStart() {
        super.onStart();

        option =new FirebaseRecyclerOptions.Builder<Modeluserclass>().setQuery(dbrf,Modeluserclass.class).build();
        fadapter = new FirebaseRecyclerAdapter<Modeluserclass, UserViewholder>(option) {
            @Override
            protected void onBindViewHolder(@NonNull final UserViewholder holder, int position, @NonNull Modeluserclass model) {

                holder.tvName.setText(model.getName());
                holder.tvStatus.setText(model.getStatus());

                Glide.with(AllusersActivity.this).load(model.getThumimg()).into(holder.imgview);


                final String userid=getRef(position).getKey();

                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        startActivity(new Intent(getApplicationContext(),UserprofiledetailsActivity.class).putExtra("id",userid));
                }
                });

            }

            @NonNull
            @Override
            public UserViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.firebaselayoutforuser,viewGroup,false);

                UserViewholder holder = new UserViewholder(v);
                return holder;
            }
        };

        fadapter.startListening();
        //recyclerView.addItemDecoration(new DividerItemDecoration(AllusersActivity.this,LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(fadapter);

    }


    public static class UserViewholder extends  RecyclerView.ViewHolder{

        CircleImageView imgview;
        TextView tvName,tvStatus;
        ImageView onlinestate;
        View view;
        public UserViewholder(@NonNull View itemView) {
            super(itemView);

            view=itemView;

            imgview=view.findViewById(R.id.listprofileimg);
            tvName=view.findViewById(R.id.listName);
            tvStatus=view.findViewById(R.id.tvStatus);
            onlinestate=view.findViewById(R.id.onlineindicator);

        }
    }


}

