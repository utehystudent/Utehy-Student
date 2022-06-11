package com.example.utehystudent.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;
import com.example.utehystudent.fragments.BangTinFragment;
import com.example.utehystudent.model.BaiViet;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BaiVietAdapter extends RecyclerView.Adapter<BaiVietAdapter.BaiVietViewHolder> {
    ArrayList<BaiViet> listBV;
    Fragment fragment;
    FirebaseFirestore db;

    public BaiVietAdapter(Fragment activity, ArrayList<BaiViet> listBV) {
        this.fragment = activity;
        this.listBV = listBV;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public BaiVietViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View studentView = inflater.inflate(R.layout.item_baiviet, parent, false);

        BaiVietViewHolder viewHolder = new BaiVietViewHolder(studentView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BaiVietViewHolder holder, int position) {
        final int pos = position;
        SharedPreferences pref = fragment.requireActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
        String username = pref.getString("username", "");
        final boolean[] isLiked = new boolean[1];
        Log.d("vvv", "onBindViewHolder: "+listBV.get(position).toString());
        final BaiViet[] bv = {listBV.get(position)};

        if (bv[0] == null) {
            return;
        }

        try {
            Picasso.get().load(bv[0].getLinkAnhNguoiDang()).resize(200, 200).centerCrop().into(holder.imgAvt);
        } catch (Exception e) {
            holder.imgAvt.setImageResource(R.drawable.ic_student);
        }
        holder.tenNguoiDang.setText(bv[0].getTenNguoiDang());


        holder.tvNgay.setText(bv[0].getNgayDang());

        if (bv[0].getNoiDung().equals("")) {
            holder.tvND.setVisibility(View.GONE);
        }else {
            holder.tvND.setText(bv[0].getNoiDung());
        }

        if (bv[0].getLinkAnh().size() >= 1) {
            try {
                Picasso.get().load(bv[0].getLinkAnh().get(0)).into(holder.imgAnhBV);
            } catch (Exception e) {
                holder.imgAnhBV.setImageResource(R.drawable.image_err);
            }
            if (bv[0].getLinkAnh().size() == 1) {
                holder.tvSoAnhThem.setVisibility(View.GONE);
            }else {
                holder.tvSoAnhThem.setText(bv[0].getLinkAnh().size()-1+"+");
                holder.tvSoAnhThem.setVisibility(View.VISIBLE);
            }
        } else if (bv[0].getLinkAnh().size() == 0) {
            holder.imgAnhBV.setVisibility(View.GONE);
        }

        holder.tvSoLike.setText(bv[0].getListLike().size()+"");
        holder.tvSoCmt.setText(bv[0].getSoBinhLuan()+"");


        if (bv[0].getListLike().contains(username)) {
            holder.imbLike.setImageResource(R.drawable.ic_like_fill);
            isLiked[0] = true;
        }else {
            holder.imbLike.setImageResource(R.drawable.ic_like);
            isLiked[0] = false;
        }

        holder.imbLike.setOnClickListener(view -> {
            int numLike = Integer.parseInt(holder.tvSoLike.getText().toString());

            if (isLiked[0] == true) {
                isLiked[0] = false;
                holder.imbLike.setImageResource(R.drawable.ic_like);
                bv[0].getListLike().remove(username);
                numLike--;
                holder.tvSoLike.setText(numLike+"");
                db.collection("Post")
                        .document(bv[0].getIdBaiViet())
                        .update("listLike", FieldValue.arrayRemove(username));
            }else {
                isLiked[0] = true;
                holder.imbLike.setImageResource(R.drawable.ic_like_fill);
                bv[0].getListLike().add(username);
                numLike++;
                holder.tvSoLike.setText(numLike+"");
                db.collection("Post")
                        .document(bv[0].getIdBaiViet())
                        .update("listLike", FieldValue.arrayUnion(username));
            }
        });

        holder.cardView.setOnClickListener(view -> {
            ((BangTinFragment) this.fragment).viewPost(bv[0]);
        });

        holder.imgAnhBV.setOnClickListener(click -> {
            ((BangTinFragment) this.fragment).viewImage(bv[0].getLinkAnh());
        });

        db.collection("Post")
                .whereEqualTo("idBaiViet", bv[0].getIdBaiViet())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("BVAdapter", "listen:error", e);
                            return;
                        }
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case MODIFIED:
                                    BaiViet bai = dc.getDocument().toObject(BaiViet.class);
                                    bv[0] = bai;
                                   listBV.set(pos, bai);
                                    //notifyItemChanged(pos);
                                    notifyDataSetChanged();
                                    break;
                                case REMOVED:
                                    listBV.remove(bv[0]);
                                    notifyDataSetChanged();
                                    break;
                            }
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        Log.d("xxx", "getItemCount: "+listBV.size());
        return listBV.size();
    }

    public class BaiVietViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvt, imgAnhBV;
        TextView tenNguoiDang, tvNgay, tvND, tvSoLike, tvSoCmt, tvSoAnhThem;
        ImageButton imbLike, imbCmt;
        CardView cardView;

        public BaiVietViewHolder(@NonNull View itemView) {
            super(itemView);

            imgAvt = itemView.findViewById(R.id.itemBV_imgAvt);
            imgAnhBV = itemView.findViewById(R.id.itemBV_imgAnhBV);

            tenNguoiDang = itemView.findViewById(R.id.itemBV_tvTen);
            tvNgay = itemView.findViewById(R.id.itemBV_tvNgay);
            tvND = itemView.findViewById(R.id.itemBV_tvContent);
            tvSoLike = itemView.findViewById(R.id.itemBV_tvNumLike);
            tvSoCmt = itemView.findViewById(R.id.itemBV_tvNumCmt);
            tvSoAnhThem = itemView.findViewById(R.id.itemBV_numMoreImg);

            imbLike = itemView.findViewById(R.id.itemBV_imb_like);
            imbCmt = itemView.findViewById(R.id.itemBV_imb_comment);

            cardView = itemView.findViewById(R.id.itemBV_cardView);

        }
    }

}