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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bingoogolapple.androidcommon.adapter.BGAOnRVItemClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;
import cn.bingoogolapple.xmpp.R;
import cn.bingoogolapple.xmpp.dao.ContactDao;
import cn.bingoogolapple.xmpp.dao.SmsDao;
import cn.bingoogolapple.xmpp.model.MessageModel;
import cn.bingoogolapple.xmpp.provider.SmsProvider;
import cn.bingoogolapple.xmpp.ui.activity.ChatActivity;
import cn.bingoogolapple.xmpp.ui.widget.Divider;
import cn.bingoogolapple.xmpp.util.ThreadUtil;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/2 下午10:59
 * 描述:
 */
public class SessionFragment extends BaseFragment implements BGAOnRVItemClickListener {
    private RecyclerView mDataRv;
    private SessionAdapter mSessionAdapter;
    private SmsDao mSmsDao;
    private ContactDao mContactDao;
    private SmsContentObserver mSmsContentObserver;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_session);
        mDataRv = getViewById(R.id.rv_session_data);
    }

    @Override
    protected void setListener() {
        mSessionAdapter = new SessionAdapter(mDataRv);
        mSessionAdapter.setOnRVItemClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        registerSmsContentObserver();
        mContactDao = new ContactDao();
        mSmsDao = new SmsDao();
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDataRv.setLayoutManager(layoutManager);
        mDataRv.addItemDecoration(new Divider(mActivity));
        mDataRv.setAdapter(mSessionAdapter);
        reloadData();
    }

    private void reloadData() {
        ThreadUtil.runInThread(new Runnable() {
            @Override
            public void run() {
                final List<MessageModel> messageModels = mSmsDao.getSessions();
                ThreadUtil.runInUIThread(new Runnable() {
                    @Override
                    public void run() {
                        mSessionAdapter.setDatas(messageModels);
                    }
                });
            }
        });
    }

    @Override
    public void onDestroy() {
        unregisterSmsContentObserver();
        super.onDestroy();
    }

    private void registerSmsContentObserver() {
        mSmsContentObserver = new SmsContentObserver(new Handler());
        mActivity.getContentResolver().registerContentObserver(SmsProvider.URI_SMS, true, mSmsContentObserver);
    }

    private void unregisterSmsContentObserver() {
        mActivity.getContentResolver().unregisterContentObserver(mSmsContentObserver);
    }

    @Override
    public void onRVItemClick(ViewGroup viewGroup, View view, int position) {
        MessageModel messageModel = mSessionAdapter.getItem(position);
        Intent intent = new Intent(mActivity, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_SESSION_ACCOUNT, messageModel.sessionAccount);
        intent.putExtra(ChatActivity.EXTRA_SESSION_NICKNAME, mContactDao.getContactByAccount(messageModel.sessionAccount).nickname);
        mActivity.forward(intent);
    }

    private final class SmsContentObserver extends ContentObserver {

        public SmsContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            reloadData();
        }
    }

    private final class SessionAdapter extends BGARecyclerViewAdapter<MessageModel> {
        private SimpleDateFormat mSimpleDateFormat;

        public SessionAdapter(RecyclerView recyclerView) {
            super(recyclerView, R.layout.item_session);
            mSimpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
        }

        @Override
        protected void fillData(BGAViewHolderHelper helper, int position, MessageModel model) {
            helper.setText(R.id.tv_item_session_time, mSimpleDateFormat.format(new Date(Long.parseLong(model.time))));
            helper.setText(R.id.tv_item_session_nickname, mContactDao.getContactByAccount(model.sessionAccount).nickname);
            helper.setText(R.id.tv_item_session_body, model.body);
        }
    }
}