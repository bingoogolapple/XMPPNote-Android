package cn.bingoogolapple.xmpp.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.bingoogolapple.xmpp.dao.ContactDao;
import cn.bingoogolapple.xmpp.dao.SmsDao;
import cn.bingoogolapple.xmpp.util.Logger;
import cn.bingoogolapple.xmpp.util.ThreadUtil;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/5 下午2:45
 * 描述:
 */
public class IMService extends Service {
    private static final String TAG = IMService.class.getSimpleName();
    public static final String HOST = "www.bingoogolapple.cn";
    public static final int PORT = 5222;
    public static final String SERVICE_NAME = "bingoogolapple.cn";
    public static XMPPConnection sConn;
    public static String sAccount;

    private Roster mRoster;
    private ContactDao mContactDao;
    private ContactRosterListener mContactRosterListener;

    private SmsDao mSmsDao;
    private ChatManager mChatManager;
    private ChatMessageListener mChatMessageListener;
    private Chat mCurrentChat;
    private Map<String, Chat> mChatMap;

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.i(TAG, "onCreate");
        ThreadUtil.runInThread(new Runnable() {
            @Override
            public void run() {
                initContact();
                initMessage();
            }
        });
    }

    private void initContact() {
        mContactDao = new ContactDao();
        mRoster = sConn.getRoster();
        mContactRosterListener = new ContactRosterListener();
        mRoster.addRosterListener(mContactRosterListener);

        Collection<RosterEntry> entries = mRoster.getEntries();
        for (RosterEntry entry : entries) {
            mContactDao.saveOrUpdateEntry(entry);
        }
    }

    private void initMessage() {
        mSmsDao = new SmsDao();
        mChatManager = IMService.sConn.getChatManager();
        mChatMessageListener = new ChatMessageListener();
        mChatMap = new HashMap<>();
    }

    private boolean sendMsg(Message message) {
        try {
            mCurrentChat = mChatMap.get(message.getTo());
            if (mCurrentChat == null) {
                mCurrentChat = mChatManager.createChat(message.getTo(), mChatMessageListener);
                mChatMap.put(message.getTo(), mCurrentChat);
            }
            mCurrentChat.sendMessage(message);
            mSmsDao.saveMessage(message, message.getTo());
            return true;
        } catch (XMPPException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Logger.i(TAG, "onBind");
        return new IMBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.i(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Logger.i(TAG, "onDestroy");
        if (mRoster != null && mContactRosterListener != null) {
            mRoster.removeRosterListener(mContactRosterListener);
        }
        if (mChatMessageListener != null && mChatMap.size() > 0) {
            Set<Map.Entry<String, Chat>> sets = mChatMap.entrySet();
            for (Map.Entry<String, Chat> entry : sets) {
                entry.getValue().removeMessageListener(mChatMessageListener);
            }
        }
        super.onDestroy();
    }

    public final class IMBinder extends Binder {
        public boolean sendMessage(Message message) {
            return sendMsg(message);
        }
    }

    private final class ContactRosterListener implements RosterListener {

        @Override
        public void entriesAdded(Collection<String> addresses) {
            Logger.i(TAG, "entriesAdded");
            for (String address : addresses) {
                mContactDao.saveOrUpdateEntry(mRoster.getEntry(address));
            }
        }

        @Override
        public void entriesUpdated(Collection<String> addresses) {
            Logger.i(TAG, "entriesUpdated");
            for (String address : addresses) {
                mContactDao.saveOrUpdateEntry(mRoster.getEntry(address));
            }
        }

        @Override
        public void entriesDeleted(Collection<String> addresses) {
            Logger.i(TAG, "entriesDeleted");
            for (String account : addresses) {
                mContactDao.delete(account);
            }
        }

        @Override
        public void presenceChanged(Presence presence) {
            Logger.i(TAG, "presenceChanged");
        }
    }

    private final class ChatMessageListener implements MessageListener {

        @Override
        public void processMessage(Chat chat, Message message) {
            Logger.i(TAG, "消息=" + message.getBody() + " 类型=" + message.getType().name());
            if (message.getType() == Message.Type.chat && !TextUtils.isEmpty(message.getBody())) {
                mSmsDao.saveMessage(message, chat.getParticipant());
            }
        }
    }
}