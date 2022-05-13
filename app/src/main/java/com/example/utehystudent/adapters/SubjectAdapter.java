package com.example.utehystudent.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;
import com.example.utehystudent.activity.SubjectInTermManagementActivity;
import com.example.utehystudent.model.Subject;

import java.util.ArrayList;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>{
    private ArrayList<Subject> listSubject;
    Activity activity;
    public SubjectAdapter(Activity activity, ArrayList<Subject> listSubject) {
        this.listSubject = listSubject;
        this.activity = activity;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_subject, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        Subject subject = listSubject.get(position);
        if (subject == null) {
            return;
        }
        holder.tvSubjectName.setText(subject.getSubject_name()+"");
        holder.tvSubjectNumCred.setText("Số TC: "+subject.getNum_cred());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SubjectInTermManagementActivity) activity).LongClickItemSubject(subject);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (listSubject != null) {
            return listSubject.size();
        }
        return 0;
    }

    public class SubjectViewHolder extends RecyclerView.ViewHolder{

        private TextView tvSubjectName, tvSubjectNumCred;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubjectName = itemView.findViewById(R.id.rowSubject_tvSubjectName);
            tvSubjectNumCred = itemView.findViewById(R.id.rowSubject_tvSubjectNumCred);
        }
    }
}
