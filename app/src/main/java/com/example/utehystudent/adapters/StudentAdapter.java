package com.example.utehystudent.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.utehystudent.R;
import com.example.utehystudent.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
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
            Picasso.get().load(user.getAvt_link()).resize(270, 270).centerCrop().into(holder.imgAvt);
        }catch (Exception e) {
            Log.d(TAG, "onBindViewHolder: "+e.getMessage());
            holder.imgAvt.setImageResource(R.drawable.ic_student);
        }
        holder.tvName.setText(user.getName());
        holder.tvID.setText(user.getUsername());
        holder.imgDelete.setOnClickListener(view -> {
            Toast.makeText(view.getContext(), ""+user.getName(), Toast.LENGTH_SHORT).show();
        });

        holder.imgRSPW.setOnClickListener(view -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
            alert.setMessage("Reset mật khẩu sinh viên sẽ thành về mặc định mã sinh viên.\nBạn chắc chắn chứ?");
            alert.setPositiveButton("CÓ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    db.collection("Account")
                            .document(user.getUsername())
                            .update("password", user.getUsername())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        SharedPreferences accountPref = view.getContext().getSharedPreferences("Account", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = accountPref.edit();
                                        editor.putString("password", user.getUsername());
                                        editor.apply();
                                        dialogInterface.dismiss();
                                        Toast.makeText(view.getContext(), "Reset mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                        return;
                                    }else {
                                        Toast.makeText(view.getContext(), "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                            });
                }
            });
            alert.setNegativeButton("HỦY", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            alert.create().show();
        });

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteStudent(view, user);
            }
        });
    }

    private void deleteStudent(View view,User user) {
        ProgressDialog dialog = new ProgressDialog(view.getContext());
        dialog.setMessage("Đang xóa dữ liệu...");

        AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
        alert.setMessage("Xác nhận xóa toàn bộ dữ liệu liên quan đến sinh viên này ?");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.show();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                //delete account data
                db.collection("Account")
                        .document(user.getUsername())
                        .delete();

                //delete user data
                db.collection("User")
                        .document(user.getUsername())
                        .delete();

                //delete post data
                CollectionReference itemsRef = db.collection("Post");
                Query query = itemsRef.whereEqualTo("idNguoiDang", user.getUsername());
                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            itemsRef.document(document.getId()).delete();
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });

                //delete comment data
                CollectionReference itemsRef2 = db.collection("Comment");
                Query query2 = itemsRef2.whereEqualTo("idNguoiCmt", user.getUsername());
                query2.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            itemsRef2.document(document.getId()).delete();
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
                users.remove(user);
                notifyDataSetChanged();
                dialog.dismiss();

                Toast.makeText(view.getContext(), "Xóa dữ liệu sinh viên thành công!", Toast.LENGTH_SHORT).show();
            }
        });

        alert.create().show();
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
        private ImageView imgAvt, imgDelete, imgRSPW;
        private SwipeRevealLayout swipeRevealLayout;
        private LinearLayout layoutDelete;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            swipeRevealLayout = itemView.findViewById(R.id.rowStudent_swipeRevealLayout);
            layoutDelete = itemView.findViewById(R.id.rowStudent_layoutDelete);
            tvName = itemView.findViewById(R.id.rowStudent_tvName);
            tvID = itemView.findViewById(R.id.rowStudent_tvID);
            imgAvt = itemView.findViewById(R.id.rowStudent_imgAvt);
            imgDelete = itemView.findViewById(R.id.rowStudent_imgDelete);
            imgRSPW = itemView.findViewById(R.id.rowStudent_imgRSPW);
        }
    }
}
