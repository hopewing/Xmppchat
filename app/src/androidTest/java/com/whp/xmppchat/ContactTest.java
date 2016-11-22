package com.whp.xmppchat;

import android.content.ContentValues;
import android.test.AndroidTestCase;

import com.whp.xmppchat.dbHelper.ContactsOpenHelper;
import com.whp.xmppchat.provider.ContactsProvider;

/**
 * Created by Administrator on 2016/11/19.
 */

public class ContactTest extends AndroidTestCase {

    public void testInsert() throws Exception {
        // Context of the app under test.
        ContentValues values = new ContentValues();
        values.put(ContactsOpenHelper.ContactTable.ACCCOUNT, "whp");
        values.put(ContactsOpenHelper.ContactTable.NICKNAME, "hopewing");
        values.put(ContactsOpenHelper.ContactTable.AVATAR, "0");
        values.put(ContactsOpenHelper.ContactTable.PINYIN, "hhh");
        getContext().getContentResolver().insert(ContactsProvider.URI_CONTACT, values);
    }

    public void testDel() throws Exception {
        // Context of the app under test.
        getContext().getContentResolver().delete(ContactsProvider.URI_CONTACT, ContactsOpenHelper.ContactTable.ACCCOUNT + "=?", new String[]{"whp"});
    }

    public void testUp() throws Exception {
        // Context of the app under test.

    }

    public void testQuery() throws Exception {
        // Context of the app under test.

    }

}
