package com.example.utehystudent.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;
import com.example.utehystudent.activity.ImageViewerActivity;
import com.example.utehystudent.activity.PostViewer_Activity;
import com.example.utehystudent.adapters.BaiVietAdapter;
import com.example.utehystudent.model.BaiViet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

public class BangTinFragment extends Fragment implements Serializable {
    private static final String TAG = "BangTinFragment";
    Context context;
    Context activity;
    FirebaseFirestore db;
    SharedPreferences pref;
    ImageView imgAvt;
    TextView tvDangBai;
    RecyclerView rcv;
    String classID = "";
    ArrayList<BaiViet> postList;
    BaiVietAdapter baiVietAdapter;
    ProgressBar prgBar;

    public BangTinFragment(Context context) {
        this.context = context;
    }

    public BangTinFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this.getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bang_tin, container, false);

        postList = new ArrayList<>();

        pref = activity.getSharedPreferences("User", Context.MODE_PRIVATE);

        db = FirebaseFirestore.getInstance();

        imgAvt = view.findViewById(R.id.BangTin_imgAvtHienTai);
        tvDangBai = view.findViewById(R.id.BangTin_tvDangBai);
        rcv = view.findViewById(R.id.BangTin_rcv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        rcv.setLayoutManager(linearLayoutManager);
        prgBar = view.findViewById(R.id.BangTin_prgBar);
        baiVietAdapter = new BaiVietAdapter(this, postList);

        GetCurrentUser();
        GetPostList();
        // Inflate the layout for this fragment
        return view;
    }


    public void viewImage(ArrayList<String> imgLink) {
        Intent it = new Intent(getActivity(), ImageViewerActivity.class);
        it.putExtra("link", imgLink);
        startActivity(it);
    }

    private void GetCurrentUser() {
        String imgLink = pref.getString("avt_link", "");
        try {
            Picasso.get().load(imgLink).resize(190, 190).centerCrop().into(imgAvt);
        }catch (Exception e) {
            Log.d(TAG, "onBindViewHolder: "+e.getMessage());
            imgAvt.setImageResource(R.drawable.ic_student);
        }
        classID = pref.getString("class_ID", "");
        tvDangBai.setText("Đăng bài viết vào "+classID+"...");
    }

    private void GetPostList () {
        rcv.setVisibility(View.GONE);
        prgBar.setVisibility(View.VISIBLE);
        postList.clear();
        db.collection("Post")
                .whereEqualTo("maLop", classID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                BaiViet baiViet = document.toObject(BaiViet.class);
                                postList.add(baiViet);
                                Log.d("zzzz", "onComplete: "+baiViet.toString());
                            }
                            baiVietAdapter = new BaiVietAdapter(BangTinFragment.this, postList);
                            rcv.setAdapter(baiVietAdapter);
                            rcv.setVisibility(View.VISIBLE);
                            prgBar.setVisibility(View.GONE);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    public void viewPost(BaiViet bv) {
        Intent it = new Intent(activity, PostViewer_Activity.class);
        it.putExtra("post", bv);
        startActivity(it);
    }

    @Override
    public void onResume() {
        super.onResume();
        baiVietAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}