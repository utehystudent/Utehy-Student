package com.example.utehystudent.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;
import com.example.utehystudent.model.SubjectAbsent;

import java.util.ArrayList;

public class SubjectAbsentAdapter extends RecyclerView.Adapter<SubjectAbsentAdapter.SubjectAbsentViewHolder> {
    private ArrayList<SubjectAbsent> listSubjectAbsent;
    EventListener listener;

    public SubjectAbsentAdapter(ArrayList<SubjectAbsent> listSubjectAbsent, EventListener listener) {
        this.listSubjectAbsent = listSubjectAbsent;
        this.listener = listener;
    }

    public interface EventListener {
        void onEvent(String subjectID);
    }

    @NonNull
    @Override
    public SubjectAbsentAdapter.SubjectAbsentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_subject_absent, parent, false);
        return new SubjectAbsentAdapter.SubjectAbsentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectAbsentViewHolder holder, int position) {
        SubjectAbsent subject = listSubjectAbsent.get(position);
        if (subject == null) {
            return;
        }

        holder.tvSubjectName.setText(subject.getSubject_Name() + "");
        int numCred = subject.getNum_Cred();
        int numAbsent = subject.getNum_Absent();
        Double progr = numAbsent / Double.parseDouble(String.valueOf(numCred)) * 100;
        holder.tvNumAbsent.setText("" + numAbsent + "/" + numCred);
        if (numAbsent == numCred) {
            holder.imgInfo.setImageResource(R.drawable.ic_warning);
        }
        if (numCred >= 5) {
            holder.tvNumAbsent.setText("" + numAbsent + "/4");
            progr = numAbsent / Double.parseDouble(String.valueOf(4)) * 100;
            if (numAbsent >= 4) {
                holder.imgInfo.setImageResource(R.drawable.ic_cancel);
            }
        }
        holder.prg.setProgress((int) Math.round(progr));

        holder.layout.setOnClickListener(view -> {
            listener.onEvent(subject.getSubject_ID());
        });

    }

    @Override
    public int getItemCount() {
        if (listSubjectAbsent != null) {
            return listSubjectAbsent.size();
        }
        return 0;
    }

    public class SubjectAbsentViewHolder extends RecyclerView.ViewHolder {

        ProgressBar prg;
        TextView tvNumAbsent, tvSubjectName;
        ImageView imgInfo;
        RelativeLayout layout;

        public SubjectAbsentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubjectName = itemView.findViewById(R.id.row_subject_absent_tvSubjectName);
            tvNumAbsent = itemView.findViewById(R.id.row_subject_absent_tvProgress);
            prg = itemView.findViewById(R.id.row_subject_absent_progressBar);
            imgInfo = itemView.findViewById(R.id.row_subject_absent_imgInfo);
            layout = itemView.findViewById(R.id.row_subject_absent);
        }
    }

}
