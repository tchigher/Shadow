package com.tencent.shadow.core.manager.installplugin;

import android.support.annotation.NonNull;
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

public class PluginManager {

    private static final Logger mLogger = LoggerFactory.getLogger(PluginManager.class);

    private static final String PLUGIN_UNPACK_DONE_PREFIX = "UNPACKED_";
    private static final String PLUGIN_CONFIG_JSON_FILE_NAME = "config.json"; // TODO #28 config.json 的格式需要沉淀文档
    private static final String SHADOW_BASE_DIR_NAME = "shadow";

    private final File mPluginManagersDir;

    private final String mPluginManagerName;

    public PluginManager(
            File appFilesDir,
            String pluginManagerName
    ) {
        File shadowBaseDir = new File(appFilesDir, SHADOW_BASE_DIR_NAME);
        mPluginManagersDir = new File(shadowBaseDir, "pluginManagers");
        mPluginManagersDir.mkdirs();

        mPluginManagerName = pluginManagerName;
    }

    public File createPluginManagerDir() {
        return AppCacheFolderManager.createPluginManagerDir(
                mPluginManagersDir,
                mPluginManagerName
        );
    }

    File createPluginBaseDir(
            String pluginZipFileHash
    ) {
        return AppCacheFolderManager.createPluginBaseDir(
                mPluginManagersDir,
                mPluginManagerName,
                pluginZipFileHash
        );
    }

    /**
     * 获取插件解包的目标目录. 根据 target 的文件名决定。
     *
     * @param pluginZipFile Target
     * @return 插件解包的目标目录
     */
    File createPluginUnpackDir(
            String pluginZipFileHash,
            File pluginZipFile
    ) {
        return new File(
                createPluginBaseDir(pluginZipFileHash),
                pluginZipFile.getName().replace(".zip", "")
        );
    }

    /**
     * 解包一个下载好的插件
     *
     * @param pluginZipFileHash 插件包的hash
     * @param pluginZipFile     插件包
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public PluginConfig unpackPlugin(
            @Nullable String pluginZipFileHash,
            @NonNull File pluginZipFile
    ) throws IOException, JSONException {
        if (pluginZipFileHash == null) {
            pluginZipFileHash = MinFileUtils.getMD5(pluginZipFile);
        }

        File pluginUnpackDir = createPluginUnpackDir(pluginZipFileHash, pluginZipFile);
        pluginUnpackDir.mkdirs();

        File pluginUnpackDoneDir = createPluginUnpackDoneDir(pluginUnpackDir);

        if (pluginUnpackDoneDir.exists()) {
            try {
                return generatePluginConfig(pluginUnpackDir);
            } catch (Exception e) {
                if (!pluginUnpackDoneDir.delete()) {
                    throw new IOException("解析版本信息失败, 且无法删除标记: " + pluginUnpackDoneDir.getAbsolutePath());
                }
            }
        }

        MinFileUtils.cleanDir(pluginUnpackDir);

        ZipFile safePluginZipFile = null;
        try {
            safePluginZipFile = new SafeZipFile(pluginZipFile);
            Enumeration<? extends ZipEntry> zipEntries = safePluginZipFile.entries();
            while (zipEntries.hasMoreElements()) {
                ZipEntry zipEntry = zipEntries.nextElement();
                if (!zipEntry.isDirectory()) {
                    MinFileUtils.writeOutZipEntry(
                            safePluginZipFile,
                            zipEntry,
                            pluginUnpackDir,
                            zipEntry.getName()
                    );
                }
            }

            PluginConfig pluginConfig = generatePluginConfig(pluginUnpackDir);

            // 创建完成标记目录
            pluginUnpackDoneDir.createNewFile();

            return pluginConfig;
        } finally {
            try {
                if (safePluginZipFile != null) {
                    safePluginZipFile.close();
                }
            } catch (IOException e) {
                mLogger.warn("ZIP 关闭出错(忽略): ", e);
            }
        }
    }

    File createPluginUnpackDoneDir(
            File pluginUnzipDir
    ) {
        return new File(
                pluginUnzipDir.getParentFile(),
                PLUGIN_UNPACK_DONE_PREFIX + pluginUnzipDir.getName().replace(".zip", "")
        );
    }

    PluginConfig generatePluginConfig(
            File pluginUnzipDir
    ) throws IOException, JSONException {
        File configJsonFile = new File(pluginUnzipDir, PLUGIN_CONFIG_JSON_FILE_NAME);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(configJsonFile)));
        StringBuilder stringBuilder = new StringBuilder();
        String lineStr;
        try {
            while ((lineStr = bufferedReader.readLine()) != null) {
                stringBuilder.append(lineStr).append("\n");
            }
        } finally {
            bufferedReader.close();
        }
        String configJsonStr = stringBuilder.toString();

        return PluginConfig.parseFromJson(configJsonStr, pluginUnzipDir);
    }

}
