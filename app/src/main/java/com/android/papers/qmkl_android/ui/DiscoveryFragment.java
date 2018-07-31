package com.android.papers.qmkl_android.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.papers.qmkl_android.R;
import com.android.papers.qmkl_android.activity.UserInfoActivity;
import com.android.papers.qmkl_android.util.DownLoader;
import com.android.papers.qmkl_android.util.RetrofitUtils;
import com.android.papers.qmkl_android.util.SDCardUtils;
import com.android.papers.qmkl_android.util.SharedPreferencesUtils;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class DiscoveryFragment extends Fragment {

    @BindView(R.id.head_img)
    CircleImageView headImg;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.user_college_name)
    TextView userCollege;
    @BindView(R.id.next)
    ImageView next;
    @BindView(R.id.user_info)
    LinearLayout userInfo;

    private static final String TAG = "DiscoveryActivityTag";
    private static boolean firstLoad=true;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discovery, container, false);
        ButterKnife.bind(this, view);

        if(firstLoad){
            //使用com.zyao89:zloading:1.1.2引用別人的加载动画
            ZLoadingDialog dialog = new ZLoadingDialog(getContext());
            dialog.setLoadingBuilder(Z_TYPE.STAR_LOADING)//设置类型
                    .setLoadingColor(getResources().getColor(R.color.blue))//颜色
                    .setHintText("loading...")
                    .show();
            RetrofitUtils.postUserInfo(getContext(), SharedPreferencesUtils.getStoredMessage(getContext(),"token"),headImg,userName,userCollege,dialog);
            firstLoad=false;
        }
        else{
            setUserInfo();
        }
        userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), UserInfoActivity.class);
                startActivity(intent);
            }
        });



        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    //刷新各控件内容
    private void setUserInfo(){
        userName.setText(SharedPreferencesUtils.getStoredMessage(getContext(),"nickname"));
        userCollege.setText(SharedPreferencesUtils.getStoredMessage(getContext(),"college"));

        final Drawable drawable=Drawable.createFromPath(SDCardUtils.getAvatarImage(SharedPreferencesUtils.getStoredMessage(getContext(),"avatar")));
        headImg.setImageDrawable(drawable);

    }

    @Override
    public void onResume() {
        setUserInfo();
        super.onResume();
    }



}
