package com.whp.xmppchat.dbHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Administrator on 2016/11/19.
 */

public class ContactsOpenHelper extends SQLiteOpenHelper {
    public static final String T_CONTACT = "t_contact";//表名

    public class ContactTable implements BaseColumns {//默认添加列_ID
        public static final String ACCOUNT = "account";
        public static final String NICKNAME = "nickname";
        public static final String AVATAR = "avatar";
        public static final String PINYIN = "pinyin";
    }

    public ContactsOpenHelper(Context context) {
        super(context, "contact.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE " + T_CONTACT + "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                ContactTable.ACCOUNT + " TEXT, " +
                ContactTable.NICKNAME + " TEXT, " +
                ContactTable.AVATAR + " TEXT, " +
                ContactTable.PINYIN + " TEXT);";
        String hql = "CREATE TABLE " + T_CONTACT + "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                ContactTable.ACCOUNT + " TEXT, " +
                ContactTable.NICKNAME + " TEXT ," +
                ContactTable.AVATAR + " TEXT ," +
                ContactTable.PINYIN + " TEXT)";
        sqLiteDatabase.execSQL(hql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
