package com.example.mycontacts;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class History_Adapter extends RecyclerView.Adapter<History_Adapter.MyViewHolder> {
    private ArrayList<Call_History> items;
    private Context context;
    private ItemClickListner clickListner;
    private DatabaseReference database;
    private FirebaseUser fuser;
    private String node_id;
    History_Adapter adapter;

    public History_Adapter(ArrayList<Call_History> items, Context context) {
        this.items = items;
        this.context = context;
    }

    public void setClickListner(ItemClickListner clickListner) {
        this.clickListner = clickListner;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.raw_history, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference("History");
        Call_History u = items.get(position);
        holder.contact_name.setText(u.getC_name());
        holder.time.setText(u.getDate_time());
        node_id = u.getNode_id();
        int pos = position;
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("id", "pos:" + pos);
                Call_History u = items.get(pos);
                node_id = u.getNode_id();
                Log.d("id", "node :" + node_id);
                database = FirebaseDatabase.getInstance().getReference("History").child(node_id);
                database.removeValue();
                clearList(items);
            }
        });
        Glide.with(context).load(u.getCc_image()).into(holder.contact_image);


    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView contact_name, time, delete;
        ImageView contact_image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.contact_name = itemView.findViewById(R.id.history_name);
            this.time = itemView.findViewById(R.id.history_time);
            this.contact_image = itemView.findViewById(R.id.contact_image);
            this.delete = itemView.findViewById(R.id.contact_delete_history);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListner != null) {
                clickListner.onClick(view, getAdapterPosition());
            }
        }
    }

    public void clearList(ArrayList<Call_History> items) {
        items.clear();
        notifyDataSetChanged(); // Notify the adapter that the data set has changed
    }
}













