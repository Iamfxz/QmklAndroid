package com.android.papers.qmkl_android.umengUtil.umengApplication;

import android.app.Application;

import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;


public class UMapplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        UMConfigure.init(this, "5b694daf8f4a9d591a000013","umeng_test", UMConfigure.DEVICE_TYPE_PHONE,"b45044924a5e44d09bbd9272e9f7db03");

    }

    {

        PlatformConfig.setQQZone("1107433093", "7oudnQfOjU9E4BtI");
//        PlatformConfig.setQQZone("1106834270", "4zQPFmjvloNZsxWy");
    }
}
