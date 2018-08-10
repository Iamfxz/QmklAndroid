package com.android.papers.qmkl_android.activity;

import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.papers.qmkl_android.R;
import com.android.papers.qmkl_android.requestModel.QueryAcademiesRequest;
import com.android.papers.qmkl_android.requestModel.TokenRequest;
import com.android.papers.qmkl_android.requestModel.UpdateUserRequest;
import com.android.papers.qmkl_android.umengUtil.MyUMAuthListener;
import com.android.papers.qmkl_android.util.ActivityManager;
import com.android.papers.qmkl_android.util.CircleDrawable;
import com.android.papers.qmkl_android.util.ConstantUtils;
import com.android.papers.qmkl_android.util.PermissionUtils;
import com.android.papers.qmkl_android.util.RetrofitUtils;
import com.android.papers.qmkl_android.util.SDCardUtils;
import com.android.papers.qmkl_android.util.SharedPreferencesUtils;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserInfoActivity extends BaseActivity {

    public static final String TAG = "UserInfoActivityTag";

    //保存学院信息
    public static String[] academies=null;
    //保存学校信息
    public static String[] colleges=null;

    @BindView(R.id.user_avatar)
    RelativeLayout user_avatar;
    @BindView((R.id.avatar_info))
    ImageView avatar;
    @BindView(R.id.nickname_info)
    TextView nickname;
    @BindView(R.id.phone_info)
    TextView phone;
    @BindView(R.id.college_info)
    TextView college;
    @BindView(R.id.academy_info)
    TextView academy;
    @BindView(R.id.gender_info)
    TextView gender;
    @BindView(R.id.enterYear_info)
    TextView enterYear;
    @BindView(R.id.iv_back)
    ImageView back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityManager.addActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);
        ButterKnife.bind(this);
        initView();
    }


    @OnClick(R.id.user_avatar)
    public void clickAvatar(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    @OnClick(R.id.user_nickname)
    public void clickNickname(){
        // 创建对话框构建器
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 获取布局
        final View view = View.inflate(UserInfoActivity.this, R.layout.dialog_nickname, null);
        // 获取布局中的控件
        final EditText username = view.findViewById(R.id.nickname);
        username.setText(SharedPreferencesUtils.getStoredMessage(getApplicationContext(),"nickname"));
        // 设置参数
        builder.setTitle("请输入昵称").setView(view);
        // 创建对话框
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        Button confirm=view.findViewById(R.id.btn_confirm);
        Button cancel=view.findViewById(R.id.btn_cancel);


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!username.getText().toString().equals(SharedPreferencesUtils.getStoredMessage(view.getContext(),"nickname"))){
                    //使用com.zyao89:zloading:1.1.2引用別人的加载动画
                    ZLoadingDialog dialog = new ZLoadingDialog(v.getContext());
                    dialog.setLoadingBuilder(Z_TYPE.STAR_LOADING)//设置类型
                            .setLoadingColor(getResources().getColor(R.color.blue))//颜色
                            .setHintText("loading...")
                            .setCanceledOnTouchOutside(false)
                            .show();
                    UpdateUserRequest userRequest=getUserRequest(getApplicationContext(),username.getText().toString(),ConstantUtils.NICKNAME);
                    RetrofitUtils.postUpdateUser(ConstantUtils.NICKNAME,userRequest,alertDialog,nickname,dialog,false);
                }
                else {
                    alertDialog.dismiss();
                }

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    @OnClick(R.id.user_academy)
    public void clickAcademy(){
        //使用com.zyao89:zloading:1.1.2引用別人的加载动画
        ZLoadingDialog dialog = new ZLoadingDialog(this);
        dialog.setLoadingBuilder(Z_TYPE.STAR_LOADING)//设置类型
                .setLoadingColor(getResources().getColor(R.color.blue))//颜色
                .setHintText("loading...")
                .setCanceledOnTouchOutside(false)
                .show();

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        QueryAcademiesRequest academiesRequest=new QueryAcademiesRequest(
                SharedPreferencesUtils.getStoredMessage(getApplicationContext(),"college")
        );
        RetrofitUtils.postAllAcademies(academiesRequest,builder,college,academy,dialog,false);
    }

    @OnClick(R.id.user_college)
    public void clickCollege(){
        ZLoadingDialog dialog = new ZLoadingDialog(this);
        dialog.setLoadingBuilder(Z_TYPE.STAR_LOADING)//设置类型
                .setLoadingColor(getResources().getColor(R.color.blue))//颜色
                .setHintText("loading...")
                .setCanceledOnTouchOutside(false)
                .show();

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        TokenRequest tokenRequest=new TokenRequest(
                SharedPreferencesUtils.getStoredMessage(getApplicationContext(),"token")
        );
        RetrofitUtils.postAllColleges(builder,college,academy,dialog);
    }

    @OnClick(R.id.user_gender)
    public void clickGender(){
        final ZLoadingDialog dialog = new ZLoadingDialog(this);
        dialog.setLoadingBuilder(Z_TYPE.STAR_LOADING)//设置类型
                .setLoadingColor(getResources().getColor(R.color.blue))//颜色
                .setHintText("loading...")
                .setCanceledOnTouchOutside(false)
                .show();
        final String[] genderItems = new String[] { "男","女" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择性别")
                .setItems(genderItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface DialogInterface, int which) {
                        UpdateUserRequest userRequest=getUserRequest(getApplicationContext(),genderItems[which],ConstantUtils.GENDER);
                        RetrofitUtils.postUpdateUser(ConstantUtils.GENDER,userRequest,null,gender,dialog,false);
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
        AlertDialog alertDialog=builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    @OnClick(R.id.user_enterYear)
    public void clickEnterYear(){
        final ZLoadingDialog dialog = new ZLoadingDialog(this);
        dialog.setLoadingBuilder(Z_TYPE.STAR_LOADING)//设置类型
                .setLoadingColor(getResources().getColor(R.color.blue))//颜色
                .setHintText("loading...")
                .setCanceledOnTouchOutside(false)
                .show();
        final String[] enterYearItems = new String[] { "2013","2014","2015","2016","2017","2018","2019" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择入学年份")
                .setItems(enterYearItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface DialogInterface, int which) {
                        UpdateUserRequest userRequest=getUserRequest(getApplicationContext(),enterYearItems[which],ConstantUtils.ENTERYEAR);
                        RetrofitUtils.postUpdateUser(ConstantUtils.ENTERYEAR,userRequest,null,enterYear,dialog,false);
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
        AlertDialog alertDialog=builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    @OnClick(R.id.user_exit)
    public void clickExit(){
        AlertDialog.Builder builder  = new AlertDialog.Builder(this);
        builder.setTitle("退出当前账号");
        builder.setMessage("退出登录后下次登录需重新输入账号与密码，确定退出？");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if(SharedPreferencesUtils.getStoredMessage(UserInfoActivity.this,"platform")==null){
                    final ZLoadingDialog dialog = new ZLoadingDialog(UserInfoActivity.this);
                    dialog.setLoadingBuilder(Z_TYPE.STAR_LOADING)//设置类型
                            .setLoadingColor(getResources().getColor(R.color.blue))//颜色
                            .setHintText("exiting...")
                            .setCanceledOnTouchOutside(false)
                            .show();
                    RetrofitUtils.postExitLogin(SharedPreferencesUtils.getStoredMessage(getApplicationContext(),"username"),UserInfoActivity.this,dialog);
                }
                else if(SharedPreferencesUtils.getStoredMessage(UserInfoActivity.this,"platform").equals("qq")){
                    UMShareAPI.get(UserInfoActivity.this).deleteOauth(UserInfoActivity.this, SHARE_MEDIA.QQ,new MyUMAuthListener(UserInfoActivity.this,UserInfoActivity.this,"qq",false));
                }
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
        }

    @OnClick(R.id.join_us)
    public void clickJoinUs(){
        Intent intent=new Intent(UserInfoActivity.this,WebViewActivity.class);
        intent.putExtra("url","http://cn.mikecrm.com/6lMhybb");
        intent.putExtra("title","加入我们");
        startActivity(intent);
    }

    @OnClick(R.id.iv_back)
    public void clickBack(){
        finish();
    }



    private void initView(){
        Drawable drawable=Drawable.createFromPath(SDCardUtils.getAvatarImage(SharedPreferencesUtils.getStoredMessage(getApplicationContext(),"avatar")));
        avatar.setImageDrawable(drawable);
        nickname.setText(SharedPreferencesUtils.getStoredMessage(getApplicationContext(),"nickname"));
        phone.setText(SharedPreferencesUtils.getStoredMessage(getApplicationContext(),"phone"));
        college.setText(SharedPreferencesUtils.getStoredMessage(getApplicationContext(),"college"));
        academy.setText(SharedPreferencesUtils.getStoredMessage(getApplicationContext(),"academy"));
        gender.setText(SharedPreferencesUtils.getStoredMessage(getApplicationContext(),"gender"));
        enterYear.setText(SharedPreferencesUtils.getStoredMessage(getApplicationContext(),"enterYear"));
    }

    //通过flag判断传入的值是什么，然后构造UpdateUserRequest并返回
    public static UpdateUserRequest getUserRequest(Context context,String value, int flag){
        String nickname,gender,enterYear,college,academy,token;
        nickname=null;
        gender=SharedPreferencesUtils.getStoredMessage(context,"gender");
        enterYear=SharedPreferencesUtils.getStoredMessage(context,"enterYear");
        college=SharedPreferencesUtils.getStoredMessage(context,"college");
        academy=SharedPreferencesUtils.getStoredMessage(context,"academy");
        token=SharedPreferencesUtils.getStoredMessage(context,"token");
        switch (flag){
            case ConstantUtils.NICKNAME:
                nickname=value;
                break;
            case ConstantUtils.GENDER:
                gender=value;
                break;
            case ConstantUtils.ENTERYEAR:
                enterYear=value;
                break;
            case ConstantUtils.COLLEGE:
                college=value;
                break;
            case ConstantUtils.ACADEMY:
                academy=value;
                break;
        }
        UpdateUserRequest userRequest=new UpdateUserRequest(
                nickname,gender,enterYear,college,academy,token);
        return userRequest;
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
            displayImage(imagePath);
        }
    }
    /**
     * 通过 uri seletion选择来获取图片的真实uri
     * @param uri 统一资源标识符
     * @param selection 搜索目标
     * @return 所在地址
     */
    private String getImagePath(Uri uri, String selection){

//        if(PermissionUtils.isHaveWritePer(UserInfoActivity.this,PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE)){
//            Toast.makeText(this,"没有权限",Toast.LENGTH_SHORT).show();
//            PermissionUtils.jumpPermissionPage(UserInfoActivity.this);
//        }else {
            String path = null;
            Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
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
     * @param imagePath 图片地址
     */
    private void displayImage(String imagePath){
        if (imagePath != null){
            imagePath=CircleDrawable.compressImage(imagePath);
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

            final ZLoadingDialog dialog = new ZLoadingDialog(this);
            dialog.setLoadingBuilder(Z_TYPE.STAR_LOADING)//设置类型
                    .setLoadingColor(getResources().getColor(R.color.blue))//颜色
                    .setHintText("upLoading...")
                    .setCanceledOnTouchOutside(false)
                    .show();
            RetrofitUtils.postUserAvatar(imagePath,avatar,bitmap,dialog);
        }else{
            Toast.makeText(this,"图片获取失败",Toast.LENGTH_SHORT).show();
        }
    }
}
