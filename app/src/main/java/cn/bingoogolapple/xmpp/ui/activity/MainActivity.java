package cn.bingoogolapple.xmpp.ui.activity;

import android.os.Bundle;

import cn.bingoogolapple.xmpp.R;

public class MainActivity extends BaseActivity {

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void setListener() {

    }

    @Override
    public void onBackPressed() {
        mApp.exitWithDoubleClick();
    }
}