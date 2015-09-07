package cn.bingoogolapple.xmpp.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import cn.bingoogolapple.xmpp.dao.SmsOpenHelper;
import cn.bingoogolapple.xmpp.util.Logger;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/5 下午4:50
 * 描述:
 */
public class SmsProvider extends ContentProvider {
    private static final String TAG = SmsProvider.class.getSimpleName();
    public static final String AUTHORITIES = SmsProvider.class.getCanonicalName();
    public static final int MATCHED_CODE_SMS = 1;
    public static final int MATCHED_CODE_SESSION = 2;
    public static final Uri URI_SMS = Uri.parse("content://" + AUTHORITIES + "/sms");
    public static final Uri URI_SESSION = Uri.parse("content://" + AUTHORITIES + "/session");

    private static UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        sUriMatcher.addURI(AUTHORITIES, "/sms", MATCHED_CODE_SMS);
        sUriMatcher.addURI(AUTHORITIES, "/session", MATCHED_CODE_SESSION);
    }

    private SmsOpenHelper mSmsOpenHelper;

    @Override
    public boolean onCreate() {
        mSmsOpenHelper = new SmsOpenHelper(getContext());
        if (mSmsOpenHelper != null) {
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int code = sUriMatcher.match(uri);
        switch (code) {
            case MATCHED_CODE_SMS:
                SQLiteDatabase db = mSmsOpenHelper.getWritableDatabase();
                long newlyId = db.insert(SmsOpenHelper.T_SMS, "", values);
                if (newlyId != -1) {
                    Logger.i(TAG, "添加消息成功");
                    // 拼接最新的Uri
                    uri = ContentUris.withAppendedId(uri, newlyId);

                    // 通知内容观察者数据有改变，null表示所有的内容观察者都能收到
                    getContext().getContentResolver().notifyChange(URI_SMS, null);
                }
                break;
            default:
                break;
        }
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int affectedCount = 0;
        int matchedCode = sUriMatcher.match(uri);
        switch (matchedCode) {
            case MATCHED_CODE_SMS:
                SQLiteDatabase db = mSmsOpenHelper.getWritableDatabase();
                affectedCount = db.delete(SmsOpenHelper.T_SMS, selection, selectionArgs);
                if (affectedCount > 0) {
                    Logger.i(TAG, "删除消息成功");

                    getContext().getContentResolver().notifyChange(URI_SMS, null);
                }
                break;
            default:
                break;
        }
        return affectedCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int affectedCount = 0;
        int code = sUriMatcher.match(uri);
        switch (code) {
            case MATCHED_CODE_SMS:
                SQLiteDatabase db = mSmsOpenHelper.getWritableDatabase();
                affectedCount = db.update(SmsOpenHelper.T_SMS, values, selection, selectionArgs);
                if (affectedCount > 0) {
                    Logger.i(TAG, "修改消息成功");
                    getContext().getContentResolver().notifyChange(URI_SMS, null);
                }
                break;
            default:
                break;
        }
        return affectedCount;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        int code = sUriMatcher.match(uri);
        switch (code) {
            case MATCHED_CODE_SMS:
                cursor = mSmsOpenHelper.getWritableDatabase().query(SmsOpenHelper.T_SMS, projection, selection, selectionArgs, null, null, sortOrder);
                Logger.i(TAG, "查询消息成功");
                break;
            case MATCHED_CODE_SESSION:
                cursor = mSmsOpenHelper.getWritableDatabase().rawQuery("SELECT * FROM (SELECT * FROM " + SmsOpenHelper.T_SMS + " WHERE " + SmsOpenHelper.SmsTable.FROM_ACCOUNT + "=? OR " + SmsOpenHelper.SmsTable.TO_ACCOUNT + "=? ORDER BY " + SmsOpenHelper.SmsTable.TIME + " ASC) GROUP BY " + SmsOpenHelper.SmsTable.SESSION_ACCOUNT, selectionArgs);
                break;
            default:
                break;
        }
        return cursor;
    }
}