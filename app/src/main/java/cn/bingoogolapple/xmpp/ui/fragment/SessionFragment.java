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
public class SessionFragment extends BaseFragment {
    private RecyclerView mDataRv;
    private SessionAdapter mSessionAdapter;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_session);
        mDataRv = getViewById(R.id.rv_session_data);
    }

    @Override
    protected void setListener() {
        mSessionAdapter = new SessionAdapter(mDataRv);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDataRv.setLayoutManager(layoutManager);
        mDataRv.addItemDecoration(new Divider(mActivity));
        mDataRv.setAdapter(mSessionAdapter);
    }

    private static final class SessionAdapter extends BGARecyclerViewAdapter<String> {

        public SessionAdapter(RecyclerView recyclerView) {
            super(recyclerView, R.layout.item_session);
        }

        @Override
        protected void fillData(BGAViewHolderHelper viewHolderHelper, int position, String model) {

        }
    }
}