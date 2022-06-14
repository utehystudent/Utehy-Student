package com.example.utehystudent.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;
import com.example.utehystudent.activity.admin.QLSV_Activity;
import com.example.utehystudent.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SinhVienQL_Adapter extends RecyclerView.Adapter<SinhVienQL_Adapter.SinhVienQLViewHolder> {
    Context context;
    ArrayList<User> listUser;

    public SinhVienQL_Adapter(Context context) {
        this.context = context;
    }

    public void setData(ArrayList<User> list) {
        this.listUser = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SinhVienQLViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.row_student_quanly, parent, false);

        SinhVienQLViewHolder viewHolder = new SinhVienQLViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SinhVienQLViewHolder holder, int position) {
        User user = listUser.get(position);
        if (user != null) {
            try {
                Picasso.get().load(user.getAvt_link()).noFade().into(holder.imgAvt);
            }catch (Exception e) {
                holder.imgAvt.setImageResource(R.drawable.ic_student);
            }
            holder.tvName.setText(user.getName());
            holder.tvMaSV.setText("Mã SV: "+user.getUsername());
            holder.tvLop.setText("Lớp: "+user.getClass_ID());
            holder.item.setOnClickListener(view -> {
                ((QLSV_Activity) context).changeToDetail(user);
            });
        }
    }

    @Override
    public int getItemCount() {
        if (listUser.size() > 0) {
            return listUser.size();
        }
        return 0;
    }


    public class SinhVienQLViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgAvt;
        TextView tvName, tvLop, tvMaSV;
        LinearLayout item;
        public SinhVienQLViewHolder(@NonNull View itemView) {
            super(itemView);

            imgAvt = itemView.findViewById(R.id.rowSVQL_imgAvt);
            tvName = itemView.findViewById(R.id.rowSVQL_tvName);
            tvLop = itemView.findViewById(R.id.rowSVQL_tvClassID);
            tvMaSV = itemView.findViewById(R.id.rowSVQL_tvMaSV);
            item = itemView.findViewById(R.id.rowSVQL_item);
        }
    }
}
