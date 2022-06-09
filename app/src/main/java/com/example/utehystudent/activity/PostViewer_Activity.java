package com.example.utehystudent.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.utehystudent.R;
import com.example.utehystudent.adapters.SliderAdapter;
import com.example.utehystudent.model.BaiViet;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

public class PostViewer_Activity extends AppCompatActivity implements Serializable {
    private static final String TAG = "PostViewer";
    SliderView sliderView;
    SliderAdapter adapter;
    ArrayList<String> listImage;
    ImageView imgAvt, imgLike, imgBack;
    TextView tvTen, tvNgay, tvContent, tvNumLike;
    FirebaseFirestore db;
    BaiViet bv;
    Boolean isLiked;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_viewer);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        db = FirebaseFirestore.getInstance();

        listImage = new ArrayList<>();
        sliderView = findViewById(R.id.viewPost_slider);

        imgAvt = findViewById(R.id.viewPost_imgAvt);
        imgLike = findViewById(R.id.viewPost_imb_like);
        imgBack = findViewById(R.id.viewPost_imgBack);

        tvTen = findViewById(R.id.viewPost_tvTen);
        tvNgay = findViewById(R.id.viewPost_tvNgay);
        tvContent = findViewById(R.id.viewPost_tvContent);
        tvNumLike = findViewById(R.id.viewPost_tvNumLike);

        sliderView.setAutoCycle(false);

        Intent it = getIntent();
        bv = (BaiViet) it.getSerializableExtra("post");

        SetData(bv);

        Event();
    }

    private void Event() {
        imgBack.setOnClickListener(view -> {
            finish();
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

    private void SetData(BaiViet bv) {
        SharedPreferences pref = getSharedPreferences("User", Context.MODE_PRIVATE);
        String username = pref.getString("username", "");
        if (bv.getLinkAnh().size() > 0) {
            listImage = bv.getLinkAnh();
            adapter = new SliderAdapter(this, listImage);
            sliderView.setSliderAdapter(adapter);
        }else {
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
        tvNumLike.setText(bv.getListLike().size()+"");

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
                tvNumLike.setText(numLike+"");
                db.collection("Post")
                        .document(bv.getIdBaiViet())
                        .update("listLike", FieldValue.arrayRemove(username));
            }else {
                isLiked = true;
                imgLike.setImageResource(R.drawable.ic_like_fill);
                bv.getListLike().add(username);
                numLike++;
                tvNumLike.setText(numLike+"");
                db.collection("Post")
                        .document(bv.getIdBaiViet())
                        .update("listLike", FieldValue.arrayUnion(username));
            }
        });
    }

    private void postChangeListener(BaiViet baiViet) {
        if (baiViet.getNoiDung().equals(bv.getNoiDung())) {

        }else {
            tvContent.setText(baiViet.getNoiDung());
        }
        if (baiViet.getListLike().size() == bv.getListLike().size()) {

        }else {
            tvNumLike.setText(baiViet.getListLike().size()+"");
        }
        bv = baiViet;
    }
}