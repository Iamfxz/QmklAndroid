package com.example.robin.papers.ui;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robin.papers.R;
import com.example.robin.papers.umengUtil.umengApplication.UMapplication;
import com.example.robin.papers.util.ConstantUtils;
import com.example.robin.papers.util.GlideImageLoader;
import com.example.robin.papers.util.SDCardUtils;
import com.umeng.analytics.MobclickAgent;
import com.youth.banner.Banner;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.robin.papers.util.ConstantUtils.*;

public class StudentsCircleFragment extends Fragment {

    private List images = new ArrayList<>(Arrays.asList(R.drawable.glide0,R.drawable.glide1,R.drawable.glide2,R.drawable.glide3,R.drawable.glide4));
    private static String mPackageName="com.example.robin.papers.ui.StudentsCircleFragment";

    @BindView(R.id.QRCode)
    ImageView QRCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_students_circle, container, false);
        ButterKnife.bind(this,view);
        initOnCreateView();
        setGlideImageLoader(view);
        onLongClick();
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
        layout.setPadding(13,0,0,0);
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

    //长按公众号二维码出现保存照片
    private void onLongClick(){
        QRCode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setItems(new String[]{SAVE_PICTURE}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        QRCode.setDrawingCacheEnabled(true);
                        Bitmap imageBitmap = QRCode.getDrawingCache();
                        if (imageBitmap != null) {
                            new SaveImageTask().execute(imageBitmap);
                        }
                    }
                });
                builder.show();

                return true;
            }
        });
    }



    private class SaveImageTask extends AsyncTask<Bitmap, Void, String> {
        @Override
        protected String doInBackground(Bitmap... params) {
            String result = SAVE_PICTURE_FAILED;
            try {
                String sdcard = SDCardUtils.getSaveImagePath();

                File file = new File(sdcard);
                if (!file.exists()) {
                    file.mkdirs();
                }

                File imageFile = new File(file.getAbsolutePath(),new Date().getTime()+".jpg");
                FileOutputStream outStream = null;
                outStream = new FileOutputStream(imageFile);
                Bitmap image = params[0];
                image.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.flush();
                outStream.close();
                result = String.format(SAVE_PICTURE_SUCCESS,file.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(UMapplication.getContext(),result,Toast.LENGTH_SHORT).show();

            QRCode.setDrawingCacheEnabled(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(mPackageName);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(mPackageName);
    }
}
