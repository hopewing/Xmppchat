package com.whp.xmppchat.fragment;


import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.whp.xmppchat.R;
import com.whp.xmppchat.activity.ChatActivity;
import com.whp.xmppchat.dbHelper.ContactsOpenHelper;
import com.whp.xmppchat.provider.ContactsProvider;
import com.whp.xmppchat.utils.ThreadUtils;

/**
 * 联系人Fragment
 */
public class ContactsFragment extends Fragment {
    private ListView mListView;
    private CursorAdapter mAdaper;

    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        init();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        initData();
        initListener();
        super.onActivityCreated(savedInstanceState);
    }

    private void init() {
        registerContentObserver();
    }

    @Override
    public void onDestroy() {
        unRegisterContentObserver();
        super.onDestroy();
    }

    private void initData() {
        setOrUpdateAdapter();
    }

    private void setOrUpdateAdapter() {
        //判断adapter是否存在
        if (mAdaper != null) {
            mAdaper.getCursor().requery();
            return;
        }
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                //设置adapter,显示数据
                final Cursor c = getActivity().getContentResolver().query(
                        ContactsProvider.URI_CONTACT, null, null, null, null);
                if (c.getCount() <= 0) {
                    return;
                }
                ThreadUtils.runInUIThread(new Runnable() {
                    @Override
                    public void run() {
                        //数据从数据库中来用CursorAdapter
                        mAdaper = new CursorAdapter(getActivity(), c) {
                            @Override
                            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                                View view = View.inflate(getActivity(), R.layout.item_contact, null);
                                return view;
                            }

                            @Override
                            public void bindView(View view, Context context, Cursor cursor) {
                                ImageView ivHead = (ImageView) view.findViewById(R.id.head);
                                TextView tvNickname = (TextView) view.findViewById(R.id.nickname);
                                TextView tvAccount = (TextView) view.findViewById(R.id.account);
                                String nickname = cursor.getString(c.getColumnIndex(ContactsOpenHelper.ContactTable.NICKNAME));
                                String account = cursor.getString(c.getColumnIndex(ContactsOpenHelper.ContactTable.ACCOUNT));
                                tvNickname.setText(nickname);
                                tvAccount.setText(account);
                            }
                        };
                        mListView.setAdapter(mAdaper);
                    }
                });
            }
        });
    }

    private void initView(View view) {
        mListView = (ListView) view.findViewById(R.id.listview);
    }

    private void initListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = mAdaper.getCursor();
                c.moveToPosition(position);
                //账号和昵称
                String account = c.getString(c.getColumnIndex(ContactsOpenHelper.ContactTable.ACCOUNT));
                String nickname = c.getString(c.getColumnIndex(ContactsOpenHelper.ContactTable.NICKNAME));
                Intent chat=new Intent(getActivity(),ChatActivity.class);
                chat.putExtra(ChatActivity.CHATACCOUNT,account);
                chat.putExtra(ChatActivity.CHATNICKNAME,nickname);
                startActivity(chat);
            }
        });
    }


    MyContentObserver mObserver = new MyContentObserver(new Handler());

    //注册监听
    public void registerContentObserver() {
        getActivity().getContentResolver().registerContentObserver(ContactsProvider.URI_CONTACT, true, mObserver);
    }

    //反注册监听
    public void unRegisterContentObserver() {
        getActivity().getContentResolver().unregisterContentObserver(mObserver);
    }

    /**
     * 监听数据库数据改变
     */
    class MyContentObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler) {
            super(handler);
        }

        /**
         * 数据库数据发生改变就调用该方法
         */
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            setOrUpdateAdapter();
            super.onChange(selfChange, uri);
        }
    }

}
