package com.tang.trade.tang.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lzy.okgo.model.Progress;
import com.lzy.okgo.request.base.Request;
import com.tang.trade.tang.MyApp;
import com.tang.trade.tang.R;
import com.tang.trade.tang.net.TangApi;
import com.tang.trade.tang.net.model.UpdateResponseModel;
import com.tang.trade.tang.ui.base.BaseActivity;
import com.tang.trade.tang.utils.BuildConfig;
import com.tang.trade.tang.utils.Device;
import com.tang.trade.tang.utils.TLog;

import java.io.File;
import java.util.Random;

import butterknife.BindView;

public class SettingActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_Version)
    TextView tvVersion;
    @BindView(R.id.tv_check_version)
    TextView tvCkeck;
    @BindView(R.id.tv_languages)
    TextView tvLanguages;
    @BindView(R.id.tv_exit)
    TextView tvExit;
    private PopupWindow popWnd;
    @BindView(R.id.bg)
    FrameLayout frameLayout;
    private ProgressBar progressBar;
    private TextView tvProgress;
    private PopupWindow popUpProgress;

    @BindView(R.id.ll)
    View mContentView;
    private static final int UI_ANIMATION_DELAY = 100;

    private final Handler mHideHandler = new Handler();

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private Runnable mHidePart2Runnable = new Runnable() {
        @Override
        public void run() {
//            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.FullscreenTheme);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_check_version:
                checkUpdate();
                break;
            case R.id.tv_languages:
                startActivity(new Intent(this,ChangeLanguageActivity.class));
                break;
            case R.id.tv_exit:
                showDialog();
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public void initView() {
        ivBack.setOnClickListener(this);
        tvCkeck.setOnClickListener(this);
        tvLanguages.setOnClickListener(this);
        tvExit.setOnClickListener(this);
    }

    @Override
    public void initData() {
        String versionName = "1.0.0";
        try {
            versionName = MyApp.context()
                    .getPackageManager()
                    .getPackageInfo(MyApp.context().getPackageName(), 0)
                    .versionName;
        } catch (PackageManager.NameNotFoundException ex) {
            versionName = "1.0.0";
        }
        tvVersion.setText(versionName);
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(100);
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private void hide() {
        // Hide UI firs
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * 退出
     */
    private void showDialog() {
  /*
  这里使用了 android.support.v7.app.AlertDialog.Builder
  可以直接在头部写 import android.support.v7.app.AlertDialog
  那么下面就可以写成 AlertDialog.Builder
  */
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle(R.string.Attention);
        builder.setMessage(R.string.exit);
        builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyApp.set(BuildConfig.USERNAME, "no_username");
                startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                finish();

            }
        });
        builder.show();
    }


    /**
     * 检查版本更新
     */
    private void checkUpdate() {
        TangApi.getUpdateInfo(new TangApi.BaseViewCallback<UpdateResponseModel>() {
            @Override
            public void setData(UpdateResponseModel updateResponseModel) {
                int i = Device.compareVersion(updateResponseModel.getAndversion(), String.valueOf(Device.getVersionCode()));
                TLog.log("Device.compareVersion(updateResponseModel." + i);
                if (i == 1) {
                    Random random = new Random();
                    int nextInt = random.nextInt(updateResponseModel.getAndurl().size());
                    showPopup(updateResponseModel, nextInt);
                    frameLayout.setAlpha(0.4f);
                    frameLayout.setVisibility(View.VISIBLE);

                } else {
                    MyApp.showToast(getString(R.string.noversion));
                }
            }
        });
    }


    private void showPopup(final UpdateResponseModel updateResponseModel, final int i) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.download_alert, null);
        popWnd = new PopupWindow(this);
        popWnd.setContentView(contentView);
        popWnd.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_rect));
        popWnd.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popWnd.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popWnd.setOutsideTouchable(false);
        popWnd.showAtLocation(View.inflate(this, R.layout.activity_launch, null), Gravity.CENTER, 0, 0);
        TextView tvContent = (TextView) contentView.findViewById(R.id.content);
        if (!MyApp.get(BuildConfig.LANGUAGE, "").equals("")){
            if (MyApp.get(BuildConfig.LANGUAGE, "").equalsIgnoreCase("繁體中文")){
                tvContent.setText(updateResponseModel.getContents_tw());
            }else if (MyApp.get(BuildConfig.LANGUAGE, "").equalsIgnoreCase("简体中文")){
                tvContent.setText(updateResponseModel.getContents_cn());
            }else if (MyApp.get(BuildConfig.LANGUAGE, "").equalsIgnoreCase("English")){
                tvContent.setText(updateResponseModel.getContents());
            }
        }else{
            String locale = getResources().getConfiguration().locale.getCountry();
            if (locale.equalsIgnoreCase("TW") || locale.equalsIgnoreCase("HK")){
                tvContent.setText(updateResponseModel.getContents_tw());
                //简体
            }else if (locale.equalsIgnoreCase("CN")){
                tvContent.setText(updateResponseModel.getContents_cn());
                //英文
            }else{
                tvContent.setText(updateResponseModel.getContents());
            }
        }

        contentView.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWnd.dismiss();

                TangApi.downloadApk(
                        updateResponseModel.getAndurl().get(i)
                        , new TangApi.BaseViewCallBackWithProgress<File>() {
                            @Override
                            public void setData(File file) {
                                Device.openFile(SettingActivity.this, file);
                            }

                            @Override
                            public void setProgress(Progress progress) {
//                                progressDialog.setMax((int) progress.totalSize);
//                                progressDialog.setProgress((int) progress.currentSize);
                                TLog.log("progress.currentSize/progress.totalSize)"+(float)progress.currentSize/ (float)progress.totalSize);
                                TLog.log("(int) ((progress.currentSize/progress.totalSize)*100)==="+(((float) progress.currentSize/(float) progress.totalSize)*100));
                                TLog.log("((int) ((progress.currentSize/progress.totalSize)*100))+\"%\"==="+((float) ((progress.currentSize/progress.totalSize)*100))+"%");
                                progressBar.setProgress((int)((progress.currentSize/(float)progress.totalSize)*100));
                                tvProgress.setText((int)((((float)progress.currentSize/(float)progress.totalSize)*100))+"%");
                            }

                            @Override
                            public void onStart(Request<File, ? extends Request> request) {
//                                ProgressDialog();
//                                showProgressDialog();
                                showPopupProgress();
                            }

                            @Override
                            public void onFinish() {
//                                progressDialog.dismiss();
                                frameLayout.setVisibility(View.INVISIBLE);
                                popUpProgress.dismiss();
                            }
                        });
            }
        });

        contentView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameLayout.setVisibility(View.INVISIBLE);
                popWnd.dismiss();
            }
        });
    }

    private void showPopupProgress() {
        View contentView = LayoutInflater.from(SettingActivity.this).inflate(R.layout.popup_progress, null);
        progressBar = (ProgressBar) contentView.findViewById(R.id.progressbar);
        progressBar.setMax(100);
        tvProgress = (TextView) contentView.findViewById(R.id.text_progressbar);

        popUpProgress = new PopupWindow(SettingActivity.this);
        popUpProgress.setContentView(contentView);
        popUpProgress.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_rect));
        popUpProgress.setWidth(850);
        popUpProgress.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popUpProgress.setOutsideTouchable(false);
        popUpProgress.showAtLocation(View.inflate(this, R.layout.activity_setting, null), Gravity.CENTER, 0, 0);
    }
}
