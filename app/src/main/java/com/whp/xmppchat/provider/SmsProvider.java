package com.whp.xmppchat.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.whp.xmppchat.dbHelper.SmsOpenHelper;

/**
 * Created by Administrator on 2016/11/20.
 */

public class SmsProvider extends ContentProvider {

    private static final String AUTHORITIES = SmsProvider.class.getCanonicalName();
    static UriMatcher mUriMatcher;
    private static final int SMS = 1;
    public static final Uri URI_SMS = Uri.parse("content://" + AUTHORITIES + "/sms");

    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        //AUTHORITIES路径     "/sms"表     SMS  code
        mUriMatcher.addURI(AUTHORITIES, "/sms", SMS);
    }

    private SmsOpenHelper mHelper;

    @Override
    public boolean onCreate() {
        mHelper = new SmsOpenHelper(getContext());
        if (mHelper != null) {
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int code = mUriMatcher.match(uri);
        Cursor cursor=null;
        switch (code) {
            case SMS:
                cursor = mHelper.getReadableDatabase().query(SmsOpenHelper.T_SMS, projection, selection,
                        selectionArgs, null, null, sortOrder);
                System.out.println("query   ok");
                break;
            default:
                break;
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        System.out.println(uri + "---uri");
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int code = mUriMatcher.match(uri);
        switch (code) {
            case SMS:
                long id = mHelper.getWritableDatabase().insert(SmsOpenHelper.T_SMS, "", values);
                if (id != 0) {
                    System.out.println("insert   ok");
                    uri = ContentUris.withAppendedId(uri, id);
                    getContext().getContentResolver().notifyChange(SmsProvider.URI_SMS,null);
                }
                break;
            default:
                break;
        }
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int code = mUriMatcher.match(uri);
        int deleteCount = 0;
        switch (code) {
            case SMS:
                deleteCount = mHelper.getWritableDatabase().delete(SmsOpenHelper.T_SMS, selection, selectionArgs);
                if (deleteCount > 0) {
                    System.out.println("delete  ok");
                    getContext().getContentResolver().notifyChange(SmsProvider.URI_SMS,null);
                }
                break;
            default:
                break;
        }
        return deleteCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int code = mUriMatcher.match(uri);
        int updatecount = 0;
        switch (code) {
            case SMS:
                updatecount = mHelper.getWritableDatabase().update(SmsOpenHelper.T_SMS, values, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(SmsProvider.URI_SMS,null);
                break;
            default:
                break;
        }
        return updatecount;
    }
}
