package com.example.utehystudent.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;

import java.io.IOException;
import java.util.ArrayList;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    private Context mContext;
    private ArrayList<Uri> mListPhotos;

    public PhotoAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(ArrayList<Uri> list) {
        this.mListPhotos = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Uri uri = mListPhotos.get(position);
        if (uri == null) {
            return;
        }
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
            if (bitmap != null) {
                holder.imgPhoto.setImageBitmap(bitmap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        holder.imbCancel.setOnClickListener(view -> {
            mListPhotos.remove(position);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        if (mListPhotos == null) {
            return 0;
        }else {
            return mListPhotos.size();
        }
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgPhoto;
        private ImageButton imbCancel;
        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.itemPhoto_imgPhoto);
            imbCancel = itemView.findViewById(R.id.itemPhoto_imbRemove);
        }
    }
}
