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
    public final static String sPluginApksZipFileName = BuildConfig.DEBUG ? "pluginsForDebug.zip" : "pluginsForRelease.zip";

    public File mPluginManagerFile;

    public File mPluginZipFile;

    public ExecutorService mSinglePool = Executors.newSingleThreadExecutor();

    private Context mContext;

    private static PluginHelper sInstance = new PluginHelper();

    public static PluginHelper getInstance() {
        return sInstance;
    }

    private PluginHelper() {
    }

    public void init(Context context) {
        mPluginManagerFile = new File(context.getFilesDir(), sPluginManagerApkFileName);
        mPluginZipFile = new File(context.getFilesDir(), sPluginApksZipFileName);

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
            InputStream is = mContext.getAssets().open(sPluginManagerApkFileName);
            FileUtils.copyInputStreamToFile(is, mPluginManagerFile);

            InputStream zip = mContext.getAssets().open(sPluginApksZipFileName);
            FileUtils.copyInputStreamToFile(zip, mPluginZipFile);

        } catch (IOException e) {
            throw new RuntimeException("从 assets 中复制 APK 出错", e);
        }
    }

}
