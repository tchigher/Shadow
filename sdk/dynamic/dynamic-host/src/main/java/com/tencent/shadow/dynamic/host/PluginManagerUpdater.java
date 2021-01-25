package com.tencent.shadow.dynamic.host;

import android.support.annotation.Nullable;

import java.io.File;
import java.util.concurrent.Future;

/**
 * PluginManager 文件升级器
 * <p>
 * 注意这个类不负责什么时候升级 PluginManager
 * 它只提供需要升级时の功能
 */
public interface PluginManagerUpdater {

    /**
     * 获取本地最新可用的
     *
     * @return null 表示本地没有可用的
     */
    File getLocalLatestApk();

    /**
     * @return true 表示之前の更新过程意外中断了
     */
    default boolean wasUpdatingInterrupted() {
        // do nothing
        return false;
    }

    /**
     * 执行更新
     *
     * @return 当前最新的 PluginManager, 可能是之前已经返回过の文件, 但它已最新
     */
    @Nullable
    default Future<File> update() {
        // do nothing
        return null;
    }

}
