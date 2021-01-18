package com.tencent.shadow.sample.host;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.webkit.WebView;

import com.tencent.shadow.core.common.LoggerFactory;
import com.tencent.shadow.dynamic.host.DynamicRuntime;
import com.tencent.shadow.dynamic.host.PluginManager;
import com.tencent.shadow.sample.host.manager.Shadow;

import java.io.File;

import static android.os.Process.myPid;

public class HostApplication extends Application {

    private static HostApplication sApp;

    private PluginManager mPluginManager;

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;

        detectNonSdkApiUsageOnAndroidP();
        setWebViewDataDirectorySuffix();
        LoggerFactory.setILoggerFactory(new AndroidLogLoggerFactory());

        if (isProcess(this, ":plugin")) {
            /// 在全动态架构中，Activity 组件没有打包在宿主而是位于被动态加载的 runtime，
            /// 为了防止插件 crash 后，系统自动恢复 crash 前的 Activity 组件，
            /// 此时由于没有加载 runtime 而发生 classNotFound 异常，导致二次 crash
            /// 因此这里恢复加载上一次的 runtime
            DynamicRuntime.recoveryRuntime(this);
        }

        PluginHelper.getInstance().init(this);
    }

    private static void detectNonSdkApiUsageOnAndroidP() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return;
        }
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        builder.detectNonSdkApiUsage();
        StrictMode.setVmPolicy(builder.build());
    }

    private static void setWebViewDataDirectorySuffix() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return;
        }
        WebView.setDataDirectorySuffix(Application.getProcessName());
    }

    public static HostApplication getApp() {
        return sApp;
    }

    public void loadPluginManager(File apk) {
        if (mPluginManager == null) {
            mPluginManager = Shadow.getPluginManager(apk);
        }
    }

    public PluginManager getPluginManager() {
        return mPluginManager;
    }

    private static boolean isProcess(
            Context context,
            String processName
    ) {
        String currentProcessName = "";
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : activityManager.getRunningAppProcesses()) {
            if (runningAppProcessInfo.pid == myPid()) {
                currentProcessName = runningAppProcessInfo.processName;
                break;
            }
        }

        return currentProcessName.endsWith(processName);
    }
}
