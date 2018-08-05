package com.android.papers.qmkl_android.activity;


import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.papers.qmkl_android.R;
import com.android.papers.qmkl_android.requestModel.PerfectInfoRequest;
import com.android.papers.qmkl_android.requestModel.TokenRequest;
import com.android.papers.qmkl_android.requestModel.UpdateUserRequest;
import com.android.papers.qmkl_android.util.CircleDrawable;
import com.android.papers.qmkl_android.util.MyTextWatcher;
import com.android.papers.qmkl_android.util.RetrofitUtils;
import com.android.papers.qmkl_android.util.SHAArithmetic;
import com.android.papers.qmkl_android.util.SharedPreferencesUtils;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//用户注册界面二--完善个人信息
public class PerfectInfoActivity extends BaseActivity {

    public static final String TAG = "PerfectInfoActivity";
    public static String[] colleges=null;
    public static String[] academies=null;
    private boolean hasSetImg=false;

    @BindView(R.id.head_img)
    ImageView headImg;
    @BindView(R.id.user_phone)
    TextInputLayout userPhone;
    @BindView(R.id.user_psw)
    TextInputLayout userPsw;
    @BindView(R.id.confirm_psw)
    TextInputLayout confirmPsw;
    @BindView(R.id.user_nickname)
    TextInputLayout userNickname;
    @BindView(R.id.gender_tiLayout)
    TextInputLayout genderLayout;
    @BindView(R.id.user_gender)
    RelativeLayout userGender;
    @BindView(R.id.gender)
    TextInputEditText genderEdt;
    @BindView(R.id.college_tiLayout)
    TextInputLayout collegeLayout;
    @BindView(R.id.user_college)
    RelativeLayout userCollege;
    @BindView(R.id.college_name)
    TextInputEditText collegeName;
    @BindView(R.id.academy_tiLayout)
    TextInputLayout academyLayout;
    @BindView(R.id.user_academy)
    RelativeLayout userAcademy;
    @BindView(R.id.academy_name)
    TextInputEditText academyName;
    @BindView(R.id.enterYear_tiLayout)
    TextInputLayout enterYearLayout;
    @BindView(R.id.user_enterYear)
    RelativeLayout userEnterYear;
    @BindView(R.id.enterYear_num)
    TextInputEditText enterYearNum;
    @BindView(R.id.next)
    Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfect_info);
        ButterKnife.bind(this);

        nextBtn.setEnabled(false);
        //所有信息都不为空时,下一步按钮变色
        Objects.requireNonNull(userPhone.getEditText()).addTextChangedListener(new MyTextWatcher(this,nextBtn,userPhone,userPsw,confirmPsw,userNickname,genderLayout,collegeLayout,academyLayout,enterYearLayout));
        Objects.requireNonNull(userPsw.getEditText()).addTextChangedListener(new MyTextWatcher(this,nextBtn,userPhone,userPsw,confirmPsw,userNickname,genderLayout,collegeLayout,academyLayout,enterYearLayout));
        Objects.requireNonNull(confirmPsw.getEditText()).addTextChangedListener(new MyTextWatcher(this,nextBtn,userPhone,userPsw,confirmPsw,userNickname,genderLayout,collegeLayout,academyLayout,enterYearLayout));
        Objects.requireNonNull(userNickname.getEditText()).addTextChangedListener(new MyTextWatcher(this,nextBtn,userPhone,userPsw,confirmPsw,userNickname,genderLayout,collegeLayout,academyLayout,enterYearLayout));
        Objects.requireNonNull(genderLayout.getEditText()).addTextChangedListener(new MyTextWatcher(this,nextBtn,userPhone,userPsw,confirmPsw,userNickname,genderLayout,collegeLayout,academyLayout,enterYearLayout));
        Objects.requireNonNull(collegeLayout.getEditText()).addTextChangedListener(new MyTextWatcher(this,nextBtn,userPhone,userPsw,confirmPsw,userNickname,genderLayout,collegeLayout,academyLayout,enterYearLayout));
        Objects.requireNonNull(academyLayout.getEditText()).addTextChangedListener(new MyTextWatcher(this,nextBtn,userPhone,userPsw,confirmPsw,userNickname,genderLayout,collegeLayout,academyLayout,enterYearLayout));
        Objects.requireNonNull(enterYearLayout.getEditText()).addTextChangedListener(new MyTextWatcher(this,nextBtn,userPhone,userPsw,confirmPsw,userNickname,genderLayout,collegeLayout,academyLayout,enterYearLayout));


        //初始化用户名（手机号），此内容不可更改
        userPhone.getEditText().setText(SharedPreferencesUtils.getStoredMessage(this,"phone"));

    }


    @OnClick({R.id.head_img,R.id.user_phone,R.id.user_psw,R.id.confirm_psw,R.id.user_nickname,R.id.gender,
            R.id.college_name,R.id.academy_name,R.id.enterYear_num,R.id.next})
    public void onClick(final View view){
        AlertDialog.Builder builder;
        final ZLoadingDialog dialog;
        switch (view.getId()){
            case R.id.head_img:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,1);
                break;
            case R.id.user_phone:
                //无操作
                break;
            case R.id.user_psw:
                //无操作
                break;
            case R.id.confirm_psw:
                //无操作
                break;
            case R.id.user_nickname:
                //无操作
                break;
            case R.id.gender:
                final String[] genderItems = new String[] { "男","女" };
                builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("选择性别")
                        .setItems(genderItems, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface DialogInterface, int which) {
                               genderEdt.setText(genderItems[which]);
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
                RetrofitUtils.postAllColleges(this,builder,collegeName,academyName,dialog);
                break;
            case R.id.academy_name:
                if(collegeName.getText().toString().equals("")){
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
                    RetrofitUtils.postAllAcademies(this,collegeName.getText().toString(),builder,academyName,dialog);
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
                                enterYearNum.setText(enterYearItems[which]);
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
                if(!isRightPsw(Objects.requireNonNull(userPsw.getEditText()).getText().toString())){
                    Toast.makeText(getApplicationContext(),"密码必须由8-16位字符和数字组合构成",Toast.LENGTH_SHORT).show();
                }
                else if(!userPsw.getEditText().getText().toString().equals(Objects.requireNonNull(confirmPsw.getEditText()).getText().toString())){
                    Toast.makeText(getApplicationContext(),"两次密码输入不一致",Toast.LENGTH_SHORT).show();
                }
                else if (Objects.requireNonNull(userNickname.getEditText()).getText().toString().trim().equals("")){
                    Toast.makeText(getApplicationContext(),"用户名不能为空",Toast.LENGTH_SHORT).show();
                }
                else if(!hasSetImg){
                    builder  = new AlertDialog.Builder(this);
                    builder.setTitle("你还未选择上传头像，是否使用默认头像？");
                    builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAG, "加密： "+SHAArithmetic.encode(userPsw.getEditText().getText().toString()));
                            PerfectInfoRequest perfectInfoRequest=new PerfectInfoRequest(
                                    userPhone.getEditText().getText().toString(),//手机号
                                    userPhone.getEditText().getText().toString(),//用户名
                                    SHAArithmetic.encode(userPsw.getEditText().getText().toString()),//加密密码
                                    userNickname.getEditText().getText().toString().trim(),//用户昵称
                                    enterYearNum.getText().toString(),//入学年份
                                    genderEdt.getText().toString(),//性别
                                    academyName.getText().toString(),//学院
                                    collegeName.getText().toString()//学校
                            );
                            RetrofitUtils.postPerfectInfo(view.getContext(),perfectInfoRequest,PerfectInfoActivity.this);
                        }
                    });
                    builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(intent,1);
                        }
                    });
                    builder.show();
                }
                else {
                    PerfectInfoRequest perfectInfoRequest=new PerfectInfoRequest(
                            userPhone.getEditText().getText().toString(),//手机号
                            userPhone.getEditText().getText().toString(),//用户名
                            SHAArithmetic.encode(userPsw.getEditText().getText().toString()),//加密密码
                            userNickname.getEditText().getText().toString().trim(),//用户昵称
                            enterYearNum.getText().toString(),//入学年份
                            genderEdt.getText().toString(),//性别
                            academyName.getText().toString(),//学院
                            collegeName.getText().toString()//学校
                    );

                    RetrofitUtils.postPerfectInfo(view.getContext(),perfectInfoRequest,PerfectInfoActivity.this);

                }


                break;

        }
    }


    //判断密码是否合法
    private boolean isRightPsw(String psw){
        boolean isDigit = false;//定义一个boolean值，用来表示是否包含数字
        boolean isChar = false;//定义一个boolean值，用来表示是否包含字母
        //必须是8-16位密码
        if(psw.length()<8 || psw.length()>16){
            return false;
        }
        //同时包含字符和数字
        for(int i=0;i<psw.length();i++){
            if(Character.isDigit(psw.charAt(i))){
                isDigit=true;
            }
            else {
                isChar=true;
            }
        }
        return (isChar && isDigit);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();
            String imagePath=null;
            if (DocumentsContract.isDocumentUri(this,uri)){
                //如果是document类型的uri 则通过id进行解析处理
                String docId = DocumentsContract.getDocumentId(uri);
                if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                    //解析出数字格式id
                    String id = docId.split(":")[1];
                    String selection = MediaStore.Images.Media._ID + "=" +id;
                    imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
                }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("" +
                            "content://downloads/public_downloads"),Long.valueOf(docId));
                    imagePath = getImagePath(contentUri,null);
                }
            }else if ("content".equals(uri.getScheme())){
                //如果不是document类型的uri，则使用普通的方式处理
                imagePath = getImagePath(uri,null);
            }
            SharedPreferencesUtils.setStoredMessage(this,"imagePath",imagePath);
            displayImage(imagePath);
        }

    }
    /**
     * 通过 uri seletion选择来获取图片的真实uri
     * @param uri
     * @param seletion
     * @return
     */
    private String getImagePath(Uri uri, String seletion){
        String path = null;
        Cursor cursor = getContentResolver().query(uri,null,seletion,null,null);
        if (cursor != null){
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    /**
     * 通过imagepath来绘制immageview图像
     * @param imagePath
     */
    private void displayImage(String imagePath){
//        if (imagePath != null){
//            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
//
//            final ZLoadingDialog dialog = new ZLoadingDialog(this);
//            dialog.setLoadingBuilder(Z_TYPE.STAR_LOADING)//设置类型
//                    .setLoadingColor(getResources().getColor(R.color.blue))//颜色
//                    .setHintText("upLoading...")
//                    .setCanceledOnTouchOutside(false)
//                    .show();
//            RetrofitUtils.postUserAvatar(this,imagePath,avatar,bitmap,dialog);
//        }else{
//            Toast.makeText(this,"图片获取失败",Toast.LENGTH_SHORT).show();
//        }
        if(imagePath!=null){
            imagePath= CircleDrawable.compressImage(imagePath);
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            headImg.setImageBitmap(bitmap);
            hasSetImg=true;
        }
        else{
            Toast.makeText(this,"图片获取失败",Toast.LENGTH_SHORT).show();
        }
    }
}
