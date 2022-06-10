package com.example.utehystudent.activity;

import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.utehystudent.R;
import com.squareup.picasso.Picasso;
import com.zolad.zoominimageview.ZoomInImageView;

import java.util.ArrayList;

public class ImageViewerActivity extends AppCompatActivity {

    ZoomInImageView img;
    ImageView imgBack, imgPre, imgNext;
    ArrayList<String> listImg;
    TextView tvCurrent;
    int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        img = findViewById(R.id.imageViewer_img);
        imgBack = findViewById(R.id.ImageViewer_imgBack);
        imgPre = findViewById(R.id.imageViewer_imgPreviousImage);
        imgNext = findViewById(R.id.imageViewer_imgNextImage);
        tvCurrent = findViewById(R.id.imageViewer_tvCurrentPos);

        listImg = new ArrayList<>();

        listImg = (ArrayList<String>) getIntent().getSerializableExtra("link");

        tvCurrent.setText((pos+1)+"/"+listImg.size());

        try {
            Picasso.get().load(listImg.get(0)).into(img);
        }catch (Exception e) {
            Toast.makeText(this, "Lỗi load ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        imgBack.setOnClickListener( click -> {
            finish();
        });

        imgPre.setOnClickListener(view -> {
            if (listImg.size() == 1) {
                return;
            }
            if (pos == 0) {
                return;
            }else {
                pos = pos - 1;
                ImageViewAnimatedChange(img, listImg.get(pos));
            }
            tvCurrent.setText((pos+1)+"/"+listImg.size());
        });

        imgNext.setOnClickListener(view -> {
            if (listImg.size() == 1) {
                return;
            }
            if (pos == listImg.size()-1) {
                return;
            }else {
                pos = pos + 1;
                ImageViewAnimatedChange(img, listImg.get(pos));
            }
            tvCurrent.setText((pos+1)+"/"+listImg.size());
        });
    }

    public void ImageViewAnimatedChange(final ImageView v, String linkAnh) {
        final Animation anim_out = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        final Animation anim_in  = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        anim_in.setDuration(100);
        anim_out.setDuration(100);
        anim_out.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation)
            {
                try {
                    Picasso.get().load(linkAnh).into(img);
                }catch (Exception e) {
                    Toast.makeText(ImageViewerActivity.this, "Lỗi load ảnh", Toast.LENGTH_SHORT).show();
                    return;
                }
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {}
                    @Override public void onAnimationRepeat(Animation animation) {}
                    @Override public void onAnimationEnd(Animation animation) {}
                });
                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
    }

}