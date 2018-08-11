package com.android.papers.qmkl_android.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.papers.qmkl_android.R;
import com.android.papers.qmkl_android.db.DownloadDB;
import com.android.papers.qmkl_android.impl.PostDislike;
import com.android.papers.qmkl_android.impl.PostIsDislike;
import com.android.papers.qmkl_android.impl.PostIsLike;
import com.android.papers.qmkl_android.impl.PostLike;
import com.android.papers.qmkl_android.model.PaperFile;
import com.android.papers.qmkl_android.model.ResponseInfo;
import com.android.papers.qmkl_android.requestModel.LikeDisLikeRequest;
import com.android.papers.qmkl_android.util.ActivityManager;

import com.android.papers.qmkl_android.util.ConstantUtils;
import com.android.papers.qmkl_android.util.DownLoader;
import com.android.papers.qmkl_android.util.LogUtils;
import com.android.papers.qmkl_android.util.PaperFileUtils;
import com.android.papers.qmkl_android.util.SDCardUtils;
import com.android.papers.qmkl_android.util.SharedPreferencesUtils;
import com.android.papers.qmkl_android.util.ToastUtils;
import com.jaren.lib.view.LikeView;

import java.io.File;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 *
 * 文件详情页,在此可下载,分享,删除,打开文件
 *
 */
public class FileDetailActivity extends BaseActivity {
    public static final String Tag = "FileDetailActivityTag";

    //下载数据库
    private DownloadDB downloadDB;
    //文件详细信息
    private PaperFile mFile;
    //是否已经下载
    private boolean isDownloading = false;
    //文件提示
    public TextView fileOpenTip,fileLocalTip;
    //下载线程
    private Thread downloadTask;

