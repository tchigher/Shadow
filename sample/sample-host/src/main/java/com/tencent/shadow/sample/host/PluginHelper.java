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
    public final static String sSamplePluginManagerApkFileName = "samplePluginManager.apk";

    /*
     * 动态加载的插件包, 里面包含以下几个部分:
     * 插件 APK,
     * 插件框架 APK(loader APK 和 runtime APK),
     * APK 信息配置关系 JSON 文件
     */
    public final static String sMgMoviePluginZipFileName = BuildConfig.DEBUG
            ? "mgmoviePlugins-debug.zip"
            : "mgmoviePlugins-release.zip";

    public File mPluginManagerApkFile;

    public File mMgMoviePluginZipFile;

    public ExecutorService mSinglePool = Executors.newSingleThreadExecutor();

    private Context mContext;

    private static PluginHelper sInstance = new PluginHelper();

    public static PluginHelper getInstance(
    ) {
        return sInstance;
    }

    private PluginHelper(
    ) {
    }

    public void init(
            Context context
    ) {
        mPluginManagerApkFile = new File(context.getFilesDir(), sSamplePluginManagerApkFileName);

        mMgMoviePluginZipFile = new File(context.getFilesDir(), sMgMoviePluginZipFileName);

        mContext = context.getApplicationContext();

        mSinglePool.execute(this::preparePlugin);
    }

    private void preparePlugin(
    ) {
        try {
            System.loadLibrary("aes");

            InputStream samplePluginManagerApkInputStream = mContext.getAssets().open(sSamplePluginManagerApkFileName);
            FileUtils.copyInputStreamToFile(samplePluginManagerApkInputStream, mPluginManagerApkFile);

            InputStream mgMoviePluginZipInputStream = mContext.getAssets().open(sMgMoviePluginZipFileName);
            FileUtils.copyInputStreamToFile(mgMoviePluginZipInputStream, mMgMoviePluginZipFile);
        } catch (IOException e) {
            throw new RuntimeException("从 assets 中复制 APK/ZIP 出错", e);
        }
    }

}
