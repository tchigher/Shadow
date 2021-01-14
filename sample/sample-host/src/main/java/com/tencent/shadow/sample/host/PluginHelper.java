package com.tencent.shadow.sample.host;

import android.content.Context;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PluginHelper {

    /*
     * 动态加载的插件管理 APK
     */
    public final static String sPluginManagerApkFileName = "pluginManager.apk";

    /*
     * 动态加载的插件包, 里面包含以下几个部分:
     * 插件 APK,
     * 插件框架 APK(loader APK 和 runtime APK),
     * APK 信息配置关系 JSON 文件
     */
    public final static String sPluginsZipFileName = BuildConfig.DEBUG ? "plugins-debug.zip" : "plugins-release.zip";

    public File mPluginManagerApkFile;

    public File mPluginsZipFile;

    public ExecutorService mSinglePool = Executors.newSingleThreadExecutor();

    private Context mContext;

    private static PluginHelper sInstance = new PluginHelper();

    public static PluginHelper getInstance() {
        return sInstance;
    }

    private PluginHelper() {
    }

    public void init(Context context) {
        mPluginManagerApkFile = new File(context.getFilesDir(), sPluginManagerApkFileName);
        mPluginsZipFile = new File(context.getFilesDir(), sPluginsZipFileName);

        mContext = context.getApplicationContext();

        mSinglePool.execute(new Runnable() {
            @Override
            public void run() {
                preparePlugin();
            }
        });
    }

    private void preparePlugin() {
        try {
            InputStream pluginManagerApkInputStream = mContext.getAssets().open(sPluginManagerApkFileName);
            FileUtils.copyInputStreamToFile(pluginManagerApkInputStream, mPluginManagerApkFile);

            InputStream pluginsZipInputStream = mContext.getAssets().open(sPluginsZipFileName);
            FileUtils.copyInputStreamToFile(pluginsZipInputStream, mPluginsZipFile);

        } catch (IOException e) {
            throw new RuntimeException("从 assets 中复制 APK/ZIP 出错", e);
        }
    }

}
