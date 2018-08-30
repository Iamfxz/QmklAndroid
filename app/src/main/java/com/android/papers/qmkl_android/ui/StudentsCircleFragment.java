package com.android.papers.qmkl_android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.papers.qmkl_android.R;
import com.android.papers.qmkl_android.studentCircle.dwcorephoto.MixShowActivity;
import com.android.papers.qmkl_android.util.GlideImageLoader;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class StudentsCircleFragment extends Fragment {

    private List images = new ArrayList<>(Arrays.asList(R.drawable.glide0,R.drawable.glide1,R.drawable.glide2,R.drawable.glide3,R.drawable.glide4));


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_students_circle, container, false);

        initOnCreateView();
        setGlideImageLoader(view);
//        Button next=view.findViewById(R.id.next);
//        next.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(view.getContext(),"进入朋友圈",Toast.LENGTH_SHORT).show();
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Intent intent=new Intent(getActivity(), MixShowActivity.class);
//                        startActivity(intent);
//                    }
//                }).start();
//
//            }
//        });

        return view;
    }

    //初始化界面时对标题栏做的一些准备工作
    private void initOnCreateView(){

        TextView title= Objects.requireNonNull(getActivity()).findViewById(R.id.toolbar).findViewById(R.id.title);
        title.setText("趣聊");
        title.setOnClickListener(null);
        Objects.requireNonNull(getActivity()).findViewById(R.id.toolbar).findViewById(R.id.choose_school).setVisibility(View.INVISIBLE);
        Objects.requireNonNull(getActivity()).findViewById(R.id.toolbar).findViewById(R.id.choose_school).setOnClickListener(null);
        RelativeLayout layout = (RelativeLayout) getActivity().findViewById(R.id.toolbar_layout);
        layout.setPadding(0,0,0,0);
    }

    //设置图片轮播
    private void setGlideImageLoader(View view){
        Banner banner = (Banner) view.findViewById(R.id.banner);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        banner.setImages(images);
        //banner设置方法全部调用完毕时最后调用
        banner.start();
    }

}
