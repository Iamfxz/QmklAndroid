package com.android.papers.qmkl_android.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.papers.qmkl_android.R;
import com.android.papers.qmkl_android.util.SDCardUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

//第二启动页 (放广告的页面 缺省)
public class AdsActivity extends Activity {

    @BindView(R.id.iv_ad)
    ImageView ivAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads);
        ButterKnife.bind(this);

    //设置窗体全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        File adImageFile = new File(SDCardUtils.getADImage());

        if (adImageFile.exists()) {
            ivAd.setImageURI(Uri.fromFile(adImageFile));
        } else  {
            getSharedPreferences("AppConfig", MODE_PRIVATE).edit().putInt("ad_version", 0);
        }


        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        startActivity(new Intent(AdsActivity.this, LoginActivity.class));
                        finish();

                    }
                });
            }
        }).start();

    }


    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

}
