package com.example.utehystudent.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;
import com.example.utehystudent.activity.admin.QLMonHoc_Activity;
import com.example.utehystudent.model.Subject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class QLMonHoc_Adapter extends RecyclerView.Adapter<QLMonHoc_Adapter.QLMonHocViewHolder> {
    Context context;
    ArrayList<Subject> listSubject;
    FirebaseFirestore db;

    public QLMonHoc_Adapter(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }

    public void setData(ArrayList<Subject> list) {
        this.listSubject = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public QLMonHocViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_ql_monhoc, parent, false);

        QLMonHocViewHolder viewHolder = new QLMonHocViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull QLMonHocViewHolder holder, int position) {
        Subject subject = listSubject.get(position);
        if (subject == null) {
            return;
        }
        holder.tvTenMH.setText(subject.getSubject_name());
        holder.tvSoTC.setText("Số tín chỉ: "+subject.getNum_cred());

        db.collection("Faculty")
                .whereEqualTo("faculty_ID", subject.getFaculty_ID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                holder.tvKhoa.setText("Khoa "+doc.getString("faculty_name"));
                            }
                        }
                    }
                });

        holder.item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ((QLMonHoc_Activity) QLMonHoc_Adapter.this.context).openBottomSheetDialog(subject);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (listSubject.size() > 0) {
            return listSubject.size();
        }
        return 0;
    }

    public class QLMonHocViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenMH, tvKhoa, tvSoTC;
        CardView item;
        public QLMonHocViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenMH = itemView.findViewById(R.id.rowMH_tvTenMH);
            tvKhoa = itemView.findViewById(R.id.rowMH_tvKhoa);
            tvSoTC = itemView.findViewById(R.id.rowMH_tvSoTC);
            item = itemView.findViewById(R.id.rowMH_item);
        }
    }
}
