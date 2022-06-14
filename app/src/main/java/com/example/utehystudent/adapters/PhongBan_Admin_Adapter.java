package com.example.utehystudent.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;
import com.example.utehystudent.activity.admin.QLLienHe_Activity;
import com.example.utehystudent.model.Department;

import java.util.ArrayList;

public class PhongBan_Admin_Adapter extends RecyclerView.Adapter<PhongBan_Admin_Adapter.BoMonAdminViewHolder>{
    Context context;
    ArrayList<Department> listDepartment;

    public PhongBan_Admin_Adapter(Context context) {
        this.context = context;
        listDepartment = new ArrayList<>();
    }

    public void setData(ArrayList<Department> listDepartment) {
        this.listDepartment = listDepartment;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PhongBan_Admin_Adapter.BoMonAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_bomon, parent, false);

        PhongBan_Admin_Adapter.BoMonAdminViewHolder viewHolder = new PhongBan_Admin_Adapter.BoMonAdminViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PhongBan_Admin_Adapter.BoMonAdminViewHolder holder, int position) {
        Department department = listDepartment.get(position);
        if (department != null) {
            holder.btnBoMon.setText(department.getDepartment_name());
        }

        holder.btnBoMon.setOnClickListener(view -> {
            ((QLLienHe_Activity) context).clickDepartment(department);
        });
    }

    @Override
    public int getItemCount() {
        if (listDepartment.size() > 0) {
            return listDepartment.size();
        }
        return 0;
    }

    public class BoMonAdminViewHolder extends RecyclerView.ViewHolder {
        Button btnBoMon;
        public BoMonAdminViewHolder(@NonNull View itemView) {
            super(itemView);
            btnBoMon = itemView.findViewById(R.id.itemBoMon_btn);
        }
    }
}
