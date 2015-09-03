package cn.bingoogolapple.xmpp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import cn.bingoogolapple.xmpp.dao.ContactOpenHelper;
import cn.bingoogolapple.xmpp.provider.ContactsProvider;
import cn.bingoogolapple.xmpp.util.Logger;
import cn.bingoogolapple.xmpp.util.PinyinUtil;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/3 下午5:58
 * 描述:
 */
public class TestContactsProvider extends AndroidTestCase {
    private static final String TAG = TestContactsProvider.class.getSimpleName();

    public void testInsert() {
        ContentValues values = new ContentValues();
        values.put(ContactOpenHelper.ContactTable.ACCOUNT, "zhangsan@bga.cn");
        values.put(ContactOpenHelper.ContactTable.NICKNAME, "张三");
        values.put(ContactOpenHelper.ContactTable.AVATAR, "zhangsanavatar");
        values.put(ContactOpenHelper.ContactTable.PINYIN, "zhangsan");
        Uri uri = getContext().getContentResolver().insert(ContactsProvider.URI_CONTACT, values);
        long newlyId = ContentUris.parseId(uri);
        if (newlyId > 0) {
            Logger.i(TAG, "添加成功" + newlyId);
        } else {
            Logger.i(TAG, "添加失败");
        }
    }

    public void testDelete() {
        getContext().getContentResolver().delete(ContactsProvider.URI_CONTACT, ContactOpenHelper.ContactTable.ACCOUNT + "=?", new String[]{"lisi@bga.cn"});
    }

    public void testUpdate() {
        ContentValues values = new ContentValues();
        values.put(ContactOpenHelper.ContactTable.ACCOUNT, "lisi@bga.cn");
        values.put(ContactOpenHelper.ContactTable.NICKNAME, "李四");
        values.put(ContactOpenHelper.ContactTable.AVATAR, "lisiavatar");
        values.put(ContactOpenHelper.ContactTable.PINYIN, "lisi");
        int updatedCount = getContext().getContentResolver().update(ContactsProvider.URI_CONTACT, values, ContactOpenHelper.ContactTable.ACCOUNT + "=?", new String[]{"zhangsan@bga.cn"});
        if (updatedCount > 0) {
            Logger.i(TAG, "修改成功" + updatedCount);
        } else {
            Logger.i(TAG, "修改失败");
        }
    }

    public void testQuery() {
        Cursor cursor = getContext().getContentResolver().query(ContactsProvider.URI_CONTACT, null, null, null, null);
        if (cursor != null) {
            int columnCount = cursor.getColumnCount();
            while (cursor.moveToNext()) {
                for (int i = 0; i < columnCount; i++) {
                    Logger.i(TAG, cursor.getColumnName(i) + " = " + cursor.getString(i) + "  ");
                }
            }
        }
    }

    public void testPinYin() {
        Logger.i(TAG, "王浩 的拼音是 " + PinyinUtil.getPinyin("王浩"));
    }

}