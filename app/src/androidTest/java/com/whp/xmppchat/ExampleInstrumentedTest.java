package com.whp.xmppchat;

import android.content.ContentValues;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.whp.xmppchat.dbHelper.ContactsOpenHelper;
import com.whp.xmppchat.provider.ContactsProvider;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.whp.xmppchat", appContext.getPackageName());
    }

    @Test
    public void contactTestInsert() throws Exception {
        // Context of the app under test.
        ContactsProvider p=new ContactsProvider();
        ContentValues values = new ContentValues();
        values.put(ContactsOpenHelper.ContactTable.ACCCOUNT, "whp");
        values.put(ContactsOpenHelper.ContactTable.NICKNAME, "hopewing");
        values.put(ContactsOpenHelper.ContactTable.AVATAR, "0");
        values.put(ContactsOpenHelper.ContactTable.PINYIN, "hhh");
        p.insert(ContactsProvider.URI_CONTACT, values);
    }

    @Test
    public void contactTestDel() throws Exception {
        // Context of the app under test.

    }

    @Test
    public void contactTestUp() throws Exception {
        // Context of the app under test.

    }

    @Test
    public void contactTestQuery() throws Exception {
        // Context of the app under test.

    }

}
