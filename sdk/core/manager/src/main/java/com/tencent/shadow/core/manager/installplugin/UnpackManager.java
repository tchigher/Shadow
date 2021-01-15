package com.tencent.shadow.core.manager.installplugin;

import android.support.annotation.Nullable;

import com.tencent.shadow.core.common.Logger;
import com.tencent.shadow.core.common.LoggerFactory;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class UnpackManager {

    private static final Logger mLogger = LoggerFactory.getLogger(UnpackManager.class);

    private static final String UNPACK_DONE_PREFIX = "unpacked.";
    private static final String CONFIG_JSON_FILE_NAME = "config.json"; // TODO #28 config.json 的格式需要沉淀文档
    private static final String DEFAULT_STORE_DIR_NAME = "ShadowPluginManager";

    private final File mPluginsUnpackedDir;

    private final String mPluginsManagerName;

    public UnpackManager(
            File appFilesDir,
            String pluginsManagerName
    ) {
        File pluginsManagerDir = new File(appFilesDir, DEFAULT_STORE_DIR_NAME);
        mPluginsUnpackedDir = new File(pluginsManagerDir, "UnpackedPlugins");
        mPluginsUnpackedDir.mkdirs();
        mPluginsManagerName = pluginsManagerName;
    }


    File getVersionDir(String pluginsZipFileHash) {
        return AppCacheFolderManager.getVersionedPluginsUnpackDirInNamedPluginsManagersDir(mPluginsUnpackedDir, mPluginsManagerName, pluginsZipFileHash);
    }

    public File getNamedPluginsManagerDirInPluginsUnpackDir() {
        return AppCacheFolderManager.getNamedPluginsManagerDirInPluginsUnpackDir(mPluginsUnpackedDir, mPluginsManagerName);
    }

    /**
     * 获取插件解包的目标目录. 根据 target 的文件名决定。
     *
     * @param pluginsZipFile Target
     * @return 插件解包的目标目录
     */
    File getPluginsUnpackDir(
            String pluginsZipFileHash,
            File pluginsZipFile
    ) {
        return new File(
                getVersionDir(pluginsZipFileHash),
                pluginsZipFile.getName()
        );
    }

    /**
     * 解包一个下载好的插件
     *
     * @param pluginsZipFileHash 插件包的hash
     * @param pluginsZipFile     插件包
     */
    public PluginsConfig unpackPlugins(
            @Nullable String pluginsZipFileHash,
            File pluginsZipFile
    ) throws IOException, JSONException {
        if (pluginsZipFileHash == null) {
            pluginsZipFileHash = MinFileUtils.getMD5(pluginsZipFile);
        }

        File pluginsUnpackDir = getPluginsUnpackDir(pluginsZipFileHash, pluginsZipFile);
        pluginsUnpackDir.mkdirs();

        File pluginsUnpackDoneDir = getUnpackedDoneDir(pluginsUnpackDir);

        if (pluginsUnpackDoneDir.exists()) {
            try {
                return getUnpackedPluginsConfig(pluginsUnpackDir);
            } catch (Exception e) {
                if (!pluginsUnpackDoneDir.delete()) {
                    throw new IOException("解析版本信息失败, 且无法删除标记: " + pluginsUnpackDoneDir.getAbsolutePath());
                }
            }
        }

        MinFileUtils.cleanDirectory(pluginsUnpackDir);

        ZipFile safePluginsZipFile = null;
        try {
            safePluginsZipFile = new SafeZipFile(pluginsZipFile);
            Enumeration<? extends ZipEntry> zipEntries = safePluginsZipFile.entries();
            while (zipEntries.hasMoreElements()) {
                ZipEntry zipEntry = zipEntries.nextElement();
                if (!zipEntry.isDirectory()) {
                    MinFileUtils.writeOutZipEntry(
                            safePluginsZipFile,
                            zipEntry,
                            pluginsUnpackDir,
                            zipEntry.getName()
                    );
                }
            }

            PluginsConfig pluginsConfig = getUnpackedPluginsConfig(pluginsUnpackDir);

            // 创建完成标记目录
            pluginsUnpackDoneDir.createNewFile();

            return pluginsConfig;
        } finally {
            try {
                if (safePluginsZipFile != null) {
                    safePluginsZipFile.close();
                }
            } catch (IOException e) {
                mLogger.warn("zip 关闭时出错忽略", e);
            }
        }
    }

    File getUnpackedDoneDir(
            File pluginUnpackDir
    ) {
        return new File(
                pluginUnpackDir.getParentFile(),
                UNPACK_DONE_PREFIX + pluginUnpackDir.getName()
        );
    }

    PluginsConfig getUnpackedPluginsConfig(
            File pluginsUnpackDir
    ) throws IOException, JSONException {
        File configJsonFile = new File(pluginsUnpackDir, CONFIG_JSON_FILE_NAME);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(configJsonFile)));
        StringBuilder stringBuilder = new StringBuilder("");
        String lineStr;
        try {
            while ((lineStr = bufferedReader.readLine()) != null) {
                stringBuilder.append(lineStr).append("\n");
            }
        } finally {
            bufferedReader.close();
        }
        String versionedJsonStr = stringBuilder.toString();

        return PluginsConfig.parseFromJson(versionedJsonStr, pluginsUnpackDir);
    }

}
