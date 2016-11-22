package com.whp.xmppchat.utils;

import android.os.Handler;

/**
 * Created by Administrator on 2016/11/18.
 */

public class ThreadUtils {
    /**
     * 子线程执行task
     */
    public static void runInThread(Runnable task) {
        new Thread(task).start();
    }

    /**
     * 主线程下的handler
     */
    public static Handler mHandler = new Handler();

    /**
     * UI线程执行task
     */
    public static void runInUIThread(Runnable task) {
        mHandler.post(task);
    }

}
