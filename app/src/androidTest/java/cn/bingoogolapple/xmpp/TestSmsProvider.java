package cn.bingoogolapple.xmpp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import cn.bingoogolapple.xmpp.dao.SmsOpenHelper;
import cn.bingoogolapple.xmpp.provider.SmsProvider;
import cn.bingoogolapple.xmpp.util.Logger;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/3 下午5:58
 * 描述:
 */
public class TestSmsProvider extends AndroidTestCase {
    private static final String TAG = TestSmsProvider.class.getSimpleName();

    public void testInsertSms() {
        ContentValues values = new ContentValues();
        values.put(SmsOpenHelper.SmsTable.FROM_ACCOUNT, "bga2@bingoogolapple.cn");
        values.put(SmsOpenHelper.SmsTable.TO_ACCOUNT, "bga5@bingoogolapple.cn");
        values.put(SmsOpenHelper.SmsTable.BODY, "这是修改前的消息");
        values.put(SmsOpenHelper.SmsTable.STATUS, "offline");
        values.put(SmsOpenHelper.SmsTable.TYPE, "chat");
        values.put(SmsOpenHelper.SmsTable.TIME, System.currentTimeMillis());
        values.put(SmsOpenHelper.SmsTable.SESSION_ACCOUNT, "bga5@bingoogolapple.cn");
        Uri uri = getContext().getContentResolver().insert(SmsProvider.URI_SMS, values);
        long newlyId = ContentUris.parseId(uri);
        if (newlyId > 0) {
            Logger.i(TAG, "添加成功" + newlyId);
        } else {
            Logger.i(TAG, "添加失败");
        }
    }

    public void testDeleteSms() {
        int deletedCount = getContext().getContentResolver().delete(SmsProvider.URI_SMS, SmsOpenHelper.SmsTable.FROM_ACCOUNT + "=?", new String[]{"bga3@bingoogolapple.cn"});
        Logger.i(TAG, "删除" + deletedCount);
    }

    public void testUpdateSms() {
        ContentValues values = new ContentValues();
        values.put(SmsOpenHelper.SmsTable.FROM_ACCOUNT, "bga3@bingoogolapple.cn");
        values.put(SmsOpenHelper.SmsTable.TO_ACCOUNT, "bga4@bingoogolapple.cn");
        values.put(SmsOpenHelper.SmsTable.BODY, "这是修改后的消息");
        values.put(SmsOpenHelper.SmsTable.STATUS, "online");
        values.put(SmsOpenHelper.SmsTable.TYPE, "chat");
        values.put(SmsOpenHelper.SmsTable.TIME, System.currentTimeMillis());
        values.put(SmsOpenHelper.SmsTable.SESSION_ACCOUNT, "bga5@bingoogolapple.cn");
        int updatedCount = getContext().getContentResolver().update(SmsProvider.URI_SMS, values, SmsOpenHelper.SmsTable.FROM_ACCOUNT + "=?", new String[]{"bga2@bingoogolapple.cn"});
        if (updatedCount > 0) {
            Logger.i(TAG, "修改成功" + updatedCount);
        } else {
            Logger.i(TAG, "修改失败");
        }
    }

    public void testQuerySms() {
        Cursor cursor = getContext().getContentResolver().query(SmsProvider.URI_SMS, null, null, null, null);
        if (cursor != null) {
            int columnCount = cursor.getColumnCount();
            while (cursor.moveToNext()) {
                for (int i = 0; i < columnCount; i++) {
                    Logger.i(TAG, cursor.getColumnName(i) + " = " + cursor.getString(i) + "  ");
                }
            }
        } else {
            Logger.i(TAG, "没有消息");
        }
    }

}