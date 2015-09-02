package cn.bingoogolapple.xmpp.ui.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import cn.bingoogolapple.xmpp.R;
import cn.bingoogolapple.xmpp.util.SweetAlertDialogUtil;

public class LoginActivity extends BaseActivity {
    private static final String HOST = "192.168.199.142";
    private static final int PORT = 5222;
    private EditText mUsernameEt;
    private EditText mPwdEt;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
        mUsernameEt = getViewById(R.id.et_login_username);
        mPwdEt = getViewById(R.id.et_login_pwd);
    }

    @Override
    protected void setListener() {
        getViewById(R.id.btn_login_login).setOnClickListener(this);

        mPwdEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    login();
                }
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_login_login) {
            login();
        }
    }

    private void login() {
        String username = mUsernameEt.getText().toString().trim();
        String pwd = mPwdEt.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            mUsernameEt.setError(getString(R.string.invalid_username_emp_tip));
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            mPwdEt.setError(getString(R.string.invalid_pwd_emp_tip));
            return;
        }
        executeLogin(username, pwd);
    }

    private void executeLogin(final String username, final String pwd) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                showLoadingDialog("数据加载中");
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                ConnectionConfiguration conf = new ConnectionConfiguration(HOST, PORT);

                // 开发的时候明文传输
                conf.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
                conf.setDebuggerEnabled(true);

                XMPPConnection conn = new XMPPConnection(conf);
                try {
                    conn.connect();

                    conn.login(username, pwd);

                    return true;
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                dismissLoadingDialog();
                if (result) {
                    forwardAndFinish(MainActivity.class);
                } else {
                    SweetAlertDialogUtil.showError(LoginActivity.this, "提示", "登陆失败");
                }
            }
        }.execute();
    }

    @Override
    public void onBackPressed() {
        mApp.exitWithDoubleClick();
    }
}