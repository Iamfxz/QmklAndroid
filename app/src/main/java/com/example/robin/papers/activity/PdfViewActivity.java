package com.example.robin.papers.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robin.papers.R;

import es.voghdev.pdfviewpager.library.RemotePDFViewPager;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;
import es.voghdev.pdfviewpager.library.remote.DownloadFile;
import es.voghdev.pdfviewpager.library.util.FileUtil;

public class PdfViewActivity extends AppCompatActivity implements DownloadFile.Listener {
    private RelativeLayout pdf_root;
    private ProgressBar pb_bar;
    private RemotePDFViewPager remotePDFViewPager;
    private String mUrl ;//= "http://www.finalexam.cn/qmkl1.0.0/dir/online/file/84776d27590111a971f1e85daa453d70/210/2008-01java程序设计期末试卷答案.pdf";
    private PDFPagerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdfview_layout);
        initView();
        setDownloadListener();
    }


    protected void initView() {
        pdf_root = (RelativeLayout) findViewById(R.id.remote_pdf_root);
        pb_bar = (ProgressBar) findViewById(R.id.pb_bar);
        mUrl=getIntent().getStringExtra("url");
    }

    /*设置监听*/
    protected void setDownloadListener() {
        final DownloadFile.Listener listener = this;
        remotePDFViewPager = new RemotePDFViewPager(this, mUrl, listener);
        remotePDFViewPager.setId(R.id.pdfViewPager);
    }

    /*加载成功调用*/
    @Override
    public void onSuccess(String url, String destinationPath) {
        pb_bar.setVisibility(View.GONE);
        adapter = new PDFPagerAdapter(this, FileUtil.extractFileNameFromURL(url));
        remotePDFViewPager.setAdapter(adapter); updateLayout();
        Log.d("成功", "onSuccess: ");
    }

    /*更新视图*/
    private void updateLayout() {
        pdf_root.removeAllViewsInLayout();
        pdf_root.addView(remotePDFViewPager, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }
    /*加载失败调用*/
    @Override
    public void onFailure(Exception e) {
        pb_bar.setVisibility(View.GONE); Toast.makeText(this,"数据加载错误",Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onProgressUpdate(int progress, int total) {

    }
}
