package com.whp.xmppchat.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.whp.xmppchat.activity.LoginActivity;
import com.whp.xmppchat.dbHelper.ContactsOpenHelper;
import com.whp.xmppchat.dbHelper.SmsOpenHelper;
import com.whp.xmppchat.provider.ContactsProvider;
import com.whp.xmppchat.provider.SmsProvider;
import com.whp.xmppchat.utils.ThreadUtils;
import com.whp.xmppchat.utils.ToastUtils;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import opensource.jpinyin.PinyinFormat;
import opensource.jpinyin.PinyinHelper;

/**
 * Created by Administrator on 2016/11/19.
 */

public class IMService extends Service {
    public static XMPPConnection mConn;//保存连接对象
    public static String mCurAccount;//当前登录用户的jid
    private Roster mRoster;
    private MyRosterListener mRosterListener;
    private ChatManager mChatManager;
    private MyChatManagerListener mMyChatManagerListener;
    private Map<String, Chat> mChatMap = new HashMap<>();
    private MyMessageListener mMessageListener;
    private Chat mChat;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public class MyBinder extends Binder {
        /**
         * 返回service的实例,new IMService()不行
         */
        public IMService getService() {
            return IMService.this;
        }
    }

    @Override
    public void onCreate() {
        System.out.println("IMservice   onCreate");
        //同步花名册，设置监听
        updateRosterAddListener();
        createChatManager();
        super.onCreate();
    }

    private void createChatManager() {
        if (IMService.mConn == null) {
            System.out.println("imservice_conn     =      null");
        }
        // 1.获取消息的管理者
        if (mChatManager == null) {

            mChatManager = IMService.mConn.getChatManager();
        }
        if (mMyChatManagerListener == null) {
            mMyChatManagerListener = new MyChatManagerListener();
        }
        mChatManager.addChatListener(mMyChatManagerListener);
    }

