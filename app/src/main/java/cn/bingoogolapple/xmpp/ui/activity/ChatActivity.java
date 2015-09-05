package cn.bingoogolapple.xmpp.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import cn.bingoogolapple.titlebar.BGATitlebar;
import cn.bingoogolapple.xmpp.R;
import cn.bingoogolapple.xmpp.service.IMService;
import cn.bingoogolapple.xmpp.util.ThreadUtil;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/5 下午3:35
 * 描述:
 */
public class ChatActivity extends BaseActivity {
    public static final String EXTRA_ACCOUNT = "EXTRA_ACCOUNT";
    public static final String EXTRA_NICKNAME = "EXTRA_NICKNAME";
    private RecyclerView mDataRv;
    private EditText mMsgEt;
    private String mAccount;
    private Chat mChat;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_chat);
        mTitlebar = getViewById(R.id.titlebar);
        mDataRv = getViewById(R.id.rv_chat_data);
        mMsgEt = getViewById(R.id.et_chat_msg);
    }

    @Override
    protected void setListener() {
        mTitlebar.setDelegate(new BGATitlebar.BGATitlebarDelegate() {
            @Override
            public void onClickLeftCtv() {
                onBackPressed();
            }
        });
        mMsgEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    send();
                }
                return true;
            }
        });
        getViewById(R.id.tv_chat_send).setOnClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mTitlebar.setTitleText(String.format(getString(R.string.chat_title), getIntent().getStringExtra(EXTRA_NICKNAME)));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDataRv.setLayoutManager(layoutManager);

        mAccount = getIntent().getStringExtra(EXTRA_ACCOUNT);
        mChat = IMService.sConn.getChatManager().createChat(mAccount, null);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_chat_send) {
            send();
        }
    }

    private void send() {
        final String msg = mMsgEt.getText().toString().trim();
        if (!TextUtils.isEmpty(msg)) {
            ThreadUtil.runInThread(new Runnable() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.setFrom(IMService.sAccount);
                    message.setTo(mAccount);
                    message.setBody(msg);
                    message.setType(Message.Type.chat);
                    try {
                        mChat.sendMessage(message);
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }

                    ThreadUtil.runInUIThread(new Runnable() {
                        @Override
                        public void run() {
                            mMsgEt.setText("");
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        backward();
    }
}