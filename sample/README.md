# Sample

在 Shadow 框架下, 应用由几部分构成. 
宿主应用打包了很简单の一些接口, 并在 Manifest 中注册了壳子代理组件, 
还打包了插件管理器 (manager) 的动态升级逻辑. 
manager 负责下载、安装插件, 还带有一个动态的 View 表达 Loading 态. 
而"插件"则不光包含业务 App, 还包含 Shadow 的核心实现, 即 loader 和 runtime. 
"插件"中の业务 App 和 loader、runtime 是同一个版本の代码编译出的, 
因此 loader 可以包含一些业务逻辑, 针对业务进行特殊处理. 
由于 loader 是多实例的, 因此同一个宿主中可以有多种不同的 loader 实现. 
manager 在加载"插件"时, 首先需要先加载"插件"中的 runtime 和 loader, 
再通过 loader 的 Binder (插件应该处于独立进程中避免 native 库冲突) 操作 loader 进而加载业务 App. 

在这个 sample 目录下, 提供了示例工程: 

## 源码依赖 SDK 的 sample(`sample`)
***
要测试这个 sample 请用 Android Studio 直接打开 clone 版本库の根目录. 
***

* `sample-host` 是宿主应用
* `sample-manager` 是插件管理器の动态实现
* `sample-plugin/sample-loader` 是 loader 的动态实现, 业务主要在这里定义插件组件和壳子代理组件の配对关系等. 
* `sample-constant` 是在前 3 者中共用の相同字符串常量. 
* `sample-plugin/sample-runtime` 是 runtime 的动态实现, 业务主要在这里定义壳子代理组件の实际类. 
* `sample-plugin/sample-app-lib` 是业务 App 的主要代码, 是一个 AAR 库. 
* `sample-plugin/sample-normal-app` 是一个 APK 模块壳子, 将 `sample-app-lib` 打包在其中, 演示业务 App 是可以正常安装运行的. 
* `sample-plugin/sample-plugin-app` 也是一个 APK 模块壳子, 同样将 `sample-app-lib` 打包在其中, 但是额外应用了 Shadow 插件, 生成的 APK 不能正常安装运行. 

这些工程中对Shadow SDK 的依赖完全是源码级の依赖, 因此修改 Shadow SDK 的源码后可以直接运行生效. 

使用时可以直接在 Android Studio 中选择运行 `sample-host` 模块. 
`sample-host` 在构建中会自动打包 manager 和"插件"到 assets 中, 在运行时自动释放模拟下载过程. 
`sample-plugin` 里的 `sample-normal-apk` 模块也可以直接安装运行, 演示不使用 Shadow 时插件の运行情况. 

