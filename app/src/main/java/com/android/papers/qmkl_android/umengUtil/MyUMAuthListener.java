//package com.android.papers.qmkl_android.umengUtil;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.text.InputFilter;
//import android.text.SpannableString;
//import android.text.Spanned;
//import android.text.TextUtils;
//import android.util.Log;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.android.papers.qmkl_android.activity.LoginActivity;
//import com.android.papers.qmkl_android.requestModel.UMengLoginRequest;
//import com.android.papers.qmkl_android.util.RetrofitUtils;
//import com.android.papers.qmkl_android.util.SharedPreferencesUtils;
//import com.umeng.socialize.UMAuthListener;
//import com.umeng.socialize.bean.SHARE_MEDIA;
//
//import java.util.Map;
//import java.util.Set;
//
////qq登录与退出的UMAuthListener
//public class MyUMAuthListener implements UMAuthListener {
//    private final static String TAG="MyUMAuthListener";
//
//    private Context context;
//    private String platform;
//    private Activity startAct;
//    private boolean isLogin;
//
//    public MyUMAuthListener(Context context, Activity startAct,String platform,boolean isLogin){
//        this.context=context;
//        this.platform=platform;
//        this.startAct=startAct;
//        this.isLogin=isLogin;
//    }
//
//    @Override
//    public void onStart(SHARE_MEDIA share_media) {
//        Log.d(TAG, "onStart: ");
//    }
//
//    @Override
//    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
//        Log.d(TAG, "onComplete: ");
//        if(isLogin){
//            doLogin(map);
//        }
//        else{
//            exitLogin();
//        }
//    }
//
//    @Override
//    public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
//        Toast.makeText(context,"授权失败",Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onCancel(SHARE_MEDIA share_media, int i) {
//        Toast.makeText(context,"授权已取消",Toast.LENGTH_SHORT).show();
//    }
//
//    private void doLogin(Map<String, String> map){
//        Log.d(TAG, "onComplete: ");
//        Set<String> set = map.keySet();
//        for (String string : set) {
//            System.out.printf(string+":"+map.get(string)+"\n");
//            //获取用户昵称
//            if(string.equals("name") && !map.get(string).isEmpty()){
//                SharedPreferencesUtils.setStoredMessage(context,"nickname",map.get(string));
//            }
//            //获取用户性别
//            else if(string.equals("gender") && !map.get(string).isEmpty()){
//                SharedPreferencesUtils.setStoredMessage(context,"gender",map.get(string));
//            }
//            //获取用户头像名称
//            else if(string.equals("openid") && !map.get(string).isEmpty()){
//                SharedPreferencesUtils.setStoredMessage(context,"avatar",map.get(string)+".jpg");
//                Log.d(TAG,  SharedPreferencesUtils.getStoredMessage(context,"avatar"));
//            }
//            //获取用户头像路径
//            else if(string.equals("iconurl") && !map.get(string).isEmpty()){
//                SharedPreferencesUtils.setStoredMessage(context,"avatarUrl",map.get(string));
//            }
//            //获取用户uid
//            else if (string.equals("uid") && !map.get(string).isEmpty()) {
//                SharedPreferencesUtils.setStoredMessage(context,"uid",map.get(string));
//                UMengLoginRequest uMengLoginRequest=new UMengLoginRequest(map.get(string),platform);
//                RetrofitUtils.postAuthLogin(context,uMengLoginRequest,startAct);
//            }
//            else if(string.equals("uid") && map.get(string).isEmpty()) {
//                Toast.makeText(context,"登录失败",Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private void exitLogin(){
//        SharedPreferencesUtils.setStoredMessage(context,"hasLogin","false");
//        SharedPreferencesUtils.setStoredMessage(context,"token",null);
//        startAct.startActivity(new Intent(startAct,LoginActivity.class));
//        startAct.finish();
//    }
//
//
//
//
//
//}
//
//
