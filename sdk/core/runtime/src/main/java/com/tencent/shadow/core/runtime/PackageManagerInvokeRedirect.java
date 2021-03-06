package com.tencent.shadow.core.runtime;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.VersionedPackage;
import android.os.Build;

import java.util.List;

/**
 * PackageManagerTransform必须把对PackageManager的调用转到到这个处于Runtime层不被Transform作用的类上来，
 * 而不能把这个类实现的方法直接写在Transform生成的方法体中，是为了避免这个类实现的代码再次被Transform，
 * 形成循环调用。
 */
public class PackageManagerInvokeRedirect {

    private static PluginPackageManager getPluginPackageManager(
            ClassLoader classLoaderOfInvokeCode
    ) {
        return PluginPartInfoManager.getPluginInfo(classLoaderOfInvokeCode).packageManager;
    }

    public static ApplicationInfo getApplicationInfo(
            ClassLoader classLoaderOfInvokeCode,
            String packageName,
            int flags
    ) throws PackageManager.NameNotFoundException {
        return getPluginPackageManager(classLoaderOfInvokeCode).getApplicationInfo(packageName, flags);
    }

    public static ActivityInfo getActivityInfo(
            ClassLoader classLoaderOfInvokeCode,
            ComponentName component,
            int flags
    ) throws PackageManager.NameNotFoundException {
        return getPluginPackageManager(classLoaderOfInvokeCode).getActivityInfo(component, flags);
    }

    public static PackageInfo getPackageInfo(
            ClassLoader classLoaderOfInvokeCode,
            String packageName,
            int flags
    ) throws PackageManager.NameNotFoundException {
        return getPluginPackageManager(classLoaderOfInvokeCode).getPackageInfo(packageName, flags);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public static PackageInfo getPackageInfo(
            ClassLoader classLoaderOfInvokeCode,
            VersionedPackage versionedPackage,
            int flags
    ) throws PackageManager.NameNotFoundException {
        return getPluginPackageManager(classLoaderOfInvokeCode).getPackageInfo(versionedPackage.getPackageName(), flags);
    }

    public static ProviderInfo resolveContentProvider(
            ClassLoader classLoaderOfInvokeCode,
            String name,
            int flags
    ) {
        return getPluginPackageManager(classLoaderOfInvokeCode).resolveContentProvider(name, flags);
    }

    public static List<ProviderInfo> queryContentProviders(
            ClassLoader classLoaderOfInvokeCode,
            String processName,
            int uid,
            int flags
    ) {
        return getPluginPackageManager(classLoaderOfInvokeCode).queryContentProviders(processName, uid, flags);
    }

    public static ResolveInfo resolveActivity(
            ClassLoader classLoaderOfInvokeCode,
            Intent intent,
            int flags
    ) {
        return getPluginPackageManager(classLoaderOfInvokeCode).resolveActivity(intent, flags);
    }

}
