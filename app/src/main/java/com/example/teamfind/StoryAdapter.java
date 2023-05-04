package com.example.teamfind;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.MyViewHolder> {

    Context context;
    OnItemClickListener mOnItemClickListener;
    ArrayList<StoryReceiver> list;

    public StoryAdapter(Context context, ArrayList<StoryReceiver> list, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.list = list;
        this.mOnItemClickListener = onItemClickListener;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item,parent, false);
        return new MyViewHolder(v, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        StoryReceiver storyReceiver = list.get(position);
        holder.title.setText(storyReceiver.getTitle());
        holder.genre.setText(storyReceiver.getGenre());
        holder.content.setText(storyReceiver.getContent());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        TextView title, content, genre;
        OnItemClickListener onItemClickListener;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);

            title = itemView.findViewById(R.id.textViewTitle);
            content = itemView.findViewById(R.id.textViewContent);
            genre = itemView.findViewById(R.id.textViewGenre);
            this.onItemClickListener = onItemClickListener;

            itemView.setOnClickListener(this);
        }

        public void onClick(View view){
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}
