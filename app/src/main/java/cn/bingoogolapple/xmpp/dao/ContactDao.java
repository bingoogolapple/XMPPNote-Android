package cn.bingoogolapple.xmpp.dao;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import org.jivesoftware.smack.RosterEntry;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.xmpp.App;
import cn.bingoogolapple.xmpp.model.Contact;
import cn.bingoogolapple.xmpp.provider.ContactsProvider;
import cn.bingoogolapple.xmpp.util.Logger;
import cn.bingoogolapple.xmpp.util.PinyinUtil;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/3 下午11:27
 * 描述:
 */
public class ContactDao {
    private static final String TAG = ContactDao.class.getSimpleName();

    public void saveOrUpdateEntry(RosterEntry entry) {
        String account = entry.getUser();
//            account = account.substring(0, account.indexOf("@")) + IMEngine.SERVICE_NAME;

        String nickname = entry.getName();
        if (TextUtils.isEmpty(nickname)) {
            nickname = account.substring(0, account.indexOf("@"));
        }

        ContentValues values = new ContentValues();
        values.put(ContactOpenHelper.ContactTable.ACCOUNT, account);
        values.put(ContactOpenHelper.ContactTable.NICKNAME, nickname);
        values.put(ContactOpenHelper.ContactTable.AVATAR, "0");
        values.put(ContactOpenHelper.ContactTable.PINYIN, PinyinUtil.getPinyin(nickname));

        int updatedCount = App.getInstance().getContentResolver().update(ContactsProvider.URI_CONTACT, values, ContactOpenHelper.ContactTable.ACCOUNT + "=?", new String[]{account});
        if (updatedCount <= 0) {
            Uri uri = App.getInstance().getContentResolver().insert(ContactsProvider.URI_CONTACT, values);
            long newlyId = ContentUris.parseId(uri);
            if (newlyId > 0) {
                Logger.i(TAG, "添加成功" + newlyId);
            } else {
                Logger.i(TAG, "添加失败");
            }
        } else {
            Logger.i(TAG, "修改成功" + updatedCount);
        }
    }

    public List<Contact> getContacts() {
        List<Contact> contacts = new ArrayList<>();
        Cursor cursor = App.getInstance().getContentResolver().query(ContactsProvider.URI_CONTACT, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Contact contact = new Contact();
                contact.account = cursor.getString(cursor.getColumnIndex(ContactOpenHelper.ContactTable.ACCOUNT));
                contact.nickname = cursor.getString(cursor.getColumnIndex(ContactOpenHelper.ContactTable.NICKNAME));
                contact.avatar = cursor.getString(cursor.getColumnIndex(ContactOpenHelper.ContactTable.AVATAR));
                contact.pinyin = cursor.getString(cursor.getColumnIndex(ContactOpenHelper.ContactTable.PINYIN));
                contacts.add(contact);
            }
        }
        return contacts;
    }

}