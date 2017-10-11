package com.tang.trade.tang.ui;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tang.trade.tang.MyApp;
import com.tang.trade.tang.R;
import com.tang.trade.tang.ui.base.BaseActivity;
import com.tang.trade.tang.ui.fragment.HomeFragment;
import com.tang.trade.tang.ui.fragment.MeFragment;
import com.tang.trade.tang.utils.TLog;

import java.lang.reflect.Proxy;


public class MainActivity extends BaseActivity {

    private ImageView iv_home;
    private ImageView iv_me;
    private LinearLayout ll_me;
    private LinearLayout ll_home;
    private TextView tv_home;
    private TextView tv_me;
    private boolean mLayoutComplete;
    private HomeFragment homeFragment;
    private MeFragment meFragment;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_home:
            case R.id.ll_home:
            case R.id.home:
                iv_home.setSelected(true);
                iv_me.setSelected(false);
                tv_home.setSelected(true);
                tv_me.setSelected(false);
                addFragment(R.id.container, homeFragment);
                break;
            case R.id.tv_me:
            case R.id.ll_me:
            case R.id.me:
                iv_me.setSelected(true);
                iv_home.setSelected(false);
                tv_home.setSelected(false);
                tv_me.setSelected(true);
                addFragment(R.id.container, meFragment);
                break;
        }
    }

    @Override
    public void initView() {
//        getWindow().getDecorView().setFitsSystemWindows(true);
//        SunsungWorkOut.assistActivity(findViewById(android.R.id.content));
        FrameLayout content = (FrameLayout) findViewById(android.R.id.content);
        content.post(new Runnable() {
            @Override
            public void run() {
                mLayoutComplete = true;
                TLog.log("content 布局完成");
            }
        });
        content.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                TLog.log("onGlobalLayout");
                if (!mLayoutComplete)
                    return;
            }

            
        });


        tv_home = (TextView)findViewById(R.id.tv_home);
        tv_home.setOnClickListener(this);
        tv_me = (TextView)findViewById(R.id.tv_me);
        tv_me.setOnClickListener(this);


        iv_home = (ImageView) findViewById(R.id.home);
        iv_me = (ImageView) findViewById(R.id.me);

        iv_home.setOnClickListener(this);
        iv_me.setOnClickListener(this);
        ll_me = (LinearLayout) findViewById(R.id.ll_me);
        ll_home = (LinearLayout) findViewById(R.id.ll_home);
        ll_me.setOnClickListener(this);
        ll_home.setOnClickListener(this);

        homeFragment = new HomeFragment();
        meFragment = new MeFragment();

        addFragment(R.id.container, homeFragment);
        iv_home.setSelected(true);
        tv_home.setSelected(true);
    }

    @Override
    public void initData() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApp.finishAllActivites();
        MyApp.activities.clear();
    }
}
