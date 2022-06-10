package com.example.utehystudent.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;
import com.example.utehystudent.model.Comment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    Context context;
    ArrayList<Comment> list;

    public CommentAdapter(Context context, ArrayList<Comment> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_binh_luan, parent, false);

        CommentViewHolder viewHolder = new CommentViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = list.get(position);
        if (comment == null) {
            return;
        }

        try {
            Picasso.get().load(comment.getLinkAnhNguoiCmt()).resize(180, 180).centerCrop().into(holder.imgAvt);
        } catch (Exception e) {
            holder.imgAvt.setImageResource(R.drawable.ic_student);
        }
        holder.tvTen.setText(comment.getTenNguoiCmt());
        holder.tvND.setText(comment.getNoiDung());
        holder.tvNgay.setText(comment.getNgayCmt());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvt;
        TextView tvTen, tvND, tvNgay;
        public CommentViewHolder(@NonNull View view) {
            super(view);

            imgAvt = view.findViewById(R.id.itemCmt_imgAvt);
            tvTen = view.findViewById(R.id.itemCmt_tvTenNguoiCmt);
            tvND = view.findViewById(R.id.itemCmt_noiDung);
            tvNgay = view.findViewById(R.id.itemCmt_tvGio);
        }
    }
}
