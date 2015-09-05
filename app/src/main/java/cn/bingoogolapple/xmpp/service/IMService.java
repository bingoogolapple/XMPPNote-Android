package cn.bingoogolapple.xmpp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;

import java.util.Collection;

import cn.bingoogolapple.xmpp.dao.ContactDao;
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

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.i(TAG, "onCreate");
        initDatabase();
    }

    private void initDatabase() {
        ThreadUtil.runInThread(new Runnable() {
            @Override
            public void run() {
                mContactDao = new ContactDao();
                mRoster = sConn.getRoster();
                mContactRosterListener = new ContactRosterListener();
                mRoster.addRosterListener(mContactRosterListener);

                Collection<RosterEntry> entries = mRoster.getEntries();
                for (RosterEntry entry : entries) {
                    mContactDao.saveOrUpdateEntry(entry);
                }
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Logger.i(TAG, "onBind");
        return null;
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
        super.onDestroy();
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
}