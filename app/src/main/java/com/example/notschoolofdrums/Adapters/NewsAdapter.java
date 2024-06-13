package com.example.notschoolofdrums.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.notschoolofdrums.R;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {

    Context context;
    ArrayList<NewsForAdapter> newsArrayList;

    public NewsAdapter(Context context, ArrayList<NewsForAdapter> newsArrayList) {
        this.context = context;
        this.newsArrayList = newsArrayList;
    }

    @NonNull
    @Override
    public NewsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.news_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.MyViewHolder holder, int position) {
        NewsForAdapter newsForAdapter = newsArrayList.get(position);
        holder.datetime.setText(newsForAdapter.getDateTime());
        holder.postText.setText(newsForAdapter.getPostText());
        if(newsForAdapter.getImageUri() != null){
            Glide.with(context)
                    .load(newsForAdapter.getImageUri())
                    .into(holder.imagePost);
        }
    }

    @Override
    public int getItemCount() {
        return newsArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView datetime, postText;
        ImageView imagePost;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            datetime = itemView.findViewById(R.id.datetime_card);
            postText = itemView.findViewById(R.id.text_news);
            imagePost = itemView.findViewById(R.id.card_image);
        }
    }
}
