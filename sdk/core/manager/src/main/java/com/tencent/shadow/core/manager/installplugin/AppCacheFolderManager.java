package com.tencent.shadow.core.manager.installplugin;

import java.io.File;

/**
 * 目录各模块的目录关系管理
 */
public class AppCacheFolderManager {

    public static File getVersionedPluginsUnpackDirInNamedPluginsManagersDir(
            File pluginsUnpackDir,
            String mPluginsManagerName,
            String pluginsZipFileHash
    ) {
        return new File(
                getNamedPluginsManagerDirInPluginsUnpackDir(pluginsUnpackDir, mPluginsManagerName),
                pluginsZipFileHash
        );
    }

    public static File getNamedPluginsManagerDirInPluginsUnpackDir(
            File pluginsUnpackDir,
            String pluginsManagerName
    ) {
        return new File(pluginsUnpackDir, pluginsManagerName);
    }


    public static File getOdexDir(
            File root,
            String key
    ) {
        return new File(getOdexRootDir(root), key + "_odex");
    }

    public static File getOdexCopiedTagFile(
            File odexDir,
            String pluginAppPartKey
    ) {
        return new File(odexDir, pluginAppPartKey + "_odexed");
    }


    private static File getOdexRootDir(
            File root
    ) {
        return new File(root, "oDex");
    }

    public static File getLibDir(
            File root,
            String key
    ) {
        return new File(getLibRootDir(root), key + "_lib");
    }

    public static File getLibCopiedFile(
            File soDir,
            String key
    ) {
        return new File(soDir, key + "_copied");
    }


    private static File getLibRootDir(
            File root
    ) {
        return new File(root, "lib");
    }

}
