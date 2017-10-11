package com.tang.trade.tang.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lzy.okgo.model.Progress;
import com.lzy.okgo.request.base.Request;
import com.tang.trade.tang.MyApp;
import com.tang.trade.tang.PackageInstallReceiver;
import com.tang.trade.tang.R;
import com.tang.trade.tang.net.TangApi;
import com.tang.trade.tang.net.model.UpdateResponseModel;
import com.tang.trade.tang.utils.BuildConfig;
import com.tang.trade.tang.utils.Device;
import com.tang.trade.tang.utils.TLog;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by dagou on 2017/9/11.
 */

public class LaunchActivity extends AppCompatActivity {

    private static final int REDIRECT_DELAY = 2000;

    private static final int UI_ANIMATION_DELAY = 100;
    private static final int APK_INSTALL_CODE = 300;
    private final Handler mHideHandler = new Handler();

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    private View mContentView;
    private Runnable mHidePart2Runnable = new Runnable() {
        @Override
        public void run() {
//            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private Runnable mRedirectToHandler = new Runnable() {
        @Override
        public void run() {
            redirectTo();
//            if (MyApp.get(AppConfig.IS_FIRST_COMING, true)) {
//                startActivity(new Intent(LaunchActivity.this,LeadActivity.class));
//
//                finish();
//            } else {
//                redirectTo();
//            }
        }
    };
    private PopupWindow popWnd;
    private ProgressDialog progressDialog;
    private FrameLayout frameLayout;
    private ProgressBar progressBar;
    private TextView tvProgress;
    private LinearLayout ll_progress;
    private PopupWindow popUpProgress;
    private UpdateResponseModel updateResponse;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        mContentView = findViewById(R.id.fullscreen_content);
        frameLayout = (FrameLayout) findViewById(R.id.bg);

        TLog.log("Device.getVersionCode()" + Device.getVersionCode() + "");
        TLog.log("TangConstant.FILE_APK_PATH" + Device.DEFAULT_SAVE_FILE_PATH);



        checkUpdate();
        setLanguage();

        PackageInstallReceiver.registerReceiver(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (BuildConfig.UPDATE) {
            if (!BuildConfig.IS_UPDATE) {
                redirectTo();
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BuildConfig.UPDATE = false;
        BuildConfig.IS_UPDATE = false;
        PackageInstallReceiver.unregisterReceiver(this);
    }

    private void checkUpdate() {
        TangApi.getUpdateInfo(new TangApi.BaseViewCallback<UpdateResponseModel>() {
            @Override
            public void setData(UpdateResponseModel updateResponseModel) {
                updateResponse = updateResponseModel;
                int i = Device.compareVersion(updateResponseModel.getAndversion(), String.valueOf(Device.getVersionCode()));
                TLog.log("Device.compareVersion(updateResponseModel." + i);
                if (i == 1) {
                    Random random = new Random();
                    int nextInt = random.nextInt(updateResponseModel.getAndurl().size());
                    showPopup(updateResponseModel, nextInt);
                    frameLayout.setAlpha(0.4f);
                    frameLayout.setVisibility(View.VISIBLE);

                } else {
                    mHideHandler.postDelayed(mRedirectToHandler, REDIRECT_DELAY);
                }
            }
        });
    }


    private void redirectTo() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
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

    private void showProgressDialog() {
        progressDialog.show();
    }

    private void ProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }

    private void showPopupProgress() {
        View contentView = LayoutInflater.from(LaunchActivity.this).inflate(R.layout.popup_progress, null);
        progressBar = (ProgressBar) contentView.findViewById(R.id.progressbar);
        progressBar.setMax(100);
        tvProgress = (TextView) contentView.findViewById(R.id.text_progressbar);
        ll_progress = (LinearLayout) contentView.findViewById(R.id.ll_progress);

        popUpProgress = new PopupWindow(LaunchActivity.this);
        popUpProgress.setContentView(contentView);
        popUpProgress.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_rect));
        popUpProgress.setWidth(850);
        popUpProgress.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popUpProgress.setOutsideTouchable(false);
        popUpProgress.showAtLocation(View.inflate(this, R.layout.activity_launch, null), Gravity.CENTER, 0, 0);
    }


