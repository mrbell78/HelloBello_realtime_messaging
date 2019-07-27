package com.mrbell.hellobello;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SampleAdapter extends RecyclerView.Adapter<SampleAdapter.Customadapter> {

    private static final String TAG = "SampleAdapter";

    private List<Modeluserclass>list;
    private Context context;

    public SampleAdapter( List<Modeluserclass> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public Customadapter onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.firebaselayoutforuser,viewGroup,false);
        context=viewGroup.getContext();
        return new Customadapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Customadapter customadapter, int i) {

        Log.d(TAG, "onBindViewHolder: data is=="+list.get(i).getName());
        Glide.with(context).load(R.drawable.chatimg).into(customadapter.imgview);
        customadapter.tvName.setText("nur");
        customadapter.tvstatus.setText("hello this is nur");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Customadapter extends RecyclerView.ViewHolder {

        CircleImageView imgview;
        TextView tvName,tvstatus;
        public Customadapter(@NonNull View itemView) {
            super(itemView);

            imgview=itemView.findViewById(R.id.listprofileimg);
            tvName=itemView.findViewById(R.id.listName);
            tvstatus=itemView.findViewById(R.id.tvStatus);
        }
    }
}
