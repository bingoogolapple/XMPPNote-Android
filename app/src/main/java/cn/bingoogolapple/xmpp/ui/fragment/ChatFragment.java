package cn.bingoogolapple.xmpp.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;
import cn.bingoogolapple.xmpp.R;
import cn.bingoogolapple.xmpp.ui.widget.Divider;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/2 下午10:59
 * 描述:
 */
public class ChatFragment extends BaseFragment {
    private RecyclerView mDataRv;
    private ChatAdapter mChatAdapter;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_chat);
        mDataRv = getViewById(R.id.rv_chat_data);
    }

    @Override
    protected void setListener() {
        mChatAdapter = new ChatAdapter(mDataRv);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDataRv.setLayoutManager(layoutManager);
        mDataRv.addItemDecoration(new Divider(mActivity));
        mDataRv.setAdapter(mChatAdapter);
    }

    private static final class ChatAdapter extends BGARecyclerViewAdapter<String> {

        public ChatAdapter(RecyclerView recyclerView) {
            super(recyclerView, R.layout.item_chat);
        }

        @Override
        protected void fillData(BGAViewHolderHelper viewHolderHelper, int position, String model) {

        }
    }
}