    private void updateRosterAddListener() {
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                //得到联系人,需要连接对象
                //花名册
                mRoster = IMService.mConn.getRoster();
                //所有联系人
                final Collection<RosterEntry> entries = mRoster.getEntries();
                //监听联系人改变
                mRosterListener = new MyRosterListener();
                mRoster.addRosterListener(mRosterListener);
                //同步花名册
                for (RosterEntry entry : entries) {
                    saveOrUpdateEntry(entry);
                }
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * RosterListener发现花名册改变去操作数据库-->数据库内容改变----->触发内容观察者-------->触发adapter
     */
    class MyRosterListener implements RosterListener {

        @Override
        public void entriesAdded(Collection<String> addresses) {
            for (String addr : addresses) {
                RosterEntry entry = mRoster.getEntry(addr);
                //保存或更新
                saveOrUpdateEntry(entry);
            }
        }

        @Override
        public void entriesUpdated(Collection<String> addresses) {
            for (String addr : addresses) {
                RosterEntry entry = mRoster.getEntry(addr);
                //保存或更新
                saveOrUpdateEntry(entry);
            }
        }

        @Override
        public void entriesDeleted(Collection<String> addresses) {
            for (String account : addresses) {
                System.out.print(account);
                getContentResolver().delete(ContactsProvider.URI_CONTACT,
                        ContactsOpenHelper.ContactTable.ACCOUNT + "=?", new String[]{account});
                System.out.println("MyRosterListener---delete");
            }
            System.out.println("getContentResolver         entriesDeleted     ");
            /*for (String account : addresses) {
                // 执行删除操作
                getActivity().getContentResolver().delete(ContactsProvider.URI_CONTACT,
                        ContactsOpenHelper.ContactTable.ACCOUNT + "=?", new String[]{account});
            }*/
        }

        @Override
        public void presenceChanged(Presence presence) {
            System.out.println("change");
        }
    }

    class MyChatManagerListener implements ChatManagerListener {

        //createdLocally自己创建的chat
        @Override
        public void chatCreated(Chat chat, boolean createdLocally) {
            System.out.println("--------------chatCreated--------------");

            // 判断chat是否存在map里面
            String participant = chat.getParticipant();// 和我聊天的那个人

            // 因为别人创建和我自己创建,参与者(和我聊天的人)对应的jid不同.所以需要统一处理
            participant = filterAccount(participant);

            if (!mChatMap.containsKey(participant)) {
                // 保存chat
                mChatMap.put(participant, chat);
                if (mMessageListener == null) {
                    mMessageListener = new MyMessageListener();
                }
                chat.addMessageListener(mMessageListener);
            }
            System.out.println("participant:" + participant);

            if (createdLocally) {// true
                System.out.println("-------------- 我创建了一个chat--------------");
                // participant:hm1@itheima.com admin@itheima.com hm1@itheima.com
            } else {// false
                System.out.println("-------------- 别人创建了一个chat--------------");
                // participant:hm1@itheima.com/Spark 2.6.3 admin@itheima.com hm1@itheima.com
            }
        }
    }

    class MyMessageListener implements MessageListener {
        //收到消息
        @Override
        public void processMessage(Chat chat, Message message) {
            ToastUtils.showToastSafe(getApplicationContext(), message.getBody());
            //保存消息
            saveMessage(chat.getParticipant(), message);//chat.getParticipant()参与者
        }
    }

    public void saveMessage(String sessionAccount, Message msg) {
        final ContentValues values = new ContentValues();
        values.put(SmsOpenHelper.SmsTable.FROM_ACCOUNT, msg.getFrom());
        values.put(SmsOpenHelper.SmsTable.TO_ACCOUNT, msg.getTo());
        values.put(SmsOpenHelper.SmsTable.BODY, msg.getBody());
        values.put(SmsOpenHelper.SmsTable.STATE, "offline");
        values.put(SmsOpenHelper.SmsTable.TYPE, msg.getType().name());
        values.put(SmsOpenHelper.SmsTable.TIME, System.currentTimeMillis());
        values.put(SmsOpenHelper.SmsTable.SESSION_ACCOUNT, sessionAccount);
        //System.out.println("getContentResolver========"+getContentResolver().toString());
        System.out.println("SmsProvider.URI_SMS========" + SmsProvider.URI_SMS);
        System.out.println("values========" + values);
        ToastUtils.showToastSafe(getApplicationContext(),msg.getBody());
        getContentResolver().insert(SmsProvider.URI_SMS, values);
    }

    public void sendMessage(final Message msg) {
        createChatManager();
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mMessageListener == null) {
                        mMessageListener = new MyMessageListener();
                    }
                    //创建聊天对象,发送给mAccount
                    String toAccount = msg.getTo();

                    if (mChatMap.containsKey(toAccount)) {
                        mChat = mChatMap.get(toAccount);
                    } else {
                        mChat = mChatManager.createChat(toAccount, mMessageListener);
                        mChatMap.put(toAccount, mChat);
                    }
                    //发送消息
                    mChat.sendMessage(msg);
                    saveMessage(msg.getTo(), msg);
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void saveOrUpdateEntry(RosterEntry entry) {
        System.out.println("-----------saveOrUpdateEntry-------------");
        String nickName = entry.getName();
        if (nickName == null || "".equals(nickName)) {
            nickName = entry.getUser().substring(0, entry.getUser().indexOf("@"));
        }
        String account = entry.getUser().substring(0, entry.getUser().indexOf("@")) + "@" + LoginActivity.SERVICENAME;
        ContentValues values = new ContentValues();
        values.put(ContactsOpenHelper.ContactTable.ACCOUNT, account);
        values.put(ContactsOpenHelper.ContactTable.NICKNAME, nickName);
        values.put(ContactsOpenHelper.ContactTable.AVATAR, "0");
        values.put(ContactsOpenHelper.ContactTable.PINYIN, PinyinHelper.convertToPinyinString(
                entry.getName(), "", PinyinFormat.WITHOUT_TONE));
        //先update不行再插入
        int updateCount = getContentResolver().update(ContactsProvider.URI_CONTACT, values,
                ContactsOpenHelper.ContactTable.ACCOUNT + " =? ", new String[]{account});
        if (updateCount <= 0) {
            getContentResolver().insert(ContactsProvider.URI_CONTACT, values);
            System.out.println("insert         saveOrUpdateEntry     ");
        }
    }

    /**
     * @param accout
     * @return 账户名
     */
    private String filterAccount(String accout) {
        return accout.substring(0, accout.indexOf("@")) + "@" + LoginActivity.SERVICENAME;
    }
}
