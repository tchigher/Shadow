package com.tencent.shadow.dynamic.host;

import android.text.TextUtils;

import com.tencent.shadow.core.common.InstalledApk;
import com.tencent.shadow.core.common.Logger;
import com.tencent.shadow.core.common.LoggerFactory;

import java.io.File;

import dalvik.system.BaseDexClassLoader;

/**
 * 将 Container 部分的 hack 到 PathClassLoader 之上, 形成如下结构的 classLoader 树结构
 * ---BootClassLoader
 * ----ContainerClassLoader (可能有多个)
 * -----PathClassLoader
 */
public class MultiDynamicContainer {

    private static final Logger mLogger = LoggerFactory.getLogger(MultiDynamicContainer.class);

    /**
     * hack ContainerClassLoader 到 PathClassLoader 之上
     * 1. ClassLoader 树结构中可能包含多个 ContainerClassLoader
     * 2. 在 hack 时, 需要提供 containerKey 作为该插件 containerApk 的标识
     *
     * @param containerKey 插件业务对应的 key, 不随插件版本变动
     * @param containerApk 插件 ZIP 包中的 runtimeApk
     */
    public static boolean loadContainerApk(
            String containerKey,
            InstalledApk containerApk
    ) {
        // 根据 key 去查找对应的 ContainerClassLoader
        ContainerClassLoader containerClassLoader = findContainerClassLoader(containerKey);
        if (containerClassLoader != null) {
            String apkFilePath = containerClassLoader.apkFilePath;
            if (mLogger.isInfoEnabled()) {
                mLogger.info("该 containKey 的 apk 已经加载过, containKey = " + containerKey +
                        ", last apkPath = " + apkFilePath + ", new apkPath = " + containerApk.mApkFilePath);
            }

            if (TextUtils.equals(apkFilePath, containerApk.mApkFilePath)) {
                // 已经加载相同版本的 containerApk 了, 不需要加载
                if (mLogger.isInfoEnabled()) {
                    mLogger.info("已经加载相同 apkPath 的 containerApk 了, 不需要加载");
                }
                return false;
            } else {
                // 同个插件的 ContainerClassLoader 版本不一样, 说明要移除老的ContainerClassLoader, 插入新的
                if (mLogger.isInfoEnabled()) {
                    mLogger.info("加载不相同 apkPath 的 containerApk 了, 先将老的移除");
                }
                try {
                    removeContainerClassLoader(containerClassLoader);
                } catch (Exception e) {
                    mLogger.error("移除老的 containerApk 失败. Cause: ", e);
                    throw new RuntimeException(e);
                }
            }
        }
        // 将 ContainerClassLoader hack 到 PathClassloader 之上
        try {
            hackContainerClassLoader(containerKey, containerApk);
            if (mLogger.isInfoEnabled()) {
                mLogger.info("containerApk 插入成功, containerKey = " + containerKey + ", path = " + containerApk.mApkFilePath);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    private static ContainerClassLoader findContainerClassLoader(
            String containerKey
    ) {
        ClassLoader current = MultiDynamicContainer.class.getClassLoader();
        ClassLoader parent = current.getParent();
        while (parent != null) {
            if (parent instanceof ContainerClassLoader) {
                ContainerClassLoader item = (ContainerClassLoader) parent;
                if (TextUtils.equals(item.containerKey, containerKey)) {
                    return item;
                }
            }
            parent = parent.getParent();
        }
        return null;
    }

    private static void removeContainerClassLoader(
            ContainerClassLoader containerClassLoader
    ) throws Exception {
        ClassLoader pathClassLoader = MultiDynamicContainer.class.getClassLoader();
        ClassLoader child = pathClassLoader;
        ClassLoader parent = pathClassLoader.getParent();
        while (parent != null) {
            if (parent == containerClassLoader) {
                break;
            }
            child = parent;
            parent = parent.getParent();
        }
        if (child != null && parent == containerClassLoader) {
            DynamicRuntime.hackParentClassLoader(child, containerClassLoader.getParent());
        }
    }

    private static void hackContainerClassLoader(
            String containerKey,
            InstalledApk containerApk
    ) throws Exception {
        ClassLoader pathClassLoader = MultiDynamicContainer.class.getClassLoader();
        ContainerClassLoader containerClassLoader = new ContainerClassLoader(containerKey, containerApk, pathClassLoader.getParent());
        DynamicRuntime.hackParentClassLoader(pathClassLoader, containerClassLoader);
    }

    private static class ContainerClassLoader extends BaseDexClassLoader {
        private String apkFilePath;
        private String containerKey;

        public ContainerClassLoader(
                String containerKey,
                InstalledApk installedApk,
                ClassLoader parent
        ) {
            super(
                    installedApk.mApkFilePath,
                    installedApk.odexPath != null ? new File(installedApk.odexPath) : null,
                    installedApk.libraryPath,
                    parent
            );

            this.containerKey = containerKey;
            this.apkFilePath = installedApk.mApkFilePath;
        }
    }

}
