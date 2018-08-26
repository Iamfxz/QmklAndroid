package com.android.papers.qmkl_android.studentCircle.dwcorephoto;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.papers.qmkl_android.R;
import com.android.papers.qmkl_android.studentCircle.adapter.MixListAdapter;
import com.android.papers.qmkl_android.studentCircle.model.DialogueInfo;
import com.android.papers.qmkl_android.studentCircle.model.ImageInfo;
import com.android.papers.qmkl_android.studentCircle.model.Mixinfo;
import com.android.papers.qmkl_android.studentCircle.view.PullToZoomListView;
import com.android.papers.qmkl_android.util.StatusBarUtil;
import com.android.papers.qmkl_android.util.SystemBarTintManager;

import java.util.ArrayList;



public class MixShowActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener, PullToZoomListView.BackTouchEvent {

    public PullToZoomListView mixlist;
    private MixListAdapter adapterData;
    private ArrayList<Mixinfo> data;

    private RelativeLayout bottomView;
    private EditText editText;
    private Button sendBtn;
    private int height_top = 0;
    private int keyHeight = 0;
    private int index;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            //状态栏透明 需要在创建SystemBarTintManager 之前调用。
//            setTranslucentStatus(true);
//            SystemBarTintManager tintManager = new SystemBarTintManager(this);
//            tintManager.setStatusBarTintEnabled(true);
//            //使StatusBarTintView 和 actionbar的颜色保持一致，风格统一。
//            tintManager.setStatusBarTintResource(R.color.blue);
//            // 设置状态栏的文字颜色
//            tintManager.setStatusBarDarkMode(true, this);
//        }
//        StatusBarUtil.fullScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mix_show);
        findID();
        InData();
        AddToolbar();
        keyHeight = (int) Height / 3;
    }

    @Override
    protected void findID() {
        super.findID();
        mixlist = (PullToZoomListView) findViewById(R.id.mixlist);
        mixlist.getHeaderView().setImageResource(R.mipmap.mixheadimg);
        mixlist.getHeaderView().setScaleType(ImageView.ScaleType.CENTER_CROP);
        mixlist.setOnItemClickListener(this);
        mixlist.setTouchEvent(this);
        bottomView = (RelativeLayout) findViewById(R.id.bottomView);
        editText = (EditText) findViewById(R.id.editText);
        sendBtn = (Button) findViewById(R.id.sendBtn);
        mixlist.addOnLayoutChangeListener(new LayoutChangeListener());
        sendBtn.setOnClickListener(this);
    }

    @Override
    public void InData() {
        super.InData();
        data = new ArrayList<Mixinfo>();

        Mixinfo info1 = new Mixinfo();
        info1.username = "DavidWang";
        info1.userimg = "http://imgsrc.baidu.com/forum/pic/item/8b82b9014a90f603fa18d50f3912b31bb151edca.jpg";
        info1.content = "这是一个单张的演示";
        info1.data = AddData(1, 0);
        info1.dialogdata = AddDialog();
        data.add(info1);

        Mixinfo info2 = new Mixinfo();
        info2.username = "DavidWang";
        info2.userimg = "http://imgsrc.baidu.com/forum/pic/item/8b82b9014a90f603fa18d50f3912b31bb151edca.jpg";
        info2.content = "这是一个单张的演示";
        info2.data = AddData(1, 1);
        info2.dialogdata = AddDialog();
        data.add(info2);

        Mixinfo info3 = new Mixinfo();
        info3.username = "DavidWang";
        info3.userimg = "http://imgsrc.baidu.com/forum/pic/item/8b82b9014a90f603fa18d50f3912b31bb151edca.jpg";
        info3.content = "这是一个单张的演示";
        info3.data = AddData(1, 2);
        info3.dialogdata = AddDialog();
        data.add(info3);

        for (int i = 2; i < 10; i++) {
            Mixinfo info4 = new Mixinfo();
            info4.username = "DavidWang";
            info4.userimg = "http://imgsrc.baidu.com/forum/pic/item/8b82b9014a90f603fa18d50f3912b31bb151edca.jpg";
            info4.content = "这是" + i + "个单张的演示测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度测试字符长度";
            info4.data = AddData(i, 2);
            info4.dialogdata = AddDialog();
            data.add(info4);
        }

        adapterData = new MixListAdapter(MixShowActivity.this, data);
        mixlist.setAdapter(adapterData);

    }

    private ArrayList<ImageInfo> AddData(int num, int type) {
        ArrayList<ImageInfo> data = new ArrayList<ImageInfo>();
        for (int i = 0; i < num; i++) {
            if (type == 0) {
                ImageInfo info = new ImageInfo();
                info.url = "http://img4q.duitang.com/uploads/item/201408/11/20140811141753_iNtAF.jpeg";
                info.width = 1280;
                info.height = 720;

                data.add(info);
            } else if (type == 1) {
                ImageInfo info = new ImageInfo();
                info.url = "http://article.joyme.com/article/uploads/allimg/140812/101I01291-10.jpg";
                info.width = 640;
                info.height = 960;
                data.add(info);

            } else {
                ImageInfo info = new ImageInfo();
                info.url = "http://h.hiphotos.baidu.com/album/scrop%3D236%3Bq%3D90/sign=2fab0be130adcbef056a3959dc921cee/4b90f603738da977c61bb40eb151f8198618e3db.jpg";
                info.width = 236;
                info.height = 236;
                data.add(info);
            }
        }
        return data;
    }

    private ArrayList<DialogueInfo> AddDialog() {
        ArrayList<DialogueInfo> dialogdata = new ArrayList<DialogueInfo>();
        DialogueInfo dinfo = new DialogueInfo();
        dinfo.content = "我来回复你来:哇哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哇哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈";
        dialogdata.add(dinfo);
        return dialogdata;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        hideEdit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendBtn:
                String content = editText.getText().toString();
                if (content.length() > 0) {
                    DialogueInfo dinfo = new DialogueInfo();
                    dinfo.leftname = "我来";
                    dinfo.rightname = "你来";
                    dinfo.content = dinfo.leftname + "回复" + dinfo.rightname + ":" + content;
                    Mixinfo minfo = data.get(index);
                    ArrayList<DialogueInfo> dialogarr = minfo.dialogdata;
                    dialogarr.add(dinfo);
                    adapterData.notifyDataSetChanged();
                }
                hideEdit();
                break;
        }
    }

    private void hideEdit() {
        if (bottomView.getVisibility() == View.VISIBLE) {
            bottomView.setVisibility(View.GONE);
            editText.setText("");
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0); //强制隐藏键盘
        }
    }

    public void SendContent(final int index, final int hight) {
        this.index = index;
        bottomView.setVisibility(View.VISIBLE);
        editText.setFocusable(true);
        editText.requestFocus();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                View v = mixlist.getChildAt(0);
                int top = (v == null) ? 0 : v.getTop();
                if (height_top == 0) {
                    height_top = hight + top - dip2px(50);
                }
                mixlist.setSelectionFromTop((index + 1), height_top - hight);
            }
        }, 100);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            hideEdit();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private class LayoutChangeListener implements View.OnLayoutChangeListener {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {
                //软键盘弹起
            } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {
                //软键盘消失
                bottomView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideEdit();
    }

    @Override
    public void OnTouchEvent() {
        hideEdit();
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
