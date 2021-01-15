package com.tencent.shadow.core.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import com.tencent.shadow.core.common.Logger;
import com.tencent.shadow.core.common.LoggerFactory;
import com.tencent.shadow.core.manager.installplugin.AppCacheFolderManager;
import com.tencent.shadow.core.manager.installplugin.CopySoBloc;
import com.tencent.shadow.core.manager.installplugin.InstallPluginException;
import com.tencent.shadow.core.manager.installplugin.InstalledDao;
import com.tencent.shadow.core.manager.installplugin.InstalledPlugin;
import com.tencent.shadow.core.manager.installplugin.InstalledPluginDBHelper;
import com.tencent.shadow.core.manager.installplugin.InstalledType;
import com.tencent.shadow.core.manager.installplugin.OdexBloc;
import com.tencent.shadow.core.manager.installplugin.PluginsConfig;
import com.tencent.shadow.core.manager.installplugin.UnpackManager;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public abstract class BasePluginManager {

    private static final Logger mLogger = LoggerFactory.getLogger(BasePluginManager.class);

    /*
     * 宿主的 context 对象
     */
    public Context mHostContext;

    /*
     * 从压缩包中将插件解压出来, 解析成 InstalledPlugin
     */
    private final UnpackManager mUnpackManager;

    /*
     * 插件信息查询数据库接口
     */
    private final InstalledDao mInstalledDao;

    /*
     * UI 线程的 handler
     */
    protected Handler mUiHandler = new Handler(Looper.getMainLooper());

    public BasePluginManager(
            Context context
    ) {
        mHostContext = context.getApplicationContext();
        mUnpackManager = new UnpackManager(mHostContext.getFilesDir(), getName());
        mInstalledDao = new InstalledDao(new InstalledPluginDBHelper(mHostContext, getName()));
    }

    /*
     * PluginManager 的名字
     * 用于和其它 PluginManager 区分持续化存储的名字
     */
    abstract protected String getName();

    /**
     * 从压缩包中解压插件
     *
     * @param pluginsZipFile     压缩包路径
     * @param pluginsZipFileHash 压缩包 hash
     * @return PluginConfig
     */
    public final PluginsConfig installPluginsFromZipFile(
            File pluginsZipFile,
            @Nullable String pluginsZipFileHash
    ) throws IOException, JSONException {
        return mUnpackManager.unpackPlugins(pluginsZipFileHash, pluginsZipFile);
    }

    /**
     * 安装完成时调用
     * 将插件信息持久化到数据库
     *
     * @param pluginsConfig 插件配置信息
     */
    public final void onPluginsInstallCompleted(
            PluginsConfig pluginsConfig
    ) {
        File namedPluginsManagerDir = mUnpackManager.getNamedPluginsManagerDirInPluginsUnpackDir();
        String soDirAbsolutePath = AppCacheFolderManager.getLibDir(namedPluginsManagerDir, pluginsConfig.UUID).getAbsolutePath();
        String odexDirAbsolutePath = AppCacheFolderManager.getOdexDir(namedPluginsManagerDir, pluginsConfig.UUID).getAbsolutePath();

        mInstalledDao.insert(pluginsConfig, soDirAbsolutePath, odexDirAbsolutePath);
    }

    protected InstalledPlugin.Part getPluginPartByPartKey(
            String uuid,
            String partKey
    ) {
        InstalledPlugin installedPlugin = mInstalledDao.getInstalledPluginByUUID(uuid);
        if (installedPlugin != null) {
            return installedPlugin.getPart(partKey);
        }
        throw new RuntimeException("没有找到Part partKey:" + partKey);
    }

    protected InstalledPlugin getInstalledPlugin(
            String uuid
    ) {
        return mInstalledDao.getInstalledPluginByUUID(uuid);
    }

    protected InstalledPlugin.Part getLoaderOrRunTimePart(
            String uuid,
            int type
    ) {
        if (type != InstalledType.TYPE_PLUGIN_LOADER && type != InstalledType.TYPE_PLUGIN_RUNTIME) {
            throw new RuntimeException("不支持的type:" + type);
        }
        InstalledPlugin installedPlugin = mInstalledDao.getInstalledPluginByUUID(uuid);
        if (type == InstalledType.TYPE_PLUGIN_RUNTIME) {
            if (installedPlugin.runtimeFile != null) {
                return installedPlugin.runtimeFile;
            }
        } else if (type == InstalledType.TYPE_PLUGIN_LOADER) {
            if (installedPlugin.pluginLoaderFile != null) {
                return installedPlugin.pluginLoaderFile;
            }
        }
        throw new RuntimeException("没有找到Part type :" + type);
    }

    /**
     * odex 优化
     *
     * @param pluginsUUID      插件包的 UUID
     * @param pluginAppPartKey 要 odex 的插件 partKey
     */
    public final void odexPlugin(
            String pluginsUUID,
            String pluginAppPartKey,
            File pluginApkFile
    ) throws InstallPluginException {
        try {
            File namedPluginsManagerDir = mUnpackManager.getNamedPluginsManagerDirInPluginsUnpackDir();
            File odexDir = AppCacheFolderManager.getOdexDir(namedPluginsManagerDir, pluginsUUID);
            OdexBloc.odexPluginApk(
                    pluginApkFile,
                    odexDir,
                    AppCacheFolderManager.getOdexCopiedTagFile(odexDir, pluginAppPartKey)
            );
        } catch (InstallPluginException e) {
            if (mLogger.isErrorEnabled()) {
                mLogger.error("oDexPlugin exception:", e);
            }
            throw e;
        }
    }

    /**
     * odex优化
     *
     * @param pluginsUUID   插件包的 UUID
     * @param pluginType    要 odex 的插件类型
     * @param pluginApkFile 插件 APK 文件
     */
    public final void odexPluginLoaderOrRunTime(
            String pluginsUUID,
            int pluginType,
            File pluginApkFile
    ) throws InstallPluginException {
        try {
            File namedPluginsManagerDir = mUnpackManager.getNamedPluginsManagerDirInPluginsUnpackDir();
            File odexDir = AppCacheFolderManager.getOdexDir(namedPluginsManagerDir, pluginsUUID);
            String pluginLoaderOrRuntimeKey = pluginType == InstalledType.TYPE_PLUGIN_LOADER ? "loader" : "runtime";
            OdexBloc.odexPluginApk(pluginApkFile, odexDir, AppCacheFolderManager.getOdexCopiedTagFile(odexDir, pluginLoaderOrRuntimeKey));
        } catch (InstallPluginException e) {
            if (mLogger.isErrorEnabled()) {
                mLogger.error("oDexPluginLoaderOrRunTime exception:", e);
            }
            throw e;
        }
    }

    /**
     * 插件apk的so解压
     *
     * @param pluginsUUID      插件包的 UUID
     * @param pluginAppPartKey 要解压 SO 的插件 partKey
     * @param pluginApkFile    插件 APK 文件
     */
    public final void extractSo(
            String pluginsUUID,
            String pluginAppPartKey,
            File pluginApkFile
    ) throws InstallPluginException {
        try {
            File root = mUnpackManager.getNamedPluginsManagerDirInPluginsUnpackDir();
            String filter = "lib/" + getAbi() + "/";
            File soDir = AppCacheFolderManager.getLibDir(root, pluginsUUID);
            CopySoBloc.copySo(
                    pluginApkFile,
                    soDir,
                    AppCacheFolderManager.getLibCopiedFile(soDir, pluginAppPartKey),
                    filter
            );
        } catch (InstallPluginException e) {
            if (mLogger.isErrorEnabled()) {
                mLogger.error("extractSo exception:", e);
            }
            throw e;
        }
    }

    /**
     * 获取已安装的插件，最后安装的排在返回 list 的最前面
     *
     * @param limit 最多获取个数
     */
    public final List<InstalledPlugin> getInstalledPlugins(
            int limit
    ) {
        return mInstalledDao.getLastPlugins(limit);
    }

    /**
     * 删除指定 uuid 的插件
     *
     * @param uuid 插件包的 uuid
     * @return 是否全部执行成功
     */
    public boolean deleteInstalledPlugin(
            String uuid
    ) {
        InstalledPlugin installedPlugin = mInstalledDao.getInstalledPluginByUUID(uuid);
        boolean suc = true;
        if (installedPlugin.runtimeFile != null) {
            if (!deletePart(installedPlugin.runtimeFile)) {
                suc = false;
            }
        }
        if (installedPlugin.pluginLoaderFile != null) {
            if (!deletePart(installedPlugin.pluginLoaderFile)) {
                suc = false;
            }
        }
        for (Map.Entry<String, InstalledPlugin.PluginPart> plugin : installedPlugin.plugins.entrySet()) {
            if (!deletePart(plugin.getValue())) {
                suc = false;
            }
        }
        if (mInstalledDao.deleteByUUID(uuid) <= 0) {
            suc = false;
        }
        return suc;
    }

    private boolean deletePart(
            InstalledPlugin.Part part
    ) {
        boolean suc = true;
        if (!part.pluginFile.delete()) {
            suc = false;
        }
        if (part.oDexDir != null) {
            if (!part.oDexDir.delete()) {
                suc = false;
            }
        }
        if (part.libraryDir != null) {
            if (!part.libraryDir.delete()) {
                suc = false;
            }
        }
        return suc;
    }

    /**
     * 业务插件的 abi
     */
    public String getAbi() {
        return null;
    }

}
