package com.example.robin.papers.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;

import com.example.robin.papers.R;
import com.bumptech.glide.Glide;
import com.youth.banner.loader.ImageLoader;

import java.io.InputStream;

//图片轮播的图片加载器
public class GlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        /**
         注意：
         1.图片加载器由自己选择，这里不限制，只是提供几种使用方法
         2.返回的图片路径为Object类型，由于不能确定你到底使用的那种图片加载器，
         传输的到的是什么格式，那么这种就使用Object接收和返回，你只需要强转成你传输的类型就行，
         切记不要胡乱强转！
         */

//        //Glide 加载图片简单用法
//        Glide.with(context).load(path).into(imageView);
//
//        //Picasso 加载图片简单用法
//        Picasso.with(context).load(path).into(imageView);
//
        //用fresco加载图片简单用法，记得要写下面的createImageView方法
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//        imageView.setImageResource((int)path);
        imageView.setImageBitmap(readBitMap(context,(int)path));
    }

    //提供createImageView 方法，如果不用可以不重写这个方法，主要是方便自定义ImageView的创建
//    @Override
//    public ImageView createImageView(Context context) {
//        //使用fresco，需要创建它提供的ImageView，当然你也可以用自己自定义的具有图片加载功能的ImageView
//        ImageView simpleDraweeView=new ImageView(context);
//        return simpleDraweeView;
//    }


    /**
     * 以最省内存的方式读取本地资源的图片,防止oom(out of memory)
     *
     * @param context
     * @param resId
     * @return
     */
    public static Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }
}