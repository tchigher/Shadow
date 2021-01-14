# Sample

在 Shadow 框架下，应用由几部分构成。
宿主应用打包了很简单的一些接口，并在 Manifest 中注册了壳子代理组件，
还打包了插件管理器(manager)的动态升级逻辑。
manager负责下载、安装插件，还带有一个动态的View表达Loading态。
而"插件"则不光包含业务App，还包含Shadow的核心实现，即loader和runtime。
"插件"中的业务App和loader、runtime是同一个版本的代码编译出的，
因此loader可以包含一些业务逻辑，针对业务进行特殊处理。
由于loader是多实例的，因此同一个宿主中可以有多种不同的loader实现。
manager在加载"插件"时，首先需要先加载"插件"中的runtime和loader，
再通过loader的Binder（插件应该处于独立进程中避免native库冲突）操作loader进而加载业务App。

在这个Sample目录下，提供了两种示例工程：

## 源码依赖SDK的Sample(`projects/sample/source`)
***
要测试这个Sample请用Android Studio直接打开clone版本库的根目录。
***

* `sample-host`是宿主应用
* `sample-manager`是插件管理器的动态实现
* `sample-plugin/sample-loader`是loader的动态实现，业务主要在这里定义插件组件和壳子代理组件的配对关系等。
* `sample-constant`是在前3者中共用的相同字符串常量。
* `sample-plugin/sample-runtime`是runtime的动态实现，业务主要在这里定义壳子代理组件的实际类。
* `sample-plugin/sample-app-lib`是业务App的主要代码，是一个aar库。
* `sample-plugin/sample-normal-app`是一个apk模块壳子，将`sample-app-lib`打包在其中，演示业务App是可以正常安装运行的。
* `sample-plugin/sample-plugin-app`也是一个apk模块壳子，同样将`sample-app-lib`打包在其中，但是额外应用了Shadow插件，生成的apk不能正常安装运行。

这些工程中对Shadow SDK的依赖完全是源码级的依赖，因此修改Shadow SDK的源码后可以直接运行生效。

使用时可以直接在Android Studio中选择运行`sample-host`模块。
`sample-host`在构建中会自动打包manager和"插件"到assets中，在运行时自动释放模拟下载过程。
`sample-plugin`里的`sample-normal-apk`模块也可以直接安装运行，演示不使用Shadow时插件的运行情况。

