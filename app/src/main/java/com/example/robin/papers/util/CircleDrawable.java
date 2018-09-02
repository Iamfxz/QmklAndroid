package com.example.robin.papers.util;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;


//制作圆形图片以及图像压缩

public class CircleDrawable extends Drawable {

    private static final String TAG="CircleDrawable";
    private Bitmap bitmap;

    private BitmapShader bitmapShader;

    private Paint paint;

    // 圆心
    private float cx, cy;

    // 半径
    private float radius;

    public CircleDrawable(Drawable drawable, Context context, int size) {
        if(drawable!=null){
            size = dip2px(context, size);
            Log.d(TAG, "size:"+size+"dip2px(context, size):"+dip2px(context, size));
            drawable = zoomDrawable(drawable, dip2px(context, size), dip2px(context, size));

            this.bitmap = drawableToBitmap(drawable);
            bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(bitmapShader);

            cx = size / 2;
            cy = size / 2;
            radius = size / 2;
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if(paint!=null){
            canvas.drawCircle(cx, cy, radius, paint);
        }
    }

    /**
     * 缩放Drawable
     * */
    public static Drawable zoomDrawable(Drawable drawable, int w, int h) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap oldbmp = drawableToBitmap(drawable);
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
                matrix, true);
        return new BitmapDrawable(null, newbmp);
    }

    /**
     * Drawable转Bitmap
     * */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * dp to px
     * */
    public static int dip2px(Context context, float dipValue) {
        Resources resources = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, resources.getDisplayMetrics());
    }

    public static int dip2px1(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private int dip2px2(Context context,float dpValue){
        float scale=context.getResources().getDisplayMetrics().density;
        return (int)(dpValue*scale+0.5f);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }


    private static String PHOTO_FILE_NAME = "PMSManagerPhoto";

    /**
     * 获取图片的旋转角度
     *
     * @param filePath
     * @return
     */
    public static int getRotateAngle(String filePath) {
        int rotate_angle = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(filePath);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate_angle = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate_angle = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate_angle = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotate_angle;
    }

    /**
     * 旋转图片角度
     *
     * @param angle
     * @param bitmap
     * @return
     */
    public static Bitmap setRotateAngle(int angle, Bitmap bitmap) {

        if (bitmap != null) {
            Matrix m = new Matrix();
            m.postRotate(angle);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), m, true);
            return bitmap;
        }
        return bitmap;

    }

    //转换为圆形状的bitmap
    public static Bitmap createCircleImage(Bitmap source) {
        int length = source.getWidth() < source.getHeight() ? source.getWidth() : source.getHeight();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(length, length, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        canvas.drawCircle(length / 2, length / 2, length / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, 0, 0, paint);
        return target;
    }



    /**
     * 图片压缩-质量压缩
     *
     * @param filePath 源图片路径
     * @return 压缩后的路径
     */

    public static String compressImage(String filePath) {

        //原文件
        File oldFile = new File(filePath);

        try {
            //图片超过1M压缩
            if(isMoreThanOneM(getFileSize(oldFile))){
                Log.d(TAG, "图片超过1M");
                //压缩文件路径 照片路径/
                String targetPath = oldFile.getPath();
                int quality = 50;//压缩比例0-100
                Bitmap bm = getSmallBitmap(filePath);//获取一定尺寸的图片
                int degree = getRotateAngle(filePath);//获取相片拍摄角度

                if (degree != 0) {//旋转照片角度，防止头像横着显示
                    bm = setRotateAngle(degree,bm);
                }
                File outputFile = new File(targetPath);
                try {
                    if (!outputFile.exists()) {
                        outputFile.getParentFile().mkdirs();
                        //outputFile.createNewFile();
                    } else {
                        outputFile.delete();
                    }
                    FileOutputStream out = new FileOutputStream(outputFile);
                    bm.compress(Bitmap.CompressFormat.JPEG, quality, out);
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    return filePath;
                }
                return outputFile.getPath();
            }
            else {
                Log.d(TAG, "图片未超过1M");
                return filePath;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filePath;

    }

    /**
     * 根据路径获得图片信息并按比例压缩，返回bitmap
     */
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//只解析图片边沿，获取宽高
        BitmapFactory.decodeFile(filePath, options);
        // 计算缩放比
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        // 完整解析图片返回bitmap
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }


    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }


    public static long getFileSize(File f) throws Exception{

        long l=0;

        if ( f.exists() ){

            FileInputStream mFIS = new FileInputStream(f);

            l= mFIS.available();

            mFIS.close();
        } else {

            l=0;

        }

        return l;

    }

    /**

     * 将文件大小转换成字节，返回文件大小是否超过1M

     */

    public static boolean isMoreThanOneM(long fSize){

        DecimalFormat df = new DecimalFormat("#.00");

        String fileSizeString = "";

//        if(fSize<1024 && fSize < 104875){
//
//            fileSizeString = df.format((double) fSize) + "B";
//
//        } else if ( fSize >104875 &&fSize < 1073741824){
//
//            fileSizeString = df.format((double) fSize/1024) + "K";
//
//        } else if ( fSize >1073741824){
//
//            fileSizeString = df.format((double) fSize/104875 ) + "M";
//            return true;
//        } else {
//
//            fileSizeString = df.format((double) fSize/1073741824) + "G";
//            return true;
//        }
//
//        return false;
        if( fSize <1073741824){
            return false;
        }
        return true;
    }



}
