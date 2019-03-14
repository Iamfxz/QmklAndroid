package com.example.robin.papers.util;

import android.content.Context;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.robin.papers.R;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import java.lang.reflect.Field;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * 创建不同的对话框
 */
public class DialogUtils {

    /**
     * @param context
     * @return
     * 创建等待对话框
     */
    public static ZLoadingDialog getZLoadingDialog(Context context){
        ZLoadingDialog dialog = new ZLoadingDialog(context);
        dialog.setLoadingBuilder(Z_TYPE.STAR_LOADING)//设置类型
                .setLoadingColor(context.getResources().getColor(R.color.blue))//颜色
                .setHintText("Loading")
                .setCanceledOnTouchOutside(false)
                .show();
        return dialog;
    }
    public static ZLoadingDialog getZLoadingDialog(Context context,String hintText){
        ZLoadingDialog dialog = new ZLoadingDialog(context);
        dialog.setLoadingBuilder(Z_TYPE.STAR_LOADING)//设置类型
                .setLoadingColor(context.getResources().getColor(R.color.blue))//颜色
                .setHintText(hintText)
                .setCanceledOnTouchOutside(false)
                .show();
        return dialog;
    }


    /**
     * @param context 上下文
     * @param view popupMenu需要依附的view
     * @param userId 帖子用户id
     * @return
     */
    public static PopupMenu showPopupMenu(Context context, View view, final int userId) {
        // 这里的view代表popupMenu需要依附的view
        PopupMenu popupMenu = new PopupMenu(context, view);
        // 获取布局文件
        popupMenu.getMenuInflater().inflate(R.menu.popupemu, popupMenu.getMenu());

        //根据是否是本人的帖子显示删除按钮
        if(userId==Integer.parseInt(SharedPreferencesUtils.getStoredMessage(context,"id"))){
            popupMenu.getMenu().findItem(R.id.del_post).setVisible(true);
        }
        else {
            popupMenu.getMenu().findItem(R.id.del_post).setVisible(false);
        }
        //显示图标
        try {
            Field field = popupMenu.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            MenuPopupHelper helper = (MenuPopupHelper) field.get(popupMenu);
            helper.setForceShowIcon(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        popupMenu.show();

        return popupMenu;
    }

    public static void showSoftInputFromWindow(EditText editText){
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager) editText.getContext().getSystemService(INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(editText, 0);
    }

    public static void hideEdit(RelativeLayout bottomView, EditText editText) {
        if (bottomView.getVisibility() == View.VISIBLE) {
            bottomView.setVisibility(View.GONE);
            editText.setText("");
            InputMethodManager inputManager =
                    (InputMethodManager) editText.getContext().getSystemService(INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(editText.getWindowToken(), 0); //强制隐藏键盘
        }
    }
}
