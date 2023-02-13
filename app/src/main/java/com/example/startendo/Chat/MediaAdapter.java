package com.example.startendo.Chat;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.startendo.R;

import java.util.ArrayList;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder> {
    Context context;
    ArrayList<String> mediaList;

    public MediaAdapter(Context context2, ArrayList<String> arrayList) {
        this.context = context2;
        this.mediaList = arrayList;
    }

    public MediaViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MediaViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_media, (ViewGroup) null, false));
    }

    public void onBindViewHolder(MediaViewHolder mediaViewHolder, int i) {
        Glide.with(this.context).load(Uri.parse(this.mediaList.get(i))).into(mediaViewHolder.mMedia);
    }

    public int getItemCount() {
        return this.mediaList.size();
    }

    public class MediaViewHolder extends RecyclerView.ViewHolder {
        ImageView mMedia;

        public MediaViewHolder(View view) {
            super(view);
            this.mMedia = (ImageView) view.findViewById(R.id.media);
        }
    }
}
