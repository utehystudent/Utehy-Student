package com.example.utehystudent.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;
import com.example.utehystudent.activity.admin.PostList_Activity;
import com.example.utehystudent.model.BaiViet;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ThongBaoAdmin_Adapter extends RecyclerView.Adapter<ThongBaoAdmin_Adapter.ThongBaoAdminViewHolder> {
    ArrayList<BaiViet> listBV;
    Context context;
    FirebaseFirestore db;

    public ThongBaoAdmin_Adapter(Context activity, ArrayList<BaiViet> listBV) {
        this.context = activity;
        this.listBV = listBV;
        db = FirebaseFirestore.getInstance();
    }


    @NonNull
    @Override
    public ThongBaoAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View studentView = inflater.inflate(R.layout.item_thongbao_admin, parent, false);

        ThongBaoAdminViewHolder viewHolder = new ThongBaoAdminViewHolder(studentView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ThongBaoAdminViewHolder holder, int position) {
        BaiViet baiViet = listBV.get(position);
        if (baiViet != null) {
            final int pos = position;

            Log.d("vvv", "onBindViewHolder: " + listBV.get(position).toString());
            BaiViet bv = listBV.get(position);

            if (bv == null) {
                return;
            }

            holder.tvNgay.setText(bv.getNgayDang());

            if (bv.getNoiDung().equals("")) {
                holder.tvND.setVisibility(View.GONE);
            } else {
                holder.tvND.setText(bv.getNoiDung());
            }

            if (bv.getLinkAnh().size() >= 1) {
                try {
                    Picasso.get().load(bv.getLinkAnh().get(0)).into(holder.imgAnhBV);
                } catch (Exception e) {
                    holder.imgAnhBV.setImageResource(R.drawable.image_err);
                }
                if (bv.getLinkAnh().size() == 1) {
                    holder.tvSoAnhThem.setVisibility(View.GONE);
                } else {
                    holder.tvSoAnhThem.setText(bv.getLinkAnh().size() - 1 + "+");
                    holder.tvSoAnhThem.setVisibility(View.VISIBLE);
                }
            } else if (bv.getLinkAnh().size() == 0) {
                holder.imgAnhBV.setVisibility(View.GONE);
                holder.tvSoAnhThem.setVisibility(View.GONE);
            }

            holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ((PostList_Activity) ThongBaoAdmin_Adapter.this.context).openBottomSheetDialog(baiViet);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        Log.d("xxx", "getItemCount: " + listBV.size());
        return listBV.size();
    }

    public class ThongBaoAdminViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAnhBV;
        TextView tvNgay, tvND, tvSoAnhThem;
        CardView cardView;

        public ThongBaoAdminViewHolder(@NonNull View itemView) {
            super(itemView);

            imgAnhBV = itemView.findViewById(R.id.itemBV_imgAnhBV_admin);

            tvNgay = itemView.findViewById(R.id.itemBV_tvNgay_admin);
            tvND = itemView.findViewById(R.id.itemBV_tvContent_admin);
            tvSoAnhThem = itemView.findViewById(R.id.itemBV_numMoreImg_admin);

            cardView = itemView.findViewById(R.id.itemBV_cardView_admin);

        }
    }
}
