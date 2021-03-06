package com.example.robin.papers.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.robin.papers.R;
import com.example.robin.papers.activity.UserInfoActivity;
import com.example.robin.papers.util.SDCardUtils;
import com.example.robin.papers.util.SharedPreferencesUtils;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    private static boolean firstLoad = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discovery, container, false);

        ButterKnife.bind(this, view);
        initOnCreateView();

        setUserInfo();

        userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                startActivity(intent);
            }
        });


        return view;
    }


    //初始化界面时对标题栏做的一些准备工作
    private void initOnCreateView(){
        TextView title = Objects.requireNonNull(getActivity()).findViewById(R.id.toolbar).findViewById(R.id.title);
        title.setText("我和发现");
        title.setOnClickListener(null);
        Objects.requireNonNull(getActivity()).findViewById(R.id.toolbar).findViewById(R.id.choose_school).setVisibility(View.INVISIBLE);
        Objects.requireNonNull(getActivity()).findViewById(R.id.toolbar).findViewById(R.id.choose_school).setOnClickListener(null);

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    //刷新各控件内容
    private void setUserInfo() {
        userName.setText(SharedPreferencesUtils.getStoredMessage(Objects.requireNonNull(getContext()), "nickname"));
        userCollege.setText(SharedPreferencesUtils.getStoredMessage(getContext(), "college"));
        final Drawable drawable = Drawable.createFromPath(SDCardUtils.getAvatarImage(SharedPreferencesUtils.getStoredMessage(getContext(), "avatar")));
        headImg.setImageDrawable(drawable);
    }

    @Override
    public void onResume() {
        setUserInfo();
        super.onResume();
    }


}
