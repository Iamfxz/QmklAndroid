package com.android.papers.qmkl_android.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.papers.qmkl_android.R;
import com.android.papers.qmkl_android.requestModel.AuthPerInfoRequest;
import com.android.papers.qmkl_android.umengUtil.umengApplication.UMapplication;
import com.android.papers.qmkl_android.util.CircleDrawable;
import com.android.papers.qmkl_android.util.DownLoader;
import com.android.papers.qmkl_android.util.EditTextFilter;
import com.android.papers.qmkl_android.util.MyTextWatcher;
import com.android.papers.qmkl_android.util.RetrofitUtils;
import com.android.papers.qmkl_android.util.SDCardUtils;
import com.android.papers.qmkl_android.util.SharedPreferencesUtils;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import java.io.File;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

//完善第三方登录时用户资料
public class AuthPerUserInfo extends BaseActivity{

    final static String TAG="AuthPerUserInfo";

    @BindView(R.id.head_img)
    CircleImageView headImg;
    @BindView(R.id.next)
    Button nextBtn;
    @BindView(R.id.user_nickname)
    TextInputLayout userNickname;
    @BindView(R.id.gender_tiLayout)
    TextInputLayout genderLayout;
    @BindView(R.id.college_tiLayout)
    TextInputLayout collegeLayout;
    @BindView(R.id.academy_tiLayout)
    TextInputLayout academyLayout;
    @BindView(R.id.enterYear_tiLayout)
    TextInputLayout enterYearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_perfect_info);
        ButterKnife.bind(this);

        initView();

    }

    private void initView(){
        final Bitmap[] bitmap = new Bitmap[1];
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String imagePath=SDCardUtils.getAvatarImage(SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(),"avatar"));
                    DownLoader.downloadFile(new File(imagePath),
                            SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(),"avatarUrl"));
                    bitmap[0] = BitmapFactory.decodeFile(imagePath);
                    headImg.post(new Runnable() {
                        @Override
                        public void run() {
                            headImg.setImageBitmap(bitmap[0]);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        //获取第三方昵称
        Objects.requireNonNull(userNickname.getEditText()).setText(SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(),"nickname"));
        EditTextFilter.setProhibitEmoji(userNickname.getEditText(),this);
        //获取第三方性别
        Objects.requireNonNull(genderLayout.getEditText()).setText(SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(),"gender"));

        userNickname.getEditText().setSelection(SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(),"nickname").length());
        //下一步按钮不可用
        nextBtn.setEnabled(false);

        Objects.requireNonNull(userNickname.getEditText()).addTextChangedListener(new MyTextWatcher(this,nextBtn,userNickname,genderLayout,collegeLayout,academyLayout,enterYearLayout));
        Objects.requireNonNull(genderLayout.getEditText()).addTextChangedListener(new MyTextWatcher(this,nextBtn,userNickname,genderLayout,collegeLayout,academyLayout,enterYearLayout));
        Objects.requireNonNull(collegeLayout.getEditText()).addTextChangedListener(new MyTextWatcher(this,nextBtn,userNickname,genderLayout,collegeLayout,academyLayout,enterYearLayout));
        Objects.requireNonNull(academyLayout.getEditText()).addTextChangedListener(new MyTextWatcher(this,nextBtn,userNickname,genderLayout,collegeLayout,academyLayout,enterYearLayout));
        Objects.requireNonNull(enterYearLayout.getEditText()).addTextChangedListener(new MyTextWatcher(this,nextBtn,userNickname,genderLayout,collegeLayout,academyLayout,enterYearLayout));
    }

    @OnClick({R.id.gender,R.id.college_name,R.id.academy_name,R.id.enterYear_num,R.id.next,R.id.back})
    public void onClick(View view){
        AlertDialog.Builder builder;
        final ZLoadingDialog dialog;
        switch (view.getId()){
            case R.id.gender:
                final String[] genderItems = new String[] { "男","女" };
                builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("选择性别")
                        .setItems(genderItems, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface DialogInterface, int which) {
                                genderLayout.getEditText().setText(genderItems[which]);
                            }
                        });
                AlertDialog alertDialog=builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
                break;
            case R.id.college_name:
                dialog = new ZLoadingDialog(this);
                dialog.setLoadingBuilder(Z_TYPE.STAR_LOADING)//设置类型
                        .setLoadingColor(getResources().getColor(R.color.blue))//颜色
                        .setHintText("loading...")
                        .setCanceledOnTouchOutside(false)
                        .show();
                builder = new AlertDialog.Builder(this);
                RetrofitUtils.postAllColleges(builder,collegeLayout.getEditText(),academyLayout.getEditText(),dialog);
                break;
            case R.id.academy_name:
                if(collegeLayout.getEditText().getText().toString().equals("")){
                    Toast.makeText(this,"请先选择学校",Toast.LENGTH_SHORT).show();
                }
                else {
                    dialog = new ZLoadingDialog(this);
                    dialog.setLoadingBuilder(Z_TYPE.STAR_LOADING)//设置类型
                            .setLoadingColor(getResources().getColor(R.color.blue))//颜色
                            .setHintText("loading...")
                            .setCanceledOnTouchOutside(false)
                            .show();
                    builder = new AlertDialog.Builder(this);
                    RetrofitUtils.postAllAcademies(collegeLayout.getEditText().getText().toString(),builder,academyLayout.getEditText(),dialog);
                }
                break;
            case R.id.enterYear_num:
                dialog = new ZLoadingDialog(this);
                dialog.setLoadingBuilder(Z_TYPE.STAR_LOADING)//设置类型
                        .setLoadingColor(getResources().getColor(R.color.blue))//颜色
                        .setHintText("loading...")
                        .setCanceledOnTouchOutside(false)
                        .show();
                final String[] enterYearItems = new String[] { "2013","2014","2015","2016","2017","2018","2019" };
                builder = new AlertDialog.Builder(this);
                builder.setTitle("选择入学年份")
                        .setItems(enterYearItems, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface DialogInterface, int which) {
                                enterYearLayout.getEditText().setText(enterYearItems[which]);
                                dialog.dismiss();
                            }
                        });
                //监听返回键
                builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface DialogInterface, int keyCode, KeyEvent event) {
                        if(keyCode==KeyEvent.KEYCODE_BACK){
                            dialog.dismiss();
                        }
                        return false;
                    }
                });
                alertDialog=builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
                break;
            case R.id.next:
                if(EditTextFilter.isIllegal(userNickname.getEditText().getText().toString())){
                    AuthPerInfoRequest authPerInfoRequest=new AuthPerInfoRequest(
                            SharedPreferencesUtils.getStoredMessage(AuthPerUserInfo.this,"uid"),
                            Objects.requireNonNull(userNickname.getEditText()).getText().toString(),
                            "qq", Objects.requireNonNull(genderLayout.getEditText()).getText().toString(),
                            Objects.requireNonNull(enterYearLayout.getEditText()).getText().toString(),
                            SharedPreferencesUtils.getStoredMessage(AuthPerUserInfo.this,"avatarUrl"),
                            Objects.requireNonNull(collegeLayout.getEditText()).getText().toString(),
                            Objects.requireNonNull(academyLayout.getEditText()).getText().toString()
                    );
                    Log.d(TAG,                         SharedPreferencesUtils.getStoredMessage(AuthPerUserInfo.this,"uid")+"\n"+
                            userNickname.getEditText().getText().toString()+"\n"+
                            "qq"+"\n"+genderLayout.getEditText().getText().toString()+"\n"+
                            Objects.requireNonNull(enterYearLayout.getEditText()).getText().toString()+"\n"+
                            SharedPreferencesUtils.getStoredMessage(AuthPerUserInfo.this,"avatarUrl")+"\n"+
                            Objects.requireNonNull(collegeLayout.getEditText()).getText().toString()+"\n"+
                            Objects.requireNonNull(academyLayout.getEditText()).getText().toString());
                    RetrofitUtils.postAuthPerInfo(AuthPerUserInfo.this,authPerInfoRequest,AuthPerUserInfo.this);
                }
                else {
                    Toast.makeText(this,"您的输入中有非法字符",Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.back:

                break;
        }
    }



    //获取远程信息失败或者广告版本为最新时, 检查本地头像是否存在
    private static boolean checkLocalAvatarImage(Context context) {
        Log.d(TAG, "检测本地头像是否存在");
        File avatarImageFile = new File(SDCardUtils.getAvatarImage(SharedPreferencesUtils.getStoredMessage(context,"avatar")));
        return avatarImageFile.exists();
    }
}
