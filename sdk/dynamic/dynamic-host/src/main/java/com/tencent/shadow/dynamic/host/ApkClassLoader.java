package com.tencent.shadow.dynamic.host;

import android.os.Build;

import com.tencent.shadow.core.common.InstalledApk;

import dalvik.system.DexClassLoader;

/**
 * APK 插件加载专用 ClassLoader
 * <p>
 * 将宿主 APK 和插件 APK 隔离,
 * 但例外的是, 插件可以从宿主 APK 中加载到约定的接口.
 * 这样隔离的目的是让宿主 APK 中的类可以通过约定的接口使用插件 APK 中的实现,
 * 而插件中的类不会使用到和宿主同名的类.
 * <p>
 * 如果目标类符合构造时传入的包名, 则从 parent ClassLoader 中查找, 否则先从自己的 dexPath 中查找,
 * 如果找不到, 再从 parent 的 parent ClassLoader 中查找.
 */
class ApkClassLoader
        extends DexClassLoader {

    private final ClassLoader mGrandParentClassLoader;
    private final String[] mInterfacePackageNames;

    ApkClassLoader(
            InstalledApk installedApk,
            ClassLoader parentClassLoader,
            String[] interfacePackageNames,
            int grandTimes
    ) {
        super(
                installedApk.mApkFilePath,
                installedApk.odexPath,
                installedApk.libraryPath,
                parentClassLoader
        );

        ClassLoader tempClassLoader = parentClassLoader;
        for (int i = 0; i < grandTimes; i++) {
            tempClassLoader = tempClassLoader.getParent();
        }
        mGrandParentClassLoader = tempClassLoader;

        mInterfacePackageNames = interfacePackageNames;
    }

    @Override
    protected Class<?> loadClass(
            String className,
            boolean resolve
    ) throws ClassNotFoundException {
        String packageName;
        int dot = className.lastIndexOf('.');
        if (dot != -1) {
            packageName = className.substring(0, dot);
        } else {
            packageName = "";
        }

        boolean isClassInHostInterfacePackage = false;
        for (String interfacePackageName : mInterfacePackageNames) {
            if (packageName.equals(interfacePackageName)) {
                isClassInHostInterfacePackage = true;
                break;
            }
        }

        if (isClassInHostInterfacePackage) {
            return super.loadClass(className, resolve);
        } else {
            Class<?> clazz = findLoadedClass(className);

            if (clazz == null) {
                ClassNotFoundException classNotFoundException = null;
                try {
                    clazz = findClass(className);
                } catch (ClassNotFoundException e) {
                    classNotFoundException = e;
                }

                if (clazz == null) {
                    try {
                        clazz = mGrandParentClassLoader.loadClass(className);
                    } catch (ClassNotFoundException e) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            e.addSuppressed(classNotFoundException);
                        }
                        throw e;
                    }
                }
            }

            return clazz;
        }
    }

    /**
     * 从 apk 中读取接口的实现
     *
     * @param clazz     接口类
     * @param className 实现类的类名
     * @param <T>       接口类型
     * @return 所需接口
     */
    <T> T getInterface(
            Class<T> clazz,
            String className
    ) throws Exception {
        try {
            Class<?> interfaceImplementClass = loadClass(className);
            Object interfaceImplement = interfaceImplementClass.newInstance();
            return clazz.cast(interfaceImplement);
        } catch (ClassNotFoundException | InstantiationException
                | ClassCastException | IllegalAccessException e) {
            throw new Exception(e);
        }
    }

}

