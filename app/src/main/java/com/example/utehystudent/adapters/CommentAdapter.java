package com.example.utehystudent.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;
import com.example.utehystudent.model.Comment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private static final String TAG = "CommentAdapter";
    Context context;
    ArrayList<Comment> list;
    BottomSheetDialog bottomSheetDialog;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public CommentAdapter(Context context, ArrayList<Comment> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_binh_luan, parent, false);

        CommentViewHolder viewHolder = new CommentViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        SharedPreferences pref = context.getSharedPreferences("User", Context.MODE_PRIVATE);
        String username = pref.getString("username", "");

        Comment comment = list.get(position);
        if (comment == null) {
            return;
        }

        try {
            Picasso.get().load(comment.getLinkAnhNguoiCmt()).resize(180, 180).centerCrop().into(holder.imgAvt);
        } catch (Exception e) {
            holder.imgAvt.setImageResource(R.drawable.ic_student);
        }
        holder.tvTen.setText(comment.getTenNguoiCmt());
        holder.tvND.setText(comment.getNoiDung());
        holder.tvNgay.setText(comment.getNgayCmt());
        holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!username.equals(comment.getIdNguoiCmt())) {

                }else {
                    showBottomSheetOption(comment);
                }
                return false;
            }
        });


    }

    private void showBottomSheetOption(Comment comment) {
        bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_comment_option);

        Button btnXoaCmt = bottomSheetDialog.findViewById(R.id.bottomSheetCmt_btnXoa);
        Button btnHuy = bottomSheetDialog.findViewById(R.id.bottomSheetCmt_btnHuy);

        //XÓA CMT
        btnXoaCmt.setOnClickListener(view -> {
            db.collection("Comment")
                    .document(comment.getIdComment())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            bottomSheetDialog.dismiss();
                            decreaseNumberOfCmt(comment.getIdBaiViet());
                            Log.d(TAG, "DocumentSnapshot comment successfully deleted!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Error deleting document", e);
                        }
                    });
        });

        btnHuy.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.show();
    }

    private void decreaseNumberOfCmt(String idBV) {
        db.collection("Post")
                .document(idBV)
                .update("soBinhLuan", FieldValue.increment(-1))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                return;
            }
        });
    }




    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvt;
        TextView tvTen, tvND, tvNgay;
        LinearLayout linearLayout;
        public CommentViewHolder(@NonNull View view) {
            super(view);

            imgAvt = view.findViewById(R.id.itemCmt_imgAvt);
            tvTen = view.findViewById(R.id.itemCmt_tvTenNguoiCmt);
            tvND = view.findViewById(R.id.itemCmt_noiDung);
            tvNgay = view.findViewById(R.id.itemCmt_tvGio);
            linearLayout = view.findViewById(R.id.itemCmt_linearCmt);

        }
    }
}
