package com.example.robin.papers.studentCircle.studentCircleActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.robin.papers.R;
import com.example.robin.papers.requestModel.PostAddRequest;
import com.example.robin.papers.util.RetrofitUtils;
import com.example.robin.papers.util.SharedPreferencesUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PostAddActivity extends BaseActivity {

    @BindView(R.id.add_post)
    Button postAddBtn;
    @BindView(R.id.content)
    EditText content;
    @BindView(R.id.iv_back)
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        ButterKnife.bind(this);
        addTextWatch();
    }

    @OnClick({R.id.add_post,R.id.iv_back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.add_post:
                if(!isNullContent()){
                    String token= SharedPreferencesUtils.getStoredMessage(this,"token");
                    PostAddRequest postAddRequest=new PostAddRequest(token,content.getText().toString().trim());
                    RetrofitUtils.postAddPost(this,postAddRequest,PostAddActivity.this);
                }

                break;
            case R.id.iv_back:
                if(!isNullContent()){
                    // 创建构建器
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    // 设置参数
                    builder.setTitle("返回后此次编辑不会保存，确认要返回？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {// 积极
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {

                                    startActivity(new Intent(PostAddActivity.this,MixShowActivity.class));
                                    finish();
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {// 消极

                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                        }
                    });
                    builder.create().show();
                }
                else {
                    startActivity(new Intent(PostAddActivity.this,MixShowActivity.class));
                    finish();
                }
                break;
        }

    }

    /**
     * @return 返回content文本编辑框是否为空
     */
    private boolean isNullContent(){
        return content.getText().toString().trim().equals("");
    }

    /**
     * 监听content文本框输入内容时，按钮颜色改变
     */
    private void addTextWatch(){
        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!isNullContent()){
                    postAddBtn.setBackgroundResource(R.drawable.button_post);
                    postAddBtn.setEnabled(true);
                }
                else {
                    postAddBtn.setBackgroundResource(R.drawable.button_post_empty);
                    postAddBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 监听返回按键的事件处理
     *
     * @param keyCode 点击事件的代码
     * @param event   事件
     * @return 有无处理
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(!isNullContent()){
                // 创建构建器
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                // 设置参数
                builder.setTitle("返回后此次编辑不会保存，确认要返回？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {// 积极
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                startActivity(new Intent(PostAddActivity.this,MixShowActivity.class));
                                finish();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {// 消极

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                    }
                });
                builder.create().show();
            }
            else {
                startActivity(new Intent(PostAddActivity.this,MixShowActivity.class));
                finish();
            }
        }
        return true;
    }
}
