package cn.bingoogolapple.xmpp.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import cn.bingoogolapple.xmpp.dao.ContactOpenHelper;
import cn.bingoogolapple.xmpp.util.Logger;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/3 下午4:48
 * 描述:
 */
public class ContactsProvider extends ContentProvider {
    private static final String TAG = ContactsProvider.class.getSimpleName();
    public static final String AUTHORITIES = ContactsProvider.class.getCanonicalName();
    public static final int MATCHED_CODE_CONTACT = 1;
    public static final Uri URI_CONTACT = Uri.parse("content://" + AUTHORITIES + "/contact");

    private static UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        sUriMatcher.addURI(AUTHORITIES, "/contact", MATCHED_CODE_CONTACT);
    }

    private ContactOpenHelper mContactOpenHelper;

    @Override
    public boolean onCreate() {
        mContactOpenHelper = new ContactOpenHelper(getContext());
        if (mContactOpenHelper != null) {
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
            case MATCHED_CODE_CONTACT:
                SQLiteDatabase db = mContactOpenHelper.getWritableDatabase();
                long newlyId = db.insert(ContactOpenHelper.T_CONTACT, "", values);
                if (newlyId != -1) {
                    Logger.i(TAG, "添加联系人成功");
                    // 拼接最新的Uri
                    uri = ContentUris.withAppendedId(uri, newlyId);

                    // 通知内容观察者数据有改变，null表示所有的内容观察者都能收到
                    getContext().getContentResolver().notifyChange(URI_CONTACT, null);
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
            case MATCHED_CODE_CONTACT:
                SQLiteDatabase db = mContactOpenHelper.getWritableDatabase();
                affectedCount = db.delete(ContactOpenHelper.T_CONTACT, selection, selectionArgs);
                if (affectedCount > 0) {
                    Logger.i(TAG, "删除联系人成功");

                    getContext().getContentResolver().notifyChange(URI_CONTACT, null);
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
            case MATCHED_CODE_CONTACT:
                SQLiteDatabase db = mContactOpenHelper.getWritableDatabase();
                affectedCount = db.update(ContactOpenHelper.T_CONTACT, values, selection, selectionArgs);
                if (affectedCount > 0) {
                    Logger.i(TAG, "修改联系人成功");
                    getContext().getContentResolver().notifyChange(URI_CONTACT, null);
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
            case MATCHED_CODE_CONTACT:
                SQLiteDatabase db = mContactOpenHelper.getWritableDatabase();
                cursor = db.query(ContactOpenHelper.T_CONTACT, projection, selection, selectionArgs, null, null, sortOrder);
                Logger.i(TAG, "查询联系人成功");
                break;
            default:
                break;
        }
        return cursor;
    }
}
