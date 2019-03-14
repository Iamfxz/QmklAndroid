package com.example.robin.papers.studentCircle.listener;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.example.robin.papers.studentCircle.adapter.MixListAdapter;
import com.example.robin.papers.studentCircle.studentCircleActivity.DetailsActivity;

public class UserContentOnclick implements View.OnClickListener {

    private Context context;
    private int index;
    private Class sourceClass;

    public UserContentOnclick(Context context, int index, Class sourceClass) {
        this.context=context;
        this.index = index;
        this.sourceClass=sourceClass;
    }

    @Override
    public void onClick(View v) {
        Intent intent=new Intent(context, DetailsActivity.class);
        intent.putExtra("index", index);
        intent.putExtra("isComment",false);
        intent.putExtra("sourceClass",sourceClass);
        context.startActivity(intent);
    }
}
