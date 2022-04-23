package com.example.utehystudent.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.utehystudent.R;
import com.example.utehystudent.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder>{
    final String TAG = "StudentAdapter";
    private ArrayList<User> users;
    private ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public StudentAdapter(ArrayList<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        User user = users.get(position);
        if(user == null) {
            return;
        }
        viewBinderHelper.bind(holder.swipeRevealLayout, user.getUsername());
        try {
            Picasso.get().load(user.getAvt_link()).into(holder.imgAvt);
        }catch (Exception e) {
            Log.d(TAG, "onBindViewHolder: "+e.getMessage());
            holder.imgAvt.setImageResource(R.drawable.ic_student);
        }
        holder.tvName.setText(user.getName());
        holder.tvID.setText("MSV: "+user.getUsername());
    }

    @Override
    public int getItemCount() {
        if(users!=null) {
            return users.size();
        }
        return 0;
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvID;
        private ImageView imgAvt;
        private SwipeRevealLayout swipeRevealLayout;
        private LinearLayout layoutDelete;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            swipeRevealLayout = itemView.findViewById(R.id.rowStudent_swipeRevealLayout);
            layoutDelete = itemView.findViewById(R.id.rowStudent_layoutDelete);
            tvName = itemView.findViewById(R.id.rowStudent_tvName);
            tvID = itemView.findViewById(R.id.rowStudent_tvID);
            imgAvt = itemView.findViewById(R.id.rowStudent_imgAvt);
        }
    }
}