    private void showPopup(final UpdateResponseModel updateResponseModel, final int i) {
        View contentView = LayoutInflater.from(LaunchActivity.this).inflate(R.layout.download_alert, null);
        popWnd = new PopupWindow(LaunchActivity.this);
        popWnd.setContentView(contentView);
        popWnd.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_rect));
        popWnd.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popWnd.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popWnd.setOutsideTouchable(false);
        popWnd.showAtLocation(View.inflate(this, R.layout.activity_launch, null), Gravity.CENTER, 0, 0);
        TextView tvContent = (TextView) contentView.findViewById(R.id.content);

        if (!MyApp.get(BuildConfig.LANGUAGE, "").equals("")) {
            if (MyApp.get(BuildConfig.LANGUAGE, "").equalsIgnoreCase("繁體中文")) {
                tvContent.setText(updateResponseModel.getContents_tw());
            } else if (MyApp.get(BuildConfig.LANGUAGE, "").equalsIgnoreCase("简体中文")) {
                tvContent.setText(updateResponseModel.getContents_cn());
            } else if (MyApp.get(BuildConfig.LANGUAGE, "").equalsIgnoreCase("English")) {
                tvContent.setText(updateResponseModel.getContents());
            }
        } else {
            String locale = getResources().getConfiguration().locale.getCountry();
            if (locale.equalsIgnoreCase("TW") || locale.equalsIgnoreCase("HK")) {
                tvContent.setText(updateResponseModel.getContents_tw());
                //简体
            } else if (locale.equalsIgnoreCase("CN")) {
                tvContent.setText(updateResponseModel.getContents_cn());
                //英文
            } else {
                tvContent.setText(updateResponseModel.getContents());
            }
        }

        contentView.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWnd.dismiss();
                checkPermissionB(new PermissionListener() {
                    @Override
                    public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                        TangApi.downloadApk(
                                updateResponseModel.getAndurl().get(i)
                                , new TangApi.BaseViewCallBackWithProgress<File>() {
                                    @Override
                                    public void setData(File file) {
                                        Device.openFile(LaunchActivity.this, file);
                                    }

                                    @Override
                                    public void setProgress(Progress progress) {
//                                progressDialog.setMax((int) progress.totalSize);
//                                progressDialog.setProgress((int) progress.currentSize);
                                        TLog.log("progress.currentSize/progress.totalSize)" + (float) progress.currentSize / (float) progress.totalSize);
                                        TLog.log("(int) ((progress.currentSize/progress.totalSize)*100)===" + (((float) progress.currentSize / (float) progress.totalSize) * 100));
                                        TLog.log("((int) ((progress.currentSize/progress.totalSize)*100))+\"%\"===" + ((float) ((progress.currentSize / progress.totalSize) * 100)) + "%");
                                        progressBar.setProgress((int) ((progress.currentSize / (float) progress.totalSize) * 100));
                                        tvProgress.setText((int) ((((float) progress.currentSize / (float) progress.totalSize) * 100)) + "%");
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

                    @Override
                    public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                        frameLayout.setVisibility(View.INVISIBLE);
                        if (Boolean.parseBoolean(updateResponseModel.getStatus())) {
                            finish();
                            System.exit(0);
                        } else {
                            mHideHandler.postDelayed(mRedirectToHandler, REDIRECT_DELAY);
                            popWnd.dismiss();
                        }
                    }
                });



            }
        });

        contentView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameLayout.setVisibility(View.INVISIBLE);
                if (Boolean.parseBoolean(updateResponseModel.getStatus())) {
                    finish();
                    System.exit(0);
                } else {
                    mHideHandler.postDelayed(mRedirectToHandler, REDIRECT_DELAY);
                    popWnd.dismiss();
                }
            }
        });
    }

    private void checkPermissionB(PermissionListener p) {
        AndPermission
                .with(this)
                .requestCode(200)
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {

                    }
                }).callback(p).start();
    }

    private void downLoadApk() {

    }

    private void setLanguage() {
        if (!MyApp.get(BuildConfig.LANGUAGE, "").equals("")) {
            if (MyApp.get(BuildConfig.LANGUAGE, "").equalsIgnoreCase("繁體中文")) {
                settingLanguage(Locale.TRADITIONAL_CHINESE);
            } else if (MyApp.get(BuildConfig.LANGUAGE, "").equalsIgnoreCase("简体中文")) {
                settingLanguage(Locale.SIMPLIFIED_CHINESE);
            } else if (MyApp.get(BuildConfig.LANGUAGE, "").equalsIgnoreCase("English")) {
                settingLanguage(Locale.ENGLISH);
            }
        }
    }

    private void settingLanguage(Locale locale) {
        Resources resources = getResources();// 获得res资源对象
        Configuration config = resources.getConfiguration();// 获得设置对象
        DisplayMetrics dm = resources.getDisplayMetrics();// 获得屏幕参数：主要是分辨率，像素等。
        config.locale = locale; // 英文
        resources.updateConfiguration(config, dm);
    }


}
