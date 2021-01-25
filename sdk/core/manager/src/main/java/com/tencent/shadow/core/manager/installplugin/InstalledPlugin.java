package com.tencent.shadow.core.manager.installplugin;


import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 已安装好的插件
 * <p>
 * 这是一个 Serializable 类, 目的是可以将这个类的对象放在 Intent 中跨进程传递
 * 注意: equals() 方法必须重载, 并包含全部域变量
 */
public class InstalledPlugin
        implements Serializable {

    /*
     * 标识一次插件发布的 id
     */
    public String UUID;

    /*
     * 标识一次插件发布的 id, 可以使用自定义格式描述版本信息
     */
    public String UUID_NickName;

    /*
     * pluginLoader 文件
     */
    public Part pluginLoaderFile;

    /*
     * runtime 文件
     */
    public Part runtimeFile;

    /*
     * 插件文件
     */
    public Map<String, PluginPart> plugins = new HashMap<>();

    InstalledPlugin(
    ) {
    }

    public boolean hasPart(
            String partKey
    ) {
        return plugins.containsKey(partKey);
    }

    public PluginPart getPlugin(
            String partKey
    ) {
        return plugins.get(partKey);
    }

    public Part getPart(
            String partKey
    ) {
        return plugins.get(partKey);
    }

    static public class Part
            implements Serializable {

        final public int pluginType;
        final public File pluginFile;
        public File odexDir;
        public File libraryDir;

        Part(
                int pluginType,
                File file,
                File odexDir,
                File libraryDir
        ) {
            this.pluginType = pluginType;
            this.odexDir = odexDir;
            this.libraryDir = libraryDir;
            this.pluginFile = file;
        }

    }

    static public class PluginPart extends Part {

        final public String businessName;
        final public String[] dependsOn;
        final public String[] hostWhiteList;

        PluginPart(
                int pluginType,
                String businessName,
                File file,
                File odexDir,
                File libraryDir,
                String[] dependsOn,
                String[] hostWhiteList
        ) {
            super(
                    pluginType,
                    file,
                    odexDir,
                    libraryDir
            );

            this.businessName = businessName;
            this.dependsOn = dependsOn;
            this.hostWhiteList = hostWhiteList;
        }

    }

}
