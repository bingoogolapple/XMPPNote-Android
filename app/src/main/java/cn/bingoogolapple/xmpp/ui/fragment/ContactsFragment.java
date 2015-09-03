package cn.bingoogolapple.xmpp.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;

import java.util.Collection;
import java.util.List;

import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;
import cn.bingoogolapple.xmpp.R;
import cn.bingoogolapple.xmpp.dao.ContactDao;
import cn.bingoogolapple.xmpp.engine.IMEngine;
import cn.bingoogolapple.xmpp.model.Contact;
import cn.bingoogolapple.xmpp.ui.widget.Divider;
import cn.bingoogolapple.xmpp.util.Logger;
import cn.bingoogolapple.xmpp.util.ThreadUtil;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/2 下午10:59
 * 描述:
 */
public class ContactsFragment extends BaseFragment {
    private RecyclerView mDataRv;
    private ContactsAdapter mContactsAdapter;
    private Roster mRoster;
    private ContactDao mContactDao;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_contacts);
        mDataRv = getViewById(R.id.rv_contacts_data);
    }

    @Override
    protected void setListener() {
        mContactsAdapter = new ContactsAdapter(mDataRv);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDataRv.setLayoutManager(layoutManager);
        mDataRv.addItemDecoration(new Divider(mActivity));
        mDataRv.setAdapter(mContactsAdapter);

        initDatabase();
    }

    private void initDatabase() {
        ThreadUtil.runInThread(new Runnable() {
            @Override
            public void run() {
                mContactDao = new ContactDao();
                mRoster = IMEngine.sConn.getRoster();
                final Collection<RosterEntry> entries = mRoster.getEntries();
                for (RosterEntry entry : entries) {
                    mContactDao.saveOrUpdateEntry(entry);
                }
                mRoster.addRosterListener(new RosterListener() {
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
                        for (String address : addresses) {
                            RosterEntry entry = mRoster.getEntry(address);
                            if (entry != null) {
                                mContactDao.delete(entry.getUser());
                            }
                        }
                    }

                    @Override
                    public void presenceChanged(Presence presence) {
                        Logger.i(TAG, "presenceChanged");
                    }
                });

                final List<Contact> contacts = mContactDao.getContacts();

                ThreadUtil.runInUIThread(new Runnable() {
                    @Override
                    public void run() {
                        mContactsAdapter.setDatas(contacts);
                    }
                });
            }
        });

    }

    private static final class ContactsAdapter extends BGARecyclerViewAdapter<Contact> {

        public ContactsAdapter(RecyclerView recyclerView) {
            super(recyclerView, R.layout.item_contacts);
        }

        @Override
        protected void fillData(BGAViewHolderHelper helper, int position, Contact model) {
            helper.setText(R.id.tv_item_contacts_nickname, model.nickname);
            helper.setText(R.id.tv_item_contacts_account, model.account);
        }
    }

}