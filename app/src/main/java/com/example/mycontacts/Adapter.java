package com.example.mycontacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {
    private ArrayList<userinfo> items;


    private Context context;
    private ItemClickListner clickListner;


    public Adapter(ArrayList<userinfo> items, Context context) {
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
        View view = inflater.inflate(R.layout.raw_contact, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        userinfo u = items.get(position);
        holder.contact_name.setText(u.getName());
        Glide.with(context).load(u.getImageurl()).into(holder.contact_image);

    }

    @Override
    public int getItemCount() {

        return items.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView contact_name;
        ImageView contact_image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.contact_name = itemView.findViewById(R.id.contact_name);
            this.contact_image = itemView.findViewById(R.id.contact_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListner != null) {
                clickListner.onClick(view, getAdapterPosition());
            }

        }

    }
}

