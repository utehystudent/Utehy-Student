package com.example.utehystudent.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;
import com.example.utehystudent.activity.ContactActivity;
import com.example.utehystudent.model.Department;

import java.util.ArrayList;

public class PhongBanAdapter extends RecyclerView.Adapter<PhongBanAdapter.BoMonViewHolder> {
    Context context;
    ArrayList<Department> listDepartment;

    public PhongBanAdapter(Context context) {
        this.context = context;
        listDepartment = new ArrayList<>();
    }

    public void setData(ArrayList<Department> listDepartment) {
        this.listDepartment = listDepartment;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BoMonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_bomon, parent, false);

        BoMonViewHolder viewHolder = new BoMonViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BoMonViewHolder holder, int position) {
        Department department = listDepartment.get(position);
        if (department != null) {
            holder.btnBoMon.setText(department.getDepartment_name());
        }

        holder.btnBoMon.setOnClickListener(view -> {
            ((ContactActivity) context).clickDepartment(department);
        });
    }

    @Override
    public int getItemCount() {
        if (listDepartment.size() > 0) {
            return listDepartment.size();
        }
        return 0;
    }

    public class BoMonViewHolder extends RecyclerView.ViewHolder {
        Button btnBoMon;
        public BoMonViewHolder(@NonNull View itemView) {
            super(itemView);
            btnBoMon = itemView.findViewById(R.id.itemBoMon_btn);
        }
    }
}
