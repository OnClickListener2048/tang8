package com.tang.trade.tang;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.SharedPreferencesCompat;
import android.view.Gravity;
import android.widget.Toast;

import com.tang.trade.tang.net.OkGoClient;
import com.tang.trade.tang.ui.base.BaseActivity;
import com.tang.trade.tang.utils.TLog;
import com.tang.trade.tang.widget.SimplexToast;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.sdk.QbSdk;

import java.util.ArrayList;

import static com.tang.trade.tang.AppConfig.TECENT_BUGLY_APPID;

/**
 * Created by dagou on 2017/9/11.
 */

public class MyApp extends Application {
    private static final String TAG = MyApp.class.getSimpleName();
    private static final String PREF_NAME = "tang.pref";
    private static Context _context;
    private boolean isDebug = true;
    public static ArrayList<BaseActivity> activities = new ArrayList<>();
    @Override
    public void onCreate() {
        super.onCreate();



        _context = getApplicationContext();
        OkGoClient.init();

        initBugly();

    }

    public static void saveActivity(BaseActivity baseActivity) {
        for (BaseActivity activity : activities) {
            if (!activities.contains(baseActivity)) {
                activities.add(baseActivity);
            }

        }
    }

    public static BaseActivity getActivity(Class clazz) {
        for (BaseActivity activity : activities) {
            if (activity.getClass() == clazz) {
                return activity;
            }
        }

        return null;
    }

    public static void finishAllActivites() {
        for (BaseActivity activity : activities) {
            activity.finish();
        }
    }


    private void initBugly() {
        String packageName = _context.getPackageName();
        String processName = AppConfig.getProcessName(android.os.Process.myPid());
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(_context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        CrashReport.initCrashReport(_context, TECENT_BUGLY_APPID, isDebug, strategy);
    }

    public static  MyApp context() {
        return (MyApp) _context;
    }

    public static void set(String key, int value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putInt(key, value);
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }

    public static void set(String key, boolean value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putBoolean(key, value);
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }

    public static void set(String key, String value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(key, value);
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }

    public static boolean get(String key, boolean defValue) {
        return getPreferences().getBoolean(key, defValue);
    }

    public static String get(String key, String defValue) {
        return getPreferences().getString(key, defValue);
    }

    public static int get(String key, int defValue) {
        return getPreferences().getInt(key, defValue);
    }

    public static long get(String key, long defValue) {
        return getPreferences().getLong(key, defValue);
    }

    public static float get(String key, float defValue) {
        return getPreferences().getFloat(key, defValue);
    }

    public static SharedPreferences getPreferences() {
        return context().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void showToast(int message) {
        showToast(message, Toast.LENGTH_LONG, 0);
    }

    public static void showToast(String message) {
        showToast(message, Toast.LENGTH_LONG, 0, Gravity.BOTTOM);
    }

    public static void showToast(int message, int icon) {
        showToast(message, Toast.LENGTH_LONG, icon);
    }

    public static void showToast(String message, int icon) {
        showToast(message, Toast.LENGTH_LONG, icon, Gravity.BOTTOM);
    }

    public static void showToastShort(int message) {
        showToast(message, Toast.LENGTH_SHORT, 0);
    }

    public static void showToastShort(String message) {
        showToast(message, Toast.LENGTH_SHORT, 0, Gravity.BOTTOM);
    }

    public static void showToastShort(int message, Object... args) {
        showToast(message, Toast.LENGTH_SHORT, 0, Gravity.BOTTOM, args);
    }

    public static void showToast(int message, int duration, int icon) {
        showToast(message, duration, icon, Gravity.BOTTOM);
    }

    public static void showToast(int message, int duration, int icon,
                                 int gravity) {
        showToast(context().getString(message), duration, icon, gravity);
    }

    public static void showToast(int message, int duration, int icon,
                                 int gravity, Object... args) {
        showToast(context().getString(message, args), duration, icon, gravity);
    }

    public static void showToast(String message, int duration, int icon, int gravity) {
        Context context = _context;
        if (context != null)
            SimplexToast.show(context, message, gravity, duration);
    }
}
