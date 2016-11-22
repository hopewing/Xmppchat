package com.whp.xmppchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.whp.xmppchat.R;
import com.whp.xmppchat.service.IMService;
import com.whp.xmppchat.utils.ThreadUtils;
import com.whp.xmppchat.utils.ToastUtils;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class LoginActivity extends AppCompatActivity {
    public static final int PORT = 5222;//端口号
    public static final String SERVICENAME = "hopewing";
    private RelativeLayout mActivityLogin;
    private ImageView mIvLogin;
    private LinearLayout mLlUsername;
    private EditText mEtUsername;
    private LinearLayout mLlPassword;
    private EditText mEtPassword;
    private Button mBtLogin;
    public static final String HOST = "192.168.0.106";//主机IP
    //public static final String HOST = "172.25.133.1";//9机房
    private boolean isLoadMain=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        initDatas();
        initEvents();

    }

    private void initViews() {
        mActivityLogin = (RelativeLayout) findViewById(R.id.activity_login);
        mIvLogin = (ImageView) findViewById(R.id.iv_login);
        mLlUsername = (LinearLayout) findViewById(R.id.ll_username);
        mEtUsername = (EditText) findViewById(R.id.et_username);
        mLlPassword = (LinearLayout) findViewById(R.id.ll_password);
        mEtPassword = (EditText) findViewById(R.id.et_password);
        mBtLogin = (Button) findViewById(R.id.bt_login);
    }

    private void initEvents() {
        mBtLogin.setOnClickListener(listener);
    }

    private void initDatas() {

    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_login:
                    login();
                    break;
                default:
                    return;
            }
        }
    };

    /**
     * 登录
     */
    private void login() {
        //非空验证
        final String userName = mEtUsername.getText().toString().trim();
        final String passWord = mEtPassword.getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
            mEtUsername.setError("用户名不能为空!");
            return;
        }
        if (TextUtils.isEmpty(passWord)) {
            mEtPassword.setError("密码不能为空!");
            return;
        }
        //登录处理
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                try {
                    //创建连接配置对象
                    ConnectionConfiguration config = new ConnectionConfiguration(HOST, PORT);
                    //额外的配置
                    config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);//明文传输，方便调试
                    config.setDebuggerEnabled(true);//开启调试模式，方便查看发送内容
                    //开始创建连接对象
                    XMPPConnection xmppConnection = new XMPPConnection(config);
                    //开始连接
                    xmppConnection.connect();
                    //连接成功，开始登陆
                    xmppConnection.login(userName, passWord);
                    //登陆成功
                    ToastUtils.showToastSafe(LoginActivity.this, "登陆成功");
                    //到主界面
                    loadMain();
                    //保存连接对象
                    IMService.mConn=xmppConnection;
                    System.out.println("IMService.conn"+xmppConnection.toString());
                    //保存当前登录账户
                    IMService.mCurAccount=userName+"@"+LoginActivity.SERVICENAME;
                    //启动service
                    Intent service =new Intent(getApplicationContext(),IMService.class);
                    startService(service);
                } catch (XMPPException e) {
                    e.printStackTrace();
                    ToastUtils.showToastSafe(LoginActivity.this, "登陆失败");
                }
            }
        });
    }

    private void loadMain() {
        if (isLoadMain){
            Intent main=new Intent(LoginActivity.this,MainActivity.class);
            startActivity(main);
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        isLoadMain=false;
        super.onDestroy();
    }
}