package com.example.utehystudent.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;
import com.example.utehystudent.activity.ThoiKhoaBieuActivity;
import com.example.utehystudent.model.ThoiKhoaBieu;

import java.util.ArrayList;


public class ThoiKhoaBieuAdapter extends RecyclerView.Adapter<ThoiKhoaBieuAdapter.ThoiKhoaBieuViewHolder>{
    Activity activity;
    ArrayList<ThoiKhoaBieu> listTKB;
    String regency = "";

    public ThoiKhoaBieuAdapter(Activity activity, ArrayList<ThoiKhoaBieu> listTKB) {
        this.activity = activity;
        this.listTKB = listTKB;
        SharedPreferences pref = activity.getSharedPreferences("User", Context.MODE_PRIVATE);
        regency = pref.getString("regency", "");
    }

    @NonNull
    @Override
    public ThoiKhoaBieuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thoikhoabieu, parent, false);
        return new ThoiKhoaBieuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThoiKhoaBieuViewHolder holder, int position) {
        ThoiKhoaBieu tkb = listTKB.get(position);
        if (tkb.getThu().equals("Sunday")) {
            holder.tvThu.setTextSize(40);
        }
        holder.tvThu.setText(getThu(tkb.getThu()));
        holder.tvSang.setText("S:  "+tkb.getSang());
        holder.tvChieu.setText("C:  "+tkb.getChieu());
        if (tkb.getToi().equals("Nghỉ")) {
            holder.tvToi.setVisibility(View.GONE);
        }else {
            holder.tvToi.setText("T:  "+tkb.getToi());
        }

        holder.item.setOnClickListener(item -> {
            if (regency.equals("lt")) {
                ((ThoiKhoaBieuActivity) activity).clickItemTKB(tkb);
            }else {

            }
        });

    }

    @Override
    public int getItemCount() {
        if (listTKB != null) {
            return listTKB.size();
        }
        return 0;
    }

    public class ThoiKhoaBieuViewHolder extends RecyclerView.ViewHolder {

        private TextView tvThu, tvSang, tvChieu, tvToi;
        private CardView item;

        public ThoiKhoaBieuViewHolder(@NonNull View itemView) {
            super(itemView);
            tvThu = itemView.findViewById(R.id.itemTKB_tvThu);
            tvSang = itemView.findViewById(R.id.itemTKB_tvSang);
            tvChieu = itemView.findViewById(R.id.itemTKB_tvChieu);
            tvToi = itemView.findViewById(R.id.itemTKB_tvToi);
            item = itemView.findViewById(R.id.itemTKB_item);
        }
    }

    private String getThu(String s) {
        String rs = "";
        switch (s) {
            case "Monday":
                rs = "2";
                break;
            case "Tuesday":
                rs = "3";
                break;
            case "Wednesday":
                rs = "4";
                break;
            case "Thursday":
                rs = "5";
                break;
            case "Friday":
                rs = "6";
                break;
            case "Saturday":
                rs = "7";
                break;
            case "Sunday":
                rs = "CN";
                break;
            default: rs = ""; break;
        }
        return rs;
    }
}
