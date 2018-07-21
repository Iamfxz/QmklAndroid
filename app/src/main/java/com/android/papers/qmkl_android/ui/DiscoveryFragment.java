package com.android.papers.qmkl_android.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.papers.qmkl_android.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class DiscoveryFragment extends Fragment {


    @BindView(R.id.head_img)
    CircleImageView headImg;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.user_phone_num)
    TextView userPhoneNum;
    @BindView(R.id.next)
    ImageView next;
    @BindView(R.id.user_info)
    LinearLayout userInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discovery, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @OnClick(R.id.user_info)
    public void onClick() {
    }
}
