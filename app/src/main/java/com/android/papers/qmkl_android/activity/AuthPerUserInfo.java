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

    @BindView(R.id.head_img)//头像
    CircleImageView headImg;
    @BindView(R.id.next)//下一步按钮
    Button nextBtn;
    @BindView(R.id.user_nickname)//用户昵称输入
    TextInputLayout userNickname;
    @BindView(R.id.gender_tiLayout)//用户性别
    TextInputLayout genderLayout;
    @BindView(R.id.college_tiLayout)//用户学校
    TextInputLayout collegeLayout;
    @BindView(R.id.academy_tiLayout)//用户学院
    TextInputLayout academyLayout;
    @BindView(R.id.enterYear_tiLayout)//用户年级
    TextInputLayout enterYearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_perfect_info);
        ButterKnife.bind(this);

        initView();

    }

    /**
     *      初始化视图
     */
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


        //获取第三方昵称、昵称长度
        String nickName = SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(),"nickname");
        Objects.requireNonNull(userNickname.getEditText()).setText(nickName);

        EditTextFilter.setProhibitEmoji(userNickname.getEditText(),this);
        userNickname.getEditText().setSelection(nickName.length());//光标

        //获取第三方性别
        String gender = SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(),"gender");
        Objects.requireNonNull(genderLayout.getEditText()).setText(gender);


        //下一步按钮不可用
        nextBtn.setEnabled(false);

        //添加昵称、性别、大学、专业、入学年级等的监听
        Objects.requireNonNull(userNickname.getEditText()).addTextChangedListener(new MyTextWatcher(this,nextBtn,userNickname,genderLayout,collegeLayout,academyLayout,enterYearLayout));
        Objects.requireNonNull(genderLayout.getEditText()).addTextChangedListener(new MyTextWatcher(this,nextBtn,userNickname,genderLayout,collegeLayout,academyLayout,enterYearLayout));
        Objects.requireNonNull(collegeLayout.getEditText()).addTextChangedListener(new MyTextWatcher(this,nextBtn,userNickname,genderLayout,collegeLayout,academyLayout,enterYearLayout));
        Objects.requireNonNull(academyLayout.getEditText()).addTextChangedListener(new MyTextWatcher(this,nextBtn,userNickname,genderLayout,collegeLayout,academyLayout,enterYearLayout));
        Objects.requireNonNull(enterYearLayout.getEditText()).addTextChangedListener(new MyTextWatcher(this,nextBtn,userNickname,genderLayout,collegeLayout,academyLayout,enterYearLayout));
    }

    /**
     *      监听事件，性别选择，大学选择，学院选择，入学年级选择，下一步选择，后退选择
     * @param view 视图
     */
    @OnClick({R.id.gender,R.id.college_name,R.id.academy_name,R.id.enterYear_num,R.id.next,R.id.back})
    public void onClick(View view){
        AlertDialog.Builder builder;
        final ZLoadingDialog dialog;
        switch (view.getId()){
            case R.id.gender:
                //性别选择
                final String[] genderItems = new String[] { "男","女" };
                builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("请选择您的性别")
                        .setItems(genderItems, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface DialogInterface, int which) {
                                Objects.requireNonNull(genderLayout.getEditText()).setText(genderItems[which]);
                            }
                        });
                AlertDialog alertDialog=builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
                break;
            case R.id.college_name:
                //大学选择
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
                //专业选择
                if(Objects.requireNonNull(collegeLayout.getEditText()).getText().toString().equals("")){
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
                //入学年级选择
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
                                Objects.requireNonNull(enterYearLayout.getEditText()).setText(enterYearItems[which]);
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
                //下一步
                if(EditTextFilter.isIllegal(Objects.requireNonNull(userNickname.getEditText()).getText().toString())){
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
                finish();
                //TODO 返回？
                break;
        }
    }

    /**
     *      获取远程信息失败或者广告版本为最新时, 检查本地头像是否存在
     * @param context 上下文
     * @return 头像是否存在
     */
    private static boolean checkLocalAvatarImage(Context context) {
        Log.d(TAG, "检测本地头像是否存在");
        File avatarImageFile = new File(SDCardUtils.getAvatarImage(SharedPreferencesUtils.getStoredMessage(context,"avatar")));
        return avatarImageFile.exists();
    }
}
