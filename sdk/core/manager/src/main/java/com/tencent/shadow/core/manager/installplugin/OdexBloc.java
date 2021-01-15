package com.tencent.shadow.core.manager.installplugin;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import dalvik.system.DexClassLoader;

public class OdexBloc {

    private static final ConcurrentHashMap<String, Object> sLocks = new ConcurrentHashMap<>();

    @SuppressWarnings({"SynchronizationOnLocalVariableOrMethodParameter", "ResultOfMethodCallIgnored"})
    public static void odexPluginApk(
            File pluginApkFile,
            File odexDir,
            File odexCopiedTagFile
    ) throws InstallPluginException {
        String key = pluginApkFile.getAbsolutePath();
        Object lock = sLocks.get(key);
        if (lock == null) {
            lock = new Object();
            sLocks.put(key, lock);
        }

        synchronized (lock) {
            if (odexCopiedTagFile.exists()) {
                return;
            }

            /// 如果 odex 存在但是个文件, 而不是目录, 那超出预料了
            /// 删除了也不一定能工作正常
            if (odexDir.exists() && odexDir.isFile()) {
                throw new InstallPluginException("odexDir = " + odexDir.getAbsolutePath() + " 已存在, 但它是个文件, 不敢贸然删除");
            }

            // 创建 odex 目录
            odexDir.mkdirs();

            new DexClassLoader(
                    pluginApkFile.getAbsolutePath(),
                    odexDir.getAbsolutePath(),
                    null,
                    OdexBloc.class.getClassLoader()
            );

            try {
                odexCopiedTagFile.createNewFile();
            } catch (IOException e) {
                throw new InstallPluginException("odexPlugin 完毕. 创建 tag 文件失败: " + odexCopiedTagFile.getAbsolutePath(), e);
            }
        }
    }

}
