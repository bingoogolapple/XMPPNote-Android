package cn.bingoogolapple.xmpp.ui.fragment;

import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.bingoogolapple.androidcommon.adapter.BGAOnRVItemClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;
import cn.bingoogolapple.xmpp.R;
import cn.bingoogolapple.xmpp.dao.ContactDao;
import cn.bingoogolapple.xmpp.model.Contact;
import cn.bingoogolapple.xmpp.provider.ContactsProvider;
import cn.bingoogolapple.xmpp.ui.activity.ChatActivity;
import cn.bingoogolapple.xmpp.ui.widget.Divider;
import cn.bingoogolapple.xmpp.util.Logger;
import cn.bingoogolapple.xmpp.util.ThreadUtil;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/2 下午10:59
 * 描述:
 */
public class ContactsFragment extends BaseFragment implements BGAOnRVItemClickListener {
    private RecyclerView mDataRv;
    private ContactsAdapter mContactsAdapter;
    private ContactDao mContactDao;
    private ContactContentObserver mContactContentObserver;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_contacts);
        mDataRv = getViewById(R.id.rv_contacts_data);
    }

    @Override
    protected void setListener() {
        mContactsAdapter = new ContactsAdapter(mDataRv);
        mContactsAdapter.setOnRVItemClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        registerContactContentObserver();

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDataRv.setLayoutManager(layoutManager);
        mDataRv.addItemDecoration(new Divider(mActivity));
        mDataRv.setAdapter(mContactsAdapter);

        reloadData();
    }

    private void reloadData() {
        ThreadUtil.runInThread(new Runnable() {
            @Override
            public void run() {
                if (mContactDao == null) {
                    mContactDao = new ContactDao();
                }
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

    @Override
    public void onDestroy() {
        unregisterContactContentObserver();
        super.onDestroy();
    }

    private void registerContactContentObserver() {
        mContactContentObserver = new ContactContentObserver(new Handler());
        mActivity.getContentResolver().registerContentObserver(ContactsProvider.URI_CONTACT, true, mContactContentObserver);
    }

    private void unregisterContactContentObserver() {
        mActivity.getContentResolver().unregisterContentObserver(mContactContentObserver);
    }

    @Override
    public void onRVItemClick(ViewGroup viewGroup, View view, int position) {
        Contact contact = mContactsAdapter.getItem(position);
        Intent intent = new Intent(mActivity, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_ACCOUNT, contact.account);
        intent.putExtra(ChatActivity.EXTRA_NICKNAME, contact.nickname);
        mActivity.forward(intent);
    }

    private final class ContactContentObserver extends ContentObserver {

        public ContactContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            Logger.i(TAG, "联系人发生变化");
            reloadData();
        }
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