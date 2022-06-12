package com.example.utehystudent.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;
import com.example.utehystudent.adapters.CommentAdapter;
import com.example.utehystudent.adapters.SliderAdapter;
import com.example.utehystudent.model.BaiViet;
import com.example.utehystudent.model.Comment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PostViewer_Activity extends AppCompatActivity implements Serializable {
    private static final String TAG = "PostViewer";
    SliderView sliderView;
    SliderAdapter adapter;
    ArrayList<String> listImage;
    ImageView imgAvt, imgLike, imgBack, imgCmt, imgMore;
    TextView tvTen, tvNgay, tvContent, tvNumLike;
    EditText edtCmt;
    FirebaseFirestore db;
    BaiViet bv;
    Boolean isLiked;
    RecyclerView rcvCmt;
    CommentAdapter cmtAdapter;
    ArrayList<Comment> listComment;
    BottomSheetDialog bottomSheetDialog;
    String classID = "";
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_viewer);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        db = FirebaseFirestore.getInstance();

        listImage = new ArrayList<>();
        sliderView = findViewById(R.id.viewPost_slider);

        imgAvt = findViewById(R.id.itemBV_imgAvt);
        imgLike = findViewById(R.id.itemBV_imb_like);
        imgBack = findViewById(R.id.viewPost_imgBack);
        imgCmt = findViewById(R.id.viewPost_imgSendCmt);
        imgMore = findViewById(R.id.viewPost_imgMore);

        edtCmt = findViewById(R.id.viewPost_edtCmt);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Đang xử lý...");


        tvTen = findViewById(R.id.itemBV_tvTen);
        tvNgay = findViewById(R.id.itemBV_tvNgay);
        tvContent = findViewById(R.id.itemBV_tvContent);
        tvNumLike = findViewById(R.id.itemBV_tvNumLike);

        bottomSheetDialog = new BottomSheetDialog(this);

        rcvCmt = findViewById(R.id.viewPost_rcvCmt);
        listComment = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvCmt.setLayoutManager(linearLayoutManager);
        cmtAdapter = new CommentAdapter(PostViewer_Activity.this, listComment);
        rcvCmt.setAdapter(cmtAdapter);

        sliderView.setAutoCycle(false);

        Intent it = getIntent();
        bv = (BaiViet) it.getSerializableExtra("post");

        SetData(bv);

        Event();
    }

    private void Event() {

        imgCmt.setOnClickListener(view -> {
            if (edtCmt.getText().toString().equals("")) {
                edtCmt.setError("Nhập bình luận");
                edtCmt.requestFocus();
                return;
            }
            sendComment();
        });

        imgBack.setOnClickListener(view -> {
            finish();
        });

        imgMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBottomSheetDialog(bv);
            }
        });

        db.collection("Post")
                .document(bv.getIdBaiViet())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                                ? "Local" : "Server";

                        if (snapshot != null && snapshot.exists()) {
                            Log.d(TAG, source + " data: " + snapshot.getData());
                            postChangeListener(snapshot.toObject(BaiViet.class));
                        } else {
                            Log.d(TAG, source + " data: null");
                        }
                    }
                });


    }

    private void sendComment() {
        SharedPreferences pref = getSharedPreferences("User", Context.MODE_PRIVATE);
        Timestamp time = new Timestamp(System.currentTimeMillis());
        String userID = pref.getString("username", "");
        String name = pref.getString("name", "");
        String avtLink = pref.getString("avt_link", "");
        String timeStamp = time.getTime() + "";
        String content = edtCmt.getText().toString().trim();
        String idBaiViet = bv.getIdBaiViet();
        String idComment = idBaiViet + "" + timeStamp;


        Map<String, Object> docData = new HashMap<>();
        docData.put("idBaiViet", idBaiViet);
        docData.put("idComment", idComment);
        docData.put("idNguoiCmt", userID);
        docData.put("linkAnhNguoiCmt", avtLink);
        docData.put("noiDung", content);
        docData.put("tenNguoiCmt", name);
        docData.put("timeStamp", timeStamp);


        db.collection("Comment")
                .document(idComment)
                .set(docData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        edtCmt.setText("");
                        hideVirtualKeyboard();
                        increaseNumberOfCmt(idBaiViet);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(getCurrentFocus(), "Đã có lỗi xảy ra", Snackbar.LENGTH_SHORT).show();
                hideVirtualKeyboard();
                return;
            }
        });

    }

    private void SetData(BaiViet bv) {
        SharedPreferences pref = getSharedPreferences("User", Context.MODE_PRIVATE);
        String username = pref.getString("username", "");
        classID = pref.getString("class_ID", "");

        if (!bv.getIdNguoiDang().equals(username)) {
            imgMore.setVisibility(View.GONE);
        }

        if (bv.getLinkAnh().size() > 0) {
            listImage = bv.getLinkAnh();
            adapter = new SliderAdapter(this, listImage);
            sliderView.setSliderAdapter(adapter);
        } else {
            sliderView.setVisibility(View.GONE);
        }

        if (bv.getNoiDung().equals("")) {
            tvContent.setVisibility(View.GONE);
        }

        try {
            Picasso.get().load(bv.getLinkAnhNguoiDang()).resize(200, 200).centerCrop().into(imgAvt);
        } catch (Exception e) {
            imgAvt.setImageResource(R.drawable.ic_student);
        }

        tvTen.setText(bv.getTenNguoiDang());
        tvNgay.setText(bv.getNgayDang());
        tvContent.setText(bv.getNoiDung());
        tvNumLike.setText(bv.getListLike().size() + "");
        imgLike.setImageResource(R.drawable.ic_like);

        isLiked = false;

        bv.getListLike().forEach(item -> {
            if (item.equals(username)) {
                imgLike.setImageResource(R.drawable.ic_like_fill);
                isLiked = true;
            }
        });

        imgLike.setOnClickListener(view -> {
            int numLike = Integer.parseInt(tvNumLike.getText().toString());

            if (isLiked == true) {
                isLiked = false;
                imgLike.setImageResource(R.drawable.ic_like);
                bv.getListLike().remove(username);
                numLike--;
                tvNumLike.setText(numLike + "");
                db.collection("Post")
                        .document(bv.getIdBaiViet())
                        .update("listLike", FieldValue.arrayRemove(username));
            } else {
                isLiked = true;
                imgLike.setImageResource(R.drawable.ic_like_fill);
                bv.getListLike().add(username);
                numLike++;
                tvNumLike.setText(numLike + "");
                db.collection("Post")
                        .document(bv.getIdBaiViet())
                        .update("listLike", FieldValue.arrayUnion(username));
            }
        });

        //GetListComment();
        db.collection("Comment")
                .whereEqualTo("idBaiViet", bv.getIdBaiViet())
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "listen:error", e);
                        return;
                    }

                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                listComment.add(dc.getDocument().toObject(Comment.class));
                                Collections.sort(listComment);
                                cmtAdapter.notifyDataSetChanged();
                                break;

                            case REMOVED:
                                //decreaseCommentNumber();
                                Comment comment = dc.getDocument().toObject(Comment.class);
                                int pos = 0;
                                for (int i = 0; i < listComment.size(); i++) {
                                    if (comment.getIdComment().equals(listComment.get(i).getIdComment())) {
                                        pos = i;
                                        break;
                                    }
                                }
                                listComment.remove(pos);
                                Collections.sort(listComment);
                                Log.d("hehehe", "SetData: " + listComment.size());
                                cmtAdapter.notifyDataSetChanged();
                                break;
                        }
                    }
                });
    }


    private void postChangeListener(BaiViet baiViet) {
        if (baiViet.getNoiDung().equals(bv.getNoiDung())) {

        } else {
            tvContent.setText(baiViet.getNoiDung());
        }
        if (baiViet.getListLike().size() == bv.getListLike().size()) {

        } else {
            tvNumLike.setText(baiViet.getListLike().size() + "");
        }
        bv = baiViet;
    }

    private void hideVirtualKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void increaseNumberOfCmt(String idBV) {
        db.collection("Post")
                .document(idBV)
                .update("soBinhLuan", FieldValue.increment(1))
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

    private void openBottomSheetDialog(BaiViet baiViet) {
        BaiViet post = baiViet;
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_post);

        Button btnXoaBV = bottomSheetDialog.findViewById(R.id.bottomSheetPost_btnXoa);
        Button btnHuy = bottomSheetDialog.findViewById(R.id.bottomSheetPost_btnHuy);

        btnHuy.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
            return;
        });

        btnXoaBV.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
            dialog.show();
            //delete image
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            for (String s : post.getLinkAnh()) {
                String childName = s.substring(s.indexOf("%") + 3, s.indexOf("?alt"));
                StorageReference desertRef = storageRef.child(classID + "/" + childName);
                desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // File deleted successfully
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Uh-oh, an error occurred!
                        return;
                    }
                });
            }

            //delete comment
            CollectionReference itemsRef = db.collection("Comment");
            Query query = itemsRef.whereEqualTo("idBaiViet", post.getIdBaiViet());
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        itemsRef.document(document.getId()).delete();
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            });

            //delete post
            db.collection("Post").document(post.getIdBaiViet()).delete();

            dialog.dismiss();
            PostViewer_Activity.this.finish();
        });


        bottomSheetDialog.create();
        bottomSheetDialog.show();

    }

}