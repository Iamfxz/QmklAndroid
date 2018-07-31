package com.android.papers.qmkl_android.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.papers.qmkl_android.R;

public class StudentsCircleFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_students_circle, container, false);
        TextView title=getActivity().findViewById(R.id.toolbar).findViewById(R.id.title);
        title.setText("学生圈");
        ImageView imageView=getActivity().findViewById(R.id.toolbar).findViewById(R.id.choose_school_btn);
        imageView.setVisibility(View.GONE);
        return view;
    }


}
