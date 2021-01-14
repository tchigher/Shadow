# Shadow

[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)

## 介绍
Shadow 是一个腾讯自主研发的 Android 插件框架, 经过线上亿级用户量の检验. 
Shadow 不仅开源分享了插件技术の关键代码, 还完整地分享了上线部署所需要の所有设计. 

与其它の插件框架相比, Shadow 主要具有以下特点：

* **复用可独立安装的 App 的源码**：插件 App 的源码原本就是可以正常安装和运行的. 
* **零反射无 Hack 实现插件技术**：从理论上就已经确定无需对任何系统做兼容开发, 更无任何隐藏的 API 调用, 和 Google 限制非公开 SDK 接口の访问策略完全不冲突. 
* **全动态の插件框架**：一次性实现完美の插件框架很难, 但 Shadow 将这些实现全部动态化起来, 使插件框架の代码成为了插件の一部分. 插件の迭代不再受宿主打包了旧版本の插件框架所限制. 
* **宿主增量极小**：得益于全动态の实现, 真正合入宿主程序の代码量极小 (约大 15 KB, 约加 160 个方法). 
* **使用 Kotlin 实现**：core.loader, core.transform 的核心代码完全用 Kotlin 实现, 代码简洁易维护. 

### 支持特性
* 四大组件
* Fragment (代码添加和 XML 添加)
* DataBinding (无需特别支持, 但已验证可正常运行)
* 跨进程使用插件 Service
* 自定义 Theme
* 插件访问宿主类
* So 加载
* 分段加载插件 (多 APK 分别加载, 或多 APK 依次依赖加载)
* 一个 Activity 中加载多个 APK 中的 View
* 等等……

## 编译与开发环境

### 环境准备
第一次 `clone` Shadow 的代码到本地后, 建议先在命令行编译一次. 

* 在编译前, **必须**设置 `ANDROID_HOME` 环境变量. 
* 在编译时, **必须**使用 `gradlew` 脚本, 以保证使用了项目配置的 Gradle 版本. 

在命令行测试编译时可以执行这个任务：
```
./gradlew build
```

如果没有出错, 再使用 Android Studio 打开工程. 

* **必须**使用 Android Studio **3.5+** 打开工程. (业务插件开发时没有限制)
* **必须**关闭 Android Studio 的 **Instant Run** 功能. 

然后就可以在 IDE 中选择 `sample-host` 模块直接运行了. 

Shadowの所有代码都位于 `projects` 目录下の 3 个目录, 分别是：

* `sdk` 包含 SDK 的所有代码
* `sample` 包含演示代码

其中 `sample` 是体验 Shadow 的最佳环境. 
详见 `sample` 目录中的 [README](projects/sample/README.md) 介绍. 

## 自己写の测试代码出错？
插件框架是不可能一步到位、完美实现的. 因此, 大部分业务在接入时都是需要一定の二次开发工作.

得益于全动态の设计, 插件框架和插件本身都是动态发布的,<br>
插件包里既有插件代码也有插件框架代码, 所以可以根据新版本插件の需要同时开发插件框架. 

例如, ShadowActivity 没有实现全所有 Activity 的方法, 而你写の测试代码可能用到了, 就会出现 Method Not Found 的错误,<br>
只需要在 ShadowActivity 中实现对应方法就可以了, 大部分方法の实现都只是需要简单の转调就能工作正常. 

## 后续开发
* 原理与设计说明文档
* 多插件支持の演示工程
* 开源包含下载能力的 manager 实现

