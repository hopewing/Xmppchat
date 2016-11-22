package com.whp.xmppchat.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.whp.xmppchat.R;
import com.whp.xmppchat.dbHelper.SmsOpenHelper;
import com.whp.xmppchat.provider.SmsProvider;
import com.whp.xmppchat.service.IMService;
import com.whp.xmppchat.utils.ThreadUtils;

import org.jivesoftware.smack.packet.Message;

import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    public static final String CHATACCOUNT = "account";
    public static final String CHATNICKNAME = "nickname";
    private static final int SEND = 0;
    private static final int RECEIVE = 1;
    private String mAccount;
    private String mNickname;
    private TextView mTvTitle;
    private ListView mLvChat;
    private EditText mEtText;
    private Button mBtSend;
    private CursorAdapter mCursorAdapter;
    IMService mIMService;

    private void assignViews() {
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mLvChat = (ListView) findViewById(R.id.lv_chat);
        mEtText = (EditText) findViewById(R.id.et_text);
        mBtSend = (Button) findViewById(R.id.bt_send);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();
        initView();
        initData();
        initListener();
    }

    private void init() {
        registerContentObserver();
        // 绑定服务
        Intent service = new Intent(ChatActivity.this, IMService.class);
        bindService(service, mMyServiceConnection, BIND_AUTO_CREATE);
        mAccount = getIntent().getStringExtra(ChatActivity.CHATACCOUNT);
        mNickname = getIntent().getStringExtra(ChatActivity.CHATNICKNAME);
        assignViews();
    }

    private void initView() {
        mTvTitle.setText(mNickname);
    }

    private void initData() {
        setAdapterOrNotify();
    }

    private void setAdapterOrNotify() {
        if (mCursorAdapter != null) {
            mCursorAdapter.getCursor().requery();
            mLvChat.setSelection(mCursorAdapter.getCount() - 1);
            return;
        }
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                final Cursor c = getContentResolver().query(SmsProvider.URI_SMS, null,
                        "(from_account = ? and to_account = ? )or(from_account = ? and to_account= ? )",
                        new String[] { IMService.mCurAccount, mAccount, mAccount, IMService.mCurAccount },
                        SmsOpenHelper.SmsTable.TIME + " ASC");
                if (c.getCount() < 1) {
                    return;
                }
                ThreadUtils.runInUIThread(new Runnable() {
                    @Override
                    public void run() {
                        mCursorAdapter = new CursorAdapter(ChatActivity.this, c) {

                            /*@Override
                            public View newView(Context context, Cursor cursor, ViewGroup parent) {

                                return new TextView(context);
                            }

                            @Override
                            public void bindView(View view, Context context, Cursor cursor) {
                                ((TextView)view).setText(cursor.getString(c.getColumnIndex(SmsOpenHelper.SmsTable.BODY)));
                            }*/

                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                ViewHolder holder = null;
                                if (getItemViewType(position) == SEND) {
                                    if (convertView == null) {
                                        System.out.println("第一次view" + position);
                                        convertView = View.inflate(getApplicationContext(), R.layout.item_chat_send, null);
                                        holder = new ViewHolder();
                                        convertView.setTag(holder);
                                        //holder赋值
                                        holder.time = (TextView) convertView.findViewById(R.id.time);
                                        holder.body = (TextView) convertView.findViewById(R.id.content);
                                        holder.head = (ImageView) convertView.findViewById(R.id.head);
                                    } else {
                                        holder = (ViewHolder) convertView.getTag();
                                    }
                                    //得到数据，展示数据
                                } else if (getItemViewType(position) == RECEIVE) {
                                    if (convertView == null) {
                                        convertView = View.inflate(getApplicationContext(), R.layout.item_chat_receive, null);
                                        holder = new ViewHolder();
                                        convertView.setTag(holder);
                                        //holder赋值
                                        holder.time = (TextView) convertView.findViewById(R.id.time);
                                        holder.body = (TextView) convertView.findViewById(R.id.content);
                                        holder.head = (ImageView) convertView.findViewById(R.id.head);
                                    } else {
                                        holder = (ViewHolder) convertView.getTag();
                                    }
                                }
                                //得到数据，展示数据
                                c.moveToPosition(position);
                                String time = c.getString(c.getColumnIndex(SmsOpenHelper.SmsTable.TIME));
                                String body = c.getString(c.getColumnIndex(SmsOpenHelper.SmsTable.BODY));
                                System.out.println("adapter     "+body);
                                String formatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.parseLong(time)));
                                holder.time.setText(formatTime);
                                holder.body.setText(body);
                                return super.getView(position, convertView, parent);
                            }

                            class ViewHolder {
                                TextView body;
                                TextView time;
                                ImageView head;
                            }

                            /**
                             * 当前类型的控件的位置
                             */
                            @Override
                            public int getItemViewType(int position) {
                                c.moveToPosition(position);
                                String fromAccount = c.getString(c.getColumnIndex(SmsOpenHelper.SmsTable.FROM_ACCOUNT));
                                if (IMService.mCurAccount.equals(fromAccount)) {
                                    return SEND;
                                } else {
                                    return RECEIVE;
                                }
                            }

                            /**
                             * listView有几种类型控件
                             */
                            @Override
                            public int getViewTypeCount() {
                                return super.getViewTypeCount() + 1;
                            }

                            @Override
                            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                                return null;
                            }

                            @Override
                            public void bindView(View view, Context context, Cursor cursor) {

                            }
                        };
                        mLvChat.setAdapter(mCursorAdapter);
                        mLvChat.setSelection(mCursorAdapter.getCount() - 1);
                    }
                });
            }
        });
    }

    private void initListener() {
        //点击发送
        mBtSend.setOnClickListener(listener);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_send:
                    System.out.println("send");
                    Message msg = new Message();
                    msg.setFrom(IMService.mCurAccount);
                    msg.setTo(mAccount);
                    msg.setBody(mEtText.getText().toString());
                    msg.setType(Message.Type.chat);
                    //msg.setProperty("","");
                    //发送消息
                    System.out.println("mIMService---------" + mIMService.toString());
                    mIMService.sendMessage(msg);
                    //IMService service = new IMService();
                    mEtText.setText("");
                    //mIMService.saveMessage(mAccount, msg);//保存消息
                    break;
                default:
                    break;
            }
        }
    };

    MyContentObserver mMyContentObserver = new MyContentObserver(new Handler());

    public void registerContentObserver() {
        getContentResolver().registerContentObserver(SmsProvider.URI_SMS, true, mMyContentObserver);
    }

    public void unRegisterContentObserver() {
        getContentResolver().unregisterContentObserver(mMyContentObserver);
    }

    class MyContentObserver extends ContentObserver {

        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            System.out.println("onChange");
            //更新
            setAdapterOrNotify();
            super.onChange(selfChange, uri);
        }
    }

    @Override
    protected void onDestroy() {
        unRegisterContentObserver();
        //解绑服务
        if (mMyServiceConnection == null) {
            unbindService(mMyServiceConnection);
        }
        super.onDestroy();
    }

    /*=============== 定义ServiceConnection调用服务里面的方法 ===============*/

    MyServiceConnection mMyServiceConnection = new MyServiceConnection();

    class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            System.out.println("--------------onServiceConnected--------------");
            //public class Binder implements IBinder
            //public class MyBinder extends Binder----implements IBinder
            IMService.MyBinder binder = (IMService.MyBinder) service;
            mIMService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            System.out.println("--------------onServiceDisconnected--------------");

        }
    }

}
