package com.tang.trade.tang.ui.base;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.tang.trade.tang.MyApp;
import com.tang.trade.tang.R;
import com.tang.trade.tang.interf.BaseView;
import com.tang.trade.tang.utils.BuildConfig;

import java.util.Locale;

import butterknife.ButterKnife;

/**
 * Created by dagou on 2017/9/21.
 */

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener ,BaseView{

    protected LayoutInflater mInflater;
    private Fragment mFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.saveActivity(this);
        initWindow();

        onBeforeSetContentLayout();
        if (getLayoutId() != 0) {
            setContentView(getLayoutId());
        }


        mInflater = getLayoutInflater();
        ButterKnife.bind(this);

        init(savedInstanceState);
        initView();
        initData();
        setLanguage();
    }


    protected void onBeforeSetContentLayout() {

    }

    protected boolean hasActionBar() {
        return getSupportActionBar() != null;
    }

    protected int getLayoutId() {
        return 0;
    }

    protected View inflateView(int resId) {
        return mInflater.inflate(resId, null);
    }

    protected int getActionBarTitle() {
        return R.string.app_name;
    }

    protected boolean hasBackButton() {
        return false;
    }

    protected void init(Bundle savedInstanceState) {
    }

    protected void addFragment(int frameLayoutId, Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (fragment.isAdded()) {
                if (mFragment != null) {
                    transaction.hide(mFragment).show(fragment);
                } else {
                    transaction.show(fragment);
                }
            } else {
                if (mFragment != null) {
                    transaction.hide(mFragment).add(frameLayoutId, fragment);
                } else {
                    transaction.add(frameLayoutId, fragment);
                }
            }
            mFragment = fragment;
            transaction.commit();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(null != this.getCurrentFocus()){
            /**
             * 点击空白位置 隐藏软键盘
             */
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);

    }


    private void initWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE
//            );

        }

    }


    //    protected void initActionBar(ActionBar actionBar) {
//        if (actionBar == null)
//            return;
//        if (hasBackButton()) {
//            mActionBar.setDisplayHomeAsUpEnabled(true);
//            mActionBar.setHomeButtonEnabled(true);
//        } else {
//            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
//            actionBar.setDisplayUseLogoEnabled(false);
//            int titleRes = getActionBarTitle();
//            if (titleRes != 0) {
//                actionBar.setTitle(titleRes);
//            }
//        }
//    }


    private void setLanguage(){
        if (!MyApp.get(BuildConfig.LANGUAGE, "").equals("")){
            if (MyApp.get(BuildConfig.LANGUAGE, "").equalsIgnoreCase("繁體中文")){
                settingLanguage(Locale.TRADITIONAL_CHINESE);
            }else if (MyApp.get(BuildConfig.LANGUAGE, "").equalsIgnoreCase("简体中文")){
                settingLanguage(Locale.SIMPLIFIED_CHINESE);
            }else if (MyApp.get(BuildConfig.LANGUAGE, "").equalsIgnoreCase("English")){
                settingLanguage(Locale.ENGLISH);
            }
        }
    }

    private void settingLanguage(Locale locale){
        Resources resources = getResources();// 获得res资源对象
        Configuration config = resources.getConfiguration();// 获得设置对象
        DisplayMetrics dm = resources.getDisplayMetrics();// 获得屏幕参数：主要是分辨率，像素等。
        config.locale = locale; // 英文
        resources.updateConfiguration(config, dm);
    }
}