    //发送请求
    final private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ConstantUtils.BaseUrl)// 设置 网络请求 Url,1.0.0版本
            .addConverterFactory(GsonConverterFactory.create())//设置使用Gson解析
            .build();
    //用于请求点赞/点踩的人数及用户本身是否已经点赞/点踩
    LikeDisLikeRequest LDRequest;

    @BindView(R.id.iv_exit)//后退箭头
    ImageView ivExit;
    @BindView(R.id.tv_delete)//删除文本
    TextView tvDelete;
    @BindView(R.id.tv_title)//标题
    TextView tvTitle;
    @BindView(R.id.img_file_icon)//文件图片
    ImageView imgFileIcon;
    @BindView(R.id.tv_file_name)//文件名
    TextView tvFileName;
    @BindView(R.id.btn_download)//下载按钮
    Button btnDownload;
    @BindView(R.id.btn_send)//发送到电脑按钮
    Button btnSend;
    @BindView(R.id.pb_progress)//下载进度条
    ProgressBar pbProgress;
    @BindView(R.id.tv_progress)///进度条文本
    TextView tvProgress;
    @BindView(R.id.btn_cancel)//取消按钮
    ImageButton btnCancel;
    @BindView(R.id.tv_file_size)//文件大小文本
    TextView tvFileSize;
    @BindView(R.id.likeView)//点赞图
    LikeView lvLikeNumView;
    @BindView(R.id.dislikeView)//点踩图
    LikeView lvDislikeNumView;
    @BindView(R.id.dislikeTip)//点踩人数
    TextView tvDislikeTip;
    @BindView(R.id.likeTip)//点赞人数
    TextView tvLikeTip;

    //监听退出和删除按钮
    @OnClick({R.id.iv_exit, R.id.tv_delete})
    public void barItemClicked(View view) {
        switch (view.getId()) {
            case  R.id.iv_exit:
                finish();
                break;
            case  R.id.tv_delete:
                deleteDownloadedFile();
                break;
        }
    }

    //监听下载和发送到我的电脑和取消下载按钮
    @OnClick({R.id.btn_download, R.id.btn_send, R.id.btn_cancel})
    public void btnClicked(View view) {

        switch (view.getId()) {

            case R.id.btn_download:
                LogUtils.d(Tag, "download begins");
                startDownloadProcess();
                break;
            case R.id.btn_send:
                LogUtils.d(Tag, "send file");
                sendToComputer();
                break;
            case R.id.btn_cancel:
                LogUtils.d(Tag, "cancel download");
                cancelDownload();
                break;

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityManager.addActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_detail);
        ButterKnife.bind(this);

        //打开文件的提示文本
        fileOpenTip = findViewById(R.id.fileOpenTip);
        //文件下载的提示
        fileLocalTip = findViewById(R.id.fileLocalTip);
        //获取数据库是的实例
        downloadDB = DownloadDB.getInstance(getApplicationContext());

        //获取文件的详细信息
        mFile = getIntent().getParcelableExtra("FileDetail");//由于端口设置原因url从下面方式获取
        mFile.setUrl(SharedPreferencesUtils.getStoredMessage(this,mFile.getPath()));

        //封装请求
        String token = SharedPreferencesUtils.getStoredMessage(getApplicationContext(),"token");
        LDRequest = new LikeDisLikeRequest(String.valueOf(mFile.getId()),token);
        //发送post请求
        postIsDislike();
        postIsLike();

        //显示文本
        tvTitle.setText(mFile.getCourse());
        tvFileName.setText(mFile.getName());
        tvFileSize.setText(String.valueOf(mFile.getSize()));
        tvLikeTip.setText(String.valueOf(mFile.getLikeNum()));
        tvDislikeTip.setText(String.valueOf(mFile.getDislikeNum()));

        //显示图标
        imgFileIcon.setImageResource(PaperFileUtils.parseImageResource(mFile.getType().toLowerCase()));
//        //测试传递的过来的文件是否准确，准确
//        System.out.println(mFile.getDislikeNum());
//        System.out.println(mFile.getLikeNum());
//        System.out.println(mFile.getId());
//        System.out.println(mFile.getMd5());
//        System.out.println("url:"+mFile.getUrl());
        //显示按钮
        btnDownload.setText(mFile.isDownload() ? "打开文件" : "下载到手机");
        fileOpenTip.setVisibility(mFile.isDownload()? View.VISIBLE:View.INVISIBLE);
        fileLocalTip.setVisibility(mFile.isDownload()? View.INVISIBLE:View.VISIBLE);
        tvDelete.setVisibility(mFile.isDownload() ? View.VISIBLE : View.INVISIBLE);

        //点赞和点踩的事件监听
        lvLikeNumView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvLikeNumView.toggle();
                postLike();
                //更新点赞的数量
                if(lvLikeNumView.isChecked()){
                    mHandler.sendEmptyMessage(1);
                }else {
                    mHandler.sendEmptyMessage(3);
                }
            }
        });
        lvDislikeNumView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvDislikeNumView.toggle();
                postDislike();
                //更新点踩的数量
                if(lvDislikeNumView.isChecked()){
                    mHandler.sendEmptyMessage(2);
                }else {
                    mHandler.sendEmptyMessage(4);
                }
            }
        });
    }

    /**
     *      删除文件
     */
    private void deleteDownloadedFile() {
        new AlertDialog.Builder(this)
                .setMessage("是否删除该文件?")
                .setCancelable(true)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        File file = new File(SDCardUtils.getDownloadPath() + downloadDB.getFileName(mFile.getPath()));
//                        System.out.println("文件地址"+" :"+file.toString());
                        if (file.exists()) {
                            if(file.delete())
                                Log.d(Tag, "删除文件: " + file.getName());
                        }
                        downloadDB.removeDownloadInfo(mFile.getPath());
                        Log.d(Tag, "清除数据库信息: " + mFile.getUrl());

                        mFile.setDownload(false);

                        refreshDownloadState();

                        setResult(RESULT_OK);//废弃
                        finish();
                    }
                }).create().show();
    }

    /**
     *      开始下载进程
     */
    private void startDownloadProcess() {
        if (!mFile.isDownload()) {

            //未下载 执行下载进程
            setDownloadViewVisible();
//            System.out.println("文件资源URL:"+mFile.getUrl());
//            System.out.println("下载路径:"+SDCardUtils.getDownloadPath() + mFile.getName());
            downloadTask = DownLoader.downloadPaperFile(mFile.getUrl(),
                    SDCardUtils.getDownloadPath() + mFile.getName(),
                    new DownLoader.DownloadTaskCallback() {
                        @Override
                        public void onProgress(final int hasWrite, final int totalExpected) {
                            runOnUiThread(new Runnable() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void run() {
                                    pbProgress.setProgress((int) ((double) hasWrite / (double) totalExpected * 100));
                                    tvProgress.setText("下载中...(" + PaperFileUtils.sizeWithDouble(hasWrite / 1024.0) + "" +
                                            "/" + mFile.getSize() + ")");
                                }
                            });
                        }

                        @Override
                        public void onSuccess(final String successName) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    setResult(RESULT_OK);

                                    LogUtils.d(Tag, "下载完成: " + successName);
                                    mFile.setName(successName);
                                    mFile.setDownload(true);

                                    downloadDB.addDownloadInfo(mFile);

                                    setDownloadViewVisible();
                                    downloadTask = null;
                                }
                            });
                        }

                        @Override
                        public void onFailure(Exception e) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    ToastUtils.showShort(FileDetailActivity.this, "下载发生错误");
                                    setDownloadViewVisible();
                                }
                            });
                        }

                        @Override
                        public void onInterruption() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pbProgress.setProgress(0);
                                    setDownloadViewVisible();
                                }
                            });
                        }
                    });
            downloadTask.start();

        } else {
            System.out.println("存储路径:"+SDCardUtils.getDownloadPath() + mFile.getName());
            File file = new File(SDCardUtils.getDownloadPath() + mFile.getName());
            openFile(file);
        }
    }

    /**
     *      将文件发送到我的电脑
     */
    private void sendToComputer() {
        try {
            //发送paperurl到我的电脑;
            Intent intent=new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.setPackage("com.tencent.mobileqq");
            intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
            intent.putExtra(Intent.EXTRA_TEXT,  mFile.getName() + ": " +
                    ConstantUtils.BaseUrl+"dir/download/file/"+mFile.getMd5()+"/"+mFile.getId());

            intent.putExtra(Intent.EXTRA_TITLE, "发至电脑");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(intent, "选择\"发送到我的电脑\""));
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"你没有安装QQ",Toast.LENGTH_LONG).show();
        }
    }

    /**
     *      中断下载
     */
    private void cancelDownload() {

        if (downloadTask != null && downloadTask.isAlive()) {
            downloadTask.interrupt();
            downloadTask = null;
        }
    }

    /**
     *      设置下载视图
     *      一般用于下载完成一个文件后的视图设置
     */
    private void setDownloadViewVisible() {
        isDownloading = !isDownloading;

        refreshDownloadState();

        btnDownload.setVisibility(isDownloading ? View.INVISIBLE : View.VISIBLE);
        btnSend.setVisibility(isDownloading ? View.INVISIBLE : View.VISIBLE);

        tvProgress.setVisibility(isDownloading ? View.VISIBLE : View.INVISIBLE);
        pbProgress.setVisibility(isDownloading ? View.VISIBLE : View.INVISIBLE);
        btnCancel.setVisibility(isDownloading ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     *      刷新下载状态
     *      1删除按钮设为可见
     *      2按钮字样由下载到手机变为打开文件
     */
    private void refreshDownloadState() {
        tvDelete.setVisibility(mFile.isDownload() ? View.VISIBLE : View.INVISIBLE);
        btnDownload.setText(mFile.isDownload() ? "打开文件" : "下载到手机");
    }

    /**
     *      发送点踩请求
     */
    private void postDislike(){
        PostDislike request = retrofit.create(PostDislike.class);
        Call<ResponseInfo> call = request.getCall(LDRequest);
        call.enqueue(new Callback<ResponseInfo>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo> call, @NonNull final Response<ResponseInfo> response) {
            }
            @Override
            public void onFailure(@NonNull Call<ResponseInfo> call, @NonNull Throwable t) {
            }
        });
    }

    /**
     *      获取是否已经点踩请求
     */
    private void postIsDislike(){
        PostIsDislike request = retrofit.create(PostIsDislike.class);
        Call<ResponseInfo> call = request.getCall(LDRequest);
        call.enqueue(new Callback<ResponseInfo>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo> call, @NonNull final Response<ResponseInfo> response) {
                try{
                    if(Objects.requireNonNull(response.body()).getData().toString().equals("true")){
                        mHandler.sendEmptyMessage(6);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseInfo> call, @NonNull Throwable t) {
            }
        });
    }

    /**
     *      发送点赞请求
     */
    private void postLike(){
        PostLike request = retrofit.create(PostLike.class);
        Call<ResponseInfo> call = request.getCall(LDRequest);
        call.enqueue(new Callback<ResponseInfo>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo> call, @NonNull final Response<ResponseInfo> response) {
            }
            @Override
            public void onFailure(@NonNull Call<ResponseInfo> call, @NonNull Throwable t) {
            }
        });
    }

    /**
     *      获取是否已经点赞
     */
    private void postIsLike(){
        PostIsLike request = retrofit.create(PostIsLike.class);
        Call<ResponseInfo> call = request.getCall(LDRequest);
        call.enqueue(new Callback<ResponseInfo>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo> call, @NonNull final Response<ResponseInfo> response) {
                try{
                    if(Objects.requireNonNull(response.body()).getData().toString().equals("true")){
                        mHandler.sendEmptyMessage(5);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseInfo> call, @NonNull Throwable t) {
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     *      UI线程更新视图
     */
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            boolean result = false;
            if (msg.what == 1) {
                //点赞成功+1
                mFile.setLikeNum(mFile.getLikeNum()+1);
                tvLikeTip.setText(String.valueOf(mFile.getLikeNum()));
                result = true;
            }else if (msg.what == 2){
                //点踩成功+1
                mFile.setDislikeNum(mFile.getDislikeNum()+1);
                tvDislikeTip.setText(String.valueOf(mFile.getDislikeNum()));
                result = true;
            }else if (msg.what == 3){
                //点赞取消-1
                mFile.setLikeNum(mFile.getLikeNum()-1);
                tvLikeTip.setText(String.valueOf(mFile.getLikeNum()));
                result = true;
            }else if (msg.what == 4){
                //点踩取消-1
                mFile.setDislikeNum(mFile.getDislikeNum()-1);
                tvDislikeTip.setText(String.valueOf(mFile.getDislikeNum()));
                result = true;
            }else if (msg.what == 5){
                //已经点赞
                lvLikeNumView.setChecked(true);
                result = true;
            }else if(msg.what == 6){
                //已经点踩
                lvDislikeNumView.setChecked(true);
                result = true;
            }
            return result;
        }
    }) ;
}
