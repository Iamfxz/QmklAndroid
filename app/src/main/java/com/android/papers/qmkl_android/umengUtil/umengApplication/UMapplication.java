package com.android.papers.qmkl_android.umengUtil.umengApplication;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;


public class UMapplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        UMConfigure.init(this, "5b694daf8f4a9d591a000013","umeng_test", UMConfigure.DEVICE_TYPE_PHONE,"b45044924a5e44d09bbd9272e9f7db03");
        context = getApplicationContext();
        initImageLoader(context);
    }

    {
        PlatformConfig.setQQZone("1104895232", "Blozk0r9zHIksbos");
    }

    //返回
    public static Context getContext(){
        return context;
    }

    public void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .memoryCacheSize(2 * 1024 * 1024) //缓存到内存的最大数据
                .memoryCacheSize(50 * 1024 * 1024) //设置内存缓存的大小
                .diskCacheFileCount(200)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }
}
