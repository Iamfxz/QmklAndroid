package com.example.robin.papers.studentCircle.listener;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.robin.papers.studentCircle.studentCircleActivity.DetailsActivity;
import com.example.robin.papers.util.DialogUtils;

public class CommentOnclick implements View.OnClickListener {
    private Context context;
    private int index;
    private Class sourceClass;

    private RelativeLayout bottomView;
    private EditText editText;

    public CommentOnclick(RelativeLayout bottomView,EditText editText) {
        this.bottomView = bottomView;
        this.editText = editText;
    }

    public CommentOnclick(Context context,int index,Class sourceClass) {
        this.context=context;
        this.index = index;
        this.sourceClass = sourceClass;
    }
    @Override
    public void onClick(View v) {
        if(bottomView!=null){
            bottomView.setVisibility(View.VISIBLE);
            DialogUtils.showSoftInputFromWindow(editText);
        }
        else {
            Intent intent=new Intent(context, DetailsActivity.class);
            intent.putExtra("index", index);
            intent.putExtra("isComment",true);
            intent.putExtra("sourceClass",sourceClass);
            context.startActivity(intent);
        }

    }
}
