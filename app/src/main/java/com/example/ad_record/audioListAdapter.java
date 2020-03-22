package com.example.ad_record;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

public class audioListAdapter extends RecyclerView.Adapter<audioListAdapter.AudioViewHolder>{

    File[] files;
    private onItemClick onItemClick;
    AD_time_ago time_ago;
    public audioListAdapter(File[] files,onItemClick onItemClick)
    {
        this.files = files;
        this.onItemClick = onItemClick;
    }
    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_recordfile,parent,false);
        time_ago = new AD_time_ago();
        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        holder.title.setText(files[position].getName());
        holder.date.setText(time_ago.getTime(files[position].lastModified())); // it passes the last modified time of the given file
    }

    @Override
    public int getItemCount() {
        return files.length;
    }

    public class AudioViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView title;
        TextView date;
        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.eachRecordImage);
            title = itemView.findViewById(R.id.eachRecordTitle);
            date = itemView.findViewById(R.id.eachRecordDate);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick.onClickListener(files[getAdapterPosition()],getAdapterPosition());
                }
            });
        }
    }

    public interface onItemClick
    {
        void onClickListener(File file,int position);
    }
}
