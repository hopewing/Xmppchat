package com.whp.xmppchat.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.whp.xmppchat.dbHelper.ContactsOpenHelper;

/**
 * Created by Administrator on 2016/11/19.
 */

public class ContactsProvider extends ContentProvider {
    //主机地址常量
    public static final String AUTHORITIES = ContactsProvider.class.getCanonicalName();//类的完整路径
    //地址匹配对象
    static UriMatcher mUriMatcher;
    private static final int CONTACT = 1;
    //联系人表的URI常量
    public static Uri URI_CONTACT = Uri.parse("content://"+AUTHORITIES + "/contact");

    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        //添加一个匹配规则
        mUriMatcher.addURI(AUTHORITIES, "/contact", CONTACT);
        // content://com.whp.xmppchat.provider.ContactsProvider/contact--->CONTACT
    }

    private ContactsOpenHelper mHelper;

    @Override
    public boolean onCreate() {
        mHelper = new ContactsOpenHelper(getContext());
        if (mHelper != null) {
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        int code = mUriMatcher.match(uri);
        Cursor cursor =null;
        switch (code) {
            case CONTACT:
                SQLiteDatabase db = mHelper.getWritableDatabase();
                cursor = db.query(ContactsOpenHelper.T_CONTACT, strings, s, strings1, null, null, s1);
                break;
            default:
                break;
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int code = mUriMatcher.match(uri);
        switch (code) {
            case CONTACT:
                SQLiteDatabase db = mHelper.getWritableDatabase();
                long id = db.insert(ContactsOpenHelper.T_CONTACT, "", contentValues);
                if (id != -1) {
                    System.out.println("insertOK");
                    uri = ContentUris.withAppendedId(uri, id);
                    //通知observer改变了
                    getContext().getContentResolver().notifyChange(ContactsProvider.URI_CONTACT,null);
                }
                break;
            default:
                break;
        }
        return uri;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        int code = mUriMatcher.match(uri);
        int deleteCounts = 0;
        switch (code) {
            case CONTACT:
                SQLiteDatabase db = mHelper.getWritableDatabase();
                deleteCounts = db.delete(ContactsOpenHelper.T_CONTACT, s, strings);
                if (deleteCounts != 0) {
                    System.out.println("deleteOk");
                    //通知observer改变了
                    getContext().getContentResolver().notifyChange(ContactsProvider.URI_CONTACT,null);
                }
                break;
            default:
                break;
        }
        return deleteCounts;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] strings) {
        int code = mUriMatcher.match(uri);
        int updateCounts = 0;
        switch (code) {
            case CONTACT:
                SQLiteDatabase db = mHelper.getWritableDatabase();
                updateCounts = db.update(ContactsOpenHelper.T_CONTACT, contentValues, selection, strings);
                if (updateCounts != 0) {
                    System.out.println("updateOk");
                    //通知observer改变了
                    getContext().getContentResolver().notifyChange(ContactsProvider.URI_CONTACT,null);
                }
                break;
            default:
                break;
        }
        return updateCounts;
    }

}
