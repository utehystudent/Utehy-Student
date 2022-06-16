package com.example.utehystudent.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;
import com.example.utehystudent.model.Department;

import java.util.ArrayList;

public class DepartmentAdmin_Adapter extends RecyclerView.Adapter<DepartmentAdmin_Adapter.DepartmentAdminViewHolder>{
    ArrayList<Department> listDepartment;
    Context context;

    public DepartmentAdmin_Adapter(Context context) {
        this.context = context;
    }

    public void setData(ArrayList<Department> list) {
        this.listDepartment = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DepartmentAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_ql_phongban, parent, false);

        DepartmentAdmin_Adapter.DepartmentAdminViewHolder viewHolder = new DepartmentAdmin_Adapter.DepartmentAdminViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DepartmentAdminViewHolder holder, int position) {
        Department department = listDepartment.get(position);
        if (department == null) {
            return;
        }
        holder.tvName.setText(department.getDepartment_name());
        holder.tvID.setText("Mã bộ môn: "+department.getDepartment_id());
    }


    @Override
    public int getItemCount() {
        if (listDepartment.size() != 0) {
            return listDepartment.size();
        }
        return 0;
    }

    public class DepartmentAdminViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvID;
        public DepartmentAdminViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.rowPB_tvTenPB);
            tvID = itemView.findViewById(R.id.rowPB_tvMaPB);
        }
    }
}
