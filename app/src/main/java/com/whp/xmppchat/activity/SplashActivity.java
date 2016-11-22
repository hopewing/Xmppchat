package com.whp.xmppchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;

import com.whp.xmppchat.R;
import com.whp.xmppchat.utils.ThreadUtils;

public class SplashActivity extends AppCompatActivity {

    private boolean isLogin =true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //停留3秒进入主界面
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(3000);
                //进入登录界面
                loadMain();
            }
        });

    }

    /**
     * //进入登录界面
     */
    private void loadMain() {
        if (isLogin){
            Intent login=new Intent(this,LoginActivity.class);
            startActivity(login);
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        isLogin =false;
        super.onDestroy();
    }

}
