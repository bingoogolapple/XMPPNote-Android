package cn.bingoogolapple.xmpp.dao;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.xmpp.App;
import cn.bingoogolapple.xmpp.model.MessageModel;
import cn.bingoogolapple.xmpp.provider.SmsProvider;
import cn.bingoogolapple.xmpp.service.IMService;
import cn.bingoogolapple.xmpp.util.BusinessUtil;
import cn.bingoogolapple.xmpp.util.Logger;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/5 下午11:49
 * 描述:
 */
public class SmsDao {
    private static final String TAG = SmsDao.class.getSimpleName();

    public void saveMessage(Message message, String sessionAccount) {
        ContentValues values = new ContentValues();
        values.put(SmsOpenHelper.SmsTable.FROM_ACCOUNT, BusinessUtil.getParticipant(message.getFrom()));
        values.put(SmsOpenHelper.SmsTable.TO_ACCOUNT, BusinessUtil.getParticipant(message.getTo()));
        values.put(SmsOpenHelper.SmsTable.BODY, message.getBody());
        values.put(SmsOpenHelper.SmsTable.STATUS, "offline");
        values.put(SmsOpenHelper.SmsTable.TYPE, message.getType().name());
        values.put(SmsOpenHelper.SmsTable.TIME, System.currentTimeMillis());
        values.put(SmsOpenHelper.SmsTable.SESSION_ACCOUNT, BusinessUtil.getParticipant(sessionAccount));

        Uri uri = App.getInstance().getContentResolver().insert(SmsProvider.URI_SMS, values);
        long newlyId = ContentUris.parseId(uri);
        if (newlyId > 0) {
            Logger.i(TAG, "添加成功" + newlyId);
        } else {
            Logger.i(TAG, "添加失败");
        }
    }

    public List<MessageModel> getMessages(String sessionAccount) {
        List<MessageModel> messageModels = new ArrayList<>();
        Cursor cursor = App.getInstance().getContentResolver().query(SmsProvider.URI_SMS, null, SmsOpenHelper.SmsTable.SESSION_ACCOUNT + "=?", new String[]{sessionAccount}, SmsOpenHelper.SmsTable.TIME + " ASC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                MessageModel messageModel = new MessageModel();
                messageModel.fromAccount = cursor.getString(cursor.getColumnIndex(SmsOpenHelper.SmsTable.FROM_ACCOUNT));
                messageModel.toAccount = cursor.getString(cursor.getColumnIndex(SmsOpenHelper.SmsTable.TO_ACCOUNT));
                messageModel.body = cursor.getString(cursor.getColumnIndex(SmsOpenHelper.SmsTable.BODY));
                messageModel.status = cursor.getString(cursor.getColumnIndex(SmsOpenHelper.SmsTable.STATUS));
                messageModel.type = cursor.getString(cursor.getColumnIndex(SmsOpenHelper.SmsTable.TYPE));
                messageModel.time = cursor.getString(cursor.getColumnIndex(SmsOpenHelper.SmsTable.TIME));
                messageModel.sessionAccount = cursor.getString(cursor.getColumnIndex(SmsOpenHelper.SmsTable.SESSION_ACCOUNT));
                messageModels.add(messageModel);
            }
            cursor.close();
        }
        return messageModels;
    }

    public List<MessageModel> getSessions() {
        List<MessageModel> messageModels = new ArrayList<>();
        Cursor cursor = App.getInstance().getContentResolver().query(SmsProvider.URI_SESSION, null, null, new String[]{IMService.sAccount, IMService.sAccount}, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                MessageModel messageModel = new MessageModel();
                messageModel.fromAccount = cursor.getString(cursor.getColumnIndex(SmsOpenHelper.SmsTable.FROM_ACCOUNT));
                messageModel.toAccount = cursor.getString(cursor.getColumnIndex(SmsOpenHelper.SmsTable.TO_ACCOUNT));
                messageModel.body = cursor.getString(cursor.getColumnIndex(SmsOpenHelper.SmsTable.BODY));
                messageModel.status = cursor.getString(cursor.getColumnIndex(SmsOpenHelper.SmsTable.STATUS));
                messageModel.type = cursor.getString(cursor.getColumnIndex(SmsOpenHelper.SmsTable.TYPE));
                messageModel.time = cursor.getString(cursor.getColumnIndex(SmsOpenHelper.SmsTable.TIME));
                messageModel.sessionAccount = cursor.getString(cursor.getColumnIndex(SmsOpenHelper.SmsTable.SESSION_ACCOUNT));
                messageModels.add(messageModel);
            }
            cursor.close();
        }
        return messageModels;
    }
}