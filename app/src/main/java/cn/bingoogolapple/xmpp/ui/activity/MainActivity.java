package cn.bingoogolapple.xmpp.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import cn.bingoogolapple.xmpp.R;
import cn.bingoogolapple.xmpp.ui.fragment.SessionFragment;
import cn.bingoogolapple.xmpp.ui.fragment.ContactsFragment;

public class MainActivity extends BaseActivity {
    private ViewPager mContentVp;
    private RadioGroup mTabRg;
    private RadioButton mChatRb;
    private RadioButton mContactsRb;

    private SessionFragment mSessionFragment;
    private ContactsFragment mContactsFragment;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        mTitlebar = getViewById(R.id.titlebar);
        mContentVp = getViewById(R.id.vp_main_content);
        mTabRg = getViewById(R.id.rg_main_tab);
        mChatRb = getViewById(R.id.rb_main_chat);
        mContactsRb = getViewById(R.id.rb_main_contacts);
    }

    @Override
    protected void setListener() {
        mContentVp.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mChatRb.setChecked(true);
                        mTitlebar.setTitleText(R.string.chat);
                        break;
                    case 1:
                        mContactsRb.setChecked(true);
                        mTitlebar.setTitleText(R.string.contacts);
                        break;
                    default:
                        break;
                }
            }
        });
        mTabRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_main_chat:
                        mContentVp.setCurrentItem(0, false);
                        break;
                    case R.id.rb_main_contacts:
                        mContentVp.setCurrentItem(1, false);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mContentVp.setAdapter(new ContentAdapter(getSupportFragmentManager()));
    }

    @Override
    public void onBackPressed() {
        mApp.exitWithDoubleClick();
    }

    private class ContentAdapter extends FragmentPagerAdapter {

        public ContentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (mSessionFragment == null) {
                        mSessionFragment = new SessionFragment();
                    }
                    return mSessionFragment;
                case 1:
                    if (mContactsFragment == null) {
                        mContactsFragment = new ContactsFragment();
                    }
                    return mContactsFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}