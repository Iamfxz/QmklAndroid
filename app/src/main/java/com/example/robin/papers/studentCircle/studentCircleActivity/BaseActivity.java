package com.example.robin.papers.studentCircle.studentCircleActivity;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.robin.papers.R;
import com.example.robin.papers.studentCircle.model.ImageBDInfo;
import com.example.robin.papers.studentCircle.model.ImageInfo;
import com.example.robin.papers.studentCircle.tools.ImageLoaders;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;


/**
 * Created by DavidWang on 15/8/31.
 */
public class BaseActivity extends AppCompatActivity  {

    private Toast mToast;
    // 屏幕宽度
    public float Width;
    // 屏幕高度
    public float Height;

    protected ImageView showimg;
    public Toolbar toolbar;
    public View Barview;

    private final Spring mSpring = SpringSystem
            .create()
            .createSpring()
            .addListener(new ExampleSpringListener());

    private RelativeLayout MainView;

    protected ImageBDInfo bdInfo;
    protected ImageInfo imageInfo;
    private float size, size_h;

    private float img_w;
    private float img_h;

    //原图高
    private float y_img_h;
    private float x_img_w;
    protected float to_x = 0;
    protected float to_y = 0;
    private float tx;
    private float ty;
    private int statusBarHeight;
    private int titleBarHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        Width = dm.widthPixels;
        Height = dm.heightPixels;
        setStatusBar();
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        statusBarHeight = frame.top;
        int contentTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        //statusBarHeight是上面所求的状态栏的高度
        titleBarHeight = contentTop - statusBarHeight;

    }

    /**
     * 设置状态栏为蓝色
     */
    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= 21) {//21表示5.0
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#508CEE"));
        } else if (Build.VERSION.SDK_INT >= 19) {//19表示4.4
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //虚拟键盘也透明
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    /**
     * 添加头部
     */
    protected void AddToolbar() {
        toolbar = (Toolbar) findViewById(R.id.id_toolbar);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

    }


    /**
     * 获取资源
     */
    protected void findID() {
        MainView = (RelativeLayout) findViewById(R.id.MainView);
    }




    protected void getValue() {
        showimg = new ImageView(this);
        showimg.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ImageLoaders.setsendimg(imageInfo.url, showimg);
        img_w = bdInfo.width;
        img_h = bdInfo.height;
        size = Width / img_w;
        y_img_h = imageInfo.height * Width / imageInfo.width;
        size_h = y_img_h / img_h;
        if (y_img_h > Height){
            size_h = Height / img_h;
            x_img_w = imageInfo.width * Height / imageInfo.height;
            size =  x_img_w / img_w;
        }
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams((int) bdInfo.width, (int) bdInfo.height);
        showimg.setLayoutParams(p);
        p.setMargins((int) bdInfo.x, (int) bdInfo.y, (int) (Width - (bdInfo.x + bdInfo.width)), (int) (Height - (bdInfo.y + bdInfo.height)));
        MainView.addView(showimg);

        Barview =  View.inflate(this, R.layout.waiting_view, null);
        Barview.setVisibility(View.GONE);
        RelativeLayout.LayoutParams p1 = new RelativeLayout.LayoutParams((int)Width, (int)Height);
        Barview.setLayoutParams(p1);
        MainView.addView(Barview);

        showimg.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                setShowimage();
            }
        }, 300);
    }

    protected void setShowimage() {
        if (mSpring.getEndValue() == 0) {
            mSpring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(170, 5));
            tx = Width / 2 - (bdInfo.x + img_w / 2);
            ty = Height / 2 - (bdInfo.y + img_h / 2);
            MoveView();
            return;
        }
        mSpring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(1, 5));
        mSpring.setEndValue(0);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                //execute the task
                MoveBackView();
            }
        }, 300);

    }


    private class ExampleSpringListener implements SpringListener {

        @Override
        public void onSpringUpdate(Spring spring) {
            double CurrentValue = spring.getCurrentValue();
            float mappedValue = (float) SpringUtil.mapValueFromRangeToRange(CurrentValue, 0, 1, 1, size);
            float mapy = (float) SpringUtil.mapValueFromRangeToRange(CurrentValue, 0, 1, 1, size_h);
            showimg.setScaleX(mappedValue);
            showimg.setScaleY(mapy);
            if (CurrentValue == 1) {
                EndSoring();
            }
        }

        @Override
        public void onSpringAtRest(Spring spring) {

        }

        @Override
        public void onSpringActivate(Spring spring) {

        }

        @Override
        public void onSpringEndStateChange(Spring spring) {

        }
    }

    protected void EndSoring() {

    }

    protected void EndMove() {

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

    }


    public int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public int px2dip(float pxValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    private void MoveView() {
        ObjectAnimator.ofFloat(MainView, "alpha", 0.8f).setDuration(0).start();
        MainView.setVisibility(View.VISIBLE);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(showimg, "translationX", tx).setDuration(200),
                ObjectAnimator.ofFloat(showimg, "translationY", ty).setDuration(200),
                ObjectAnimator.ofFloat(MainView, "alpha", 1).setDuration(200)

        );
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (imageInfo.hdurl == null) {
                    showimg.setScaleType(ImageView.ScaleType.FIT_XY);
                    mSpring.setEndValue(1);
                } else {
                    HDImageChange();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        set.start();

    }

    private void HDImageChange() {
        ImageLoaders.setsendimg(imageInfo.hdurl,showimg);
        showimg.setScaleType(ImageView.ScaleType.FIT_XY);
        mSpring.setEndValue(1);

    }

    private void MoveBackView() {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(showimg, "translationX", to_x).setDuration(200),
                ObjectAnimator.ofFloat(showimg, "translationY", to_y).setDuration(200)
        );
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                showimg.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                EndMove();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        set.start();
    }

}
