package com.whp.xmppchat.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/11/18.
 */

public class ToastUtils {
    /**
     * 可以在子线程弹出toast
     */
    public static void showToastSafe(final Context context, final String text){
        ThreadUtils.runInUIThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
