apply(plugin = "plugins.ktlint")

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
}

kapt {
    correctErrorTypes = true

    javacOptions {
        option("-Xmaxerrs", 1000)
    }

    arguments {
        arg("AROUTER_MODULE_NAME", project.name)
    }
}

android {
    signingConfigs {
        getByName("debug") {
            keyAlias = "alias_name"
            keyPassword = "cmvideo8"
            storeFile = file("../mgmovie/app/cmcc-shmc.keystore")
            storePassword = "cmvideo8"
        }

        create("release") {
            keyAlias = "alias_name"
            keyPassword = "cmvideo8"
            storeFile = file("../mgmovie/app/cmcc-shmc.keystore")
            storePassword = "cmvideo8"
        }
    }

    compileSdkVersion(Versions.compileSdk)
    buildToolsVersion(Versions.buildTools)

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        freeCompilerArgs = listOf("-XXLanguage:+InlineClasses")
    }

    dexOptions {
        jumboMode = true
    }

    defaultConfig {
        applicationId = "com.cmvideo.migumovie"

        minSdkVersion(Versions.minSdk)
        targetSdkVersion(Versions.targetSdk)

        multiDexKeepFile = file("../mgmovie/app/multidex-config.txt")
        multiDexKeepProguard = file("../mgmovie/appmultidexKeep.pro")

        versionCode = 65000025
        versionName = "5.0.18"

        ndk {
            abiFilters("armeabi-v7a")
        }

        manifestPlaceholders = mapOf(
                "GETUI_APP_ID" to "FkGwUbSpdI9lEvo7oNXUF8",
                "GETUI_APP_KEY" to "HsxSXJsNhm8dXVKHXxRX95",
                "GETUI_APP_SECRET" to "Xm47uUKg8b7H3EDl3DNyl7",

                "HUAWEI_APP_ID" to "10364392",

                "MEIZU_APP_ID" to "111004",
                "MEIZU_APP_KEY" to "ea80d41b49b24497a189fcb3fdb99c7b",

                "XIAOMI_APP_ID" to "2882303761517399778",
                "XIAOMI_APP_KEY" to "5201739936778",

                "OPPO_APP_KEY" to "6k1f3PiQfpooCo4s8O4C48oO0",
                "OPPO_APP_SECRET" to "94e5abf30263Ad288ce10EbaedE60b40",

                "VIVO_APP_ID" to "10718",
                "VIVO_APP_KEY" to "82601a2a-9a20-42da-9802-97aa6815a21f"
        )

        multiDexEnabled = true

        javaCompileOptions {
            annotationProcessorOptions {
                includeCompileClasspath = true
            }
        }

        vectorDrawables.useSupportLibrary = true

        viewBinding.isEnabled = true
    }

    buildTypes {
        val isLoginDebug: String by project

        getByName("debug") {
            isDebuggable = true
            signingConfig = signingConfigs.getByName("debug")

            buildConfigField("boolean", "LOGIN_DEBUG", "${isLoginDebug.toBoolean()}")
            val buglyAppIdDev: String by project
            buildConfigField("String", "BUGLY_APP_ID", "\"$buglyAppIdDev\"")

            manifestPlaceholders = mapOf(
                    "HW_APP_ID" to "10364392",
                    "MZPUSH_ID" to "111004",
                    "MZPUSH_KEY" to "ea80d41b49b24497a189fcb3fdb99c7b",
                    "MIPUSH_ID" to "2882303761517399778",
                    "MIPUSH_KEY" to "5201739936778",
                    "OPUSH_KEY" to "6k1f3PiQfpooCo4s8O4C48oO0",
                    "OPUSH_SECRET" to "94e5abf30263Ad288ce10EbaedE60b40",
                    "VIVO_APP_ID" to "10718",
                    "VIVO_APP_KEY" to "82601a2a-9a20-42da-9802-97aa6815a21f"
            )
        }

        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = false
            isZipAlignEnabled = true
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("release")

            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")

            buildConfigField("boolean", "LOGIN_DEBUG", "${isLoginDebug.toBoolean()}")
            val buglyAppId: String by project
            buildConfigField("String", "BUGLY_APP_ID", "\"$buglyAppId\"")

            manifestPlaceholders = mapOf(
                    "HW_APP_ID" to "10364392",
                    "MZPUSH_ID" to "111004",
                    "MZPUSH_KEY" to "ea80d41b49b24497a189fcb3fdb99c7b",
                    "MIPUSH_ID" to "2882303761517399778",
                    "MIPUSH_KEY" to "5201739936778",
                    "OPUSH_KEY" to "6k1f3PiQfpooCo4s8O4C48oO0",
                    "OPUSH_SECRET" to "94e5abf30263Ad288ce10EbaedE60b40",
                    "VIVO_APP_ID" to "10718",
                    "VIVO_APP_KEY" to "82601a2a-9a20-42da-9802-97aa6815a21f"
            )
        }

    }

    packagingOptions {
        exclude("META-INF/main.kotlin_module")
        doNotStrip("**/*.so")
    }

    dataBinding {
        isEnabled = true
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    implementation("com.alibaba:arouter-api:${Versions.arouterApi}")
    kapt("com.alibaba:arouter-compiler:${Versions.arouterCompiler}")
    implementation("com.jakewharton:butterknife:9.0.0")
    kapt("com.jakewharton:butterknife-compiler:9.0.0")
    implementation("io.objectbox:objectbox-android:${Versions.objectBox}")
    implementation("io.objectbox:objectbox-kotlin:${Versions.objectBox}")
    kapt("io.objectbox:objectbox-processor:${Versions.objectBox}")
    implementation("com.android.support.constraint:constraint-layout:1.1.3")
    implementation("com.android.support:design:${Versions.appCompat}")
    implementation("com.android.support:animated-vector-drawable:${Versions.appCompat}")

    implementation("com.github.promeg:tinypinyin:2.0.3")
    implementation("com.github.promeg:tinypinyin-lexicons-android-cncity:2.0.3")
    implementation("com.scwang.smartrefresh:SmartRefreshLayout:1.1.0")

    implementation(project(":Cores:libbase"))
    implementation(project(":Cores:IService"))
    implementation(project(":Cores:IDataService"))
    implementation(project(":Cores:BnModelModule"))
    implementation(project(":Cores:BnUIModule"))
    implementation(project(":Cores:IMapService"))
    implementation(project(":Cores:IMoviePlayer"))
    implementation(project(":Edges:IloginService"))
    implementation(project(":Edges:MgPushLib"))

    implementation("com.facebook.stetho:stetho:1.5.0")
    implementation("com.facebook.stetho:stetho-okhttp3:1.5.0")

    implementation(project(":Tools:annotationprocess"))

    implementation(project(":Widgets:libmatisse"))
    implementation(project(":Widgets:SwipePanel"))
    implementation(project(":Edges:libanalytics"))

    implementation(project(":Widgets:DropDownMenu"))
    implementation(project(":Widgets:TagLayout"))
    implementation(project(":Widgets:SmartShow"))
    implementation("com.mg.widgets:Dashline:1.0.1")
    implementation(project(":Widgets:StatusView"))
    implementation(project(":Cores:filedownloader"))
    implementation(project(":Widgets:starscoreview"))
    implementation(project(":Widgets:expandabletextview"))
    implementation(project(":Edges:IADService"))
    implementation(project(":Edges:AILib"))
    implementation(project(":Edges:IScannerService"))
    implementation(project(":Widgets:SlideUp"))
    implementation("com.android.support:multidex:1.0.3")
    implementation("com.yanzhenjie:permission:2.0.0-rc12")
    implementation("com.github.CymChad:BaseRecyclerViewAdapterHelper:2.9.42")
    debugImplementation("com.squareup.leakcanary:leakcanary-android:1.6.3")
    debugImplementation("com.squareup.leakcanary:leakcanary-support-fragment:1.6.3")
    releaseImplementation("com.squareup.leakcanary:leakcanary-android-no-op:1.6.3")
    implementation("com.android.support:cardview-v7:${Versions.appCompat}")
    implementation("me.relex:photodraweeview:1.1.3")
    implementation("com.github.lihangleo2:ShadowLayout:2.0.1")
    implementation("com.oushangfeng:PinnedSectionItemDecoration:1.3.2")

    implementation("com.tencent.bugly:crashreport:3.0.0")
    implementation("com.tencent.bugly:nativecrashreport:3.6.0.1")
    implementation("com.google.zxing:core:3.3.3")
    implementation("com.umeng.umsdk:analytics:8.0.0")
    implementation("com.umeng.umsdk:common:2.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.2.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.1.1")

    implementation("com.squareup.retrofit2:retrofit:${Versions.retrofit}")
    implementation("com.squareup.retrofit2:converter-moshi:${Versions.retrofit}")
    implementation("com.squareup.retrofit2:converter-scalars:${Versions.retrofit}") {
        exclude(group = "com.squareup.okhttp3")
    }

    implementation("com.squareup.okhttp3:logging-interceptor:${Versions.okHttp}")
    implementation("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2")

    implementation("org.jetbrains.anko:anko:0.10.8")
    debugImplementation("me.ele:uetool:1.0.17")
    debugImplementation("me.ele:uetool-fresco:1.0.17")

    val specialForUI: String by project
    if (specialForUI == "true") {
        releaseImplementation("me.ele:uetool:1.0.17")
        releaseImplementation("me.ele:uetool-fresco:1.0.17")
    } else {
        releaseImplementation("me.ele:uetool-no-op:1.0.17")
    }

    implementation("android.arch.lifecycle:extensions:1.1.1")
    implementation("jp.wasabeef:glide-transformations:4.0.1")
    implementation("cn.bingoogolapple:bga-qrcode-zxing:1.3.6")

    debugImplementation("com.github.brianPlummer:tinydancer:0.1.2")
    releaseImplementation("com.github.brianPlummer:tinydancer-noop:0.1.2")

    implementation("com.github.salomonbrys.kotson:kotson:2.5.0")
    implementation("tm.charlie.androidlib:expandable-textview:2.0.2")
    implementation("com.facebook.fresco:imagepipeline-okhttp3:1.13.0")

    kapt("com.google.dagger:dagger-compiler:2.27")
    kapt("com.google.dagger:dagger-android-processor:2.27")
}

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "com.android.support") {
            if (!requested.name.startsWith("multidex")) {
                useVersion(Versions.appCompat)
            }
        }

        if (requested.group == "com.squareup.okhttp3") {
            useVersion("3.12.0")
        }
        if (requested.group == "com.cmcc.base") {
            useVersion("1.1.3")
        }
    }
}

// .................................................................................................

android {
    defaultConfig {
//        applicationId = project.ext.get("SAMPLE_HOST_APP_APPLICATION_ID").toString()
//        applicationId = "com.cmvideo.migumovie"
        applicationId = "com.tencent.shadow.sample.host"
    }
}

dependencies {
    // Shadow Transform 后业务代码会有一部分实际引用 runtime 中的类
    // 如果不以 compileOnly 方式依赖，会导致其他 Transform 或者 Proguard 找不到这些类
    compileOnly("com.tencent.shadow.core:runtime-debug")
}

buildscript {
    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath("com.tencent.shadow.core:runtime-debug")
        classpath("com.tencent.shadow.core:activity-container-debug")
        classpath("com.tencent.shadow.core:gradle-plugin")
        classpath("org.javassist:javassist:3.22.0-GA")
    }
}

//fun createDuplicateApkTask(buildType: String): Task {
//    val apkDir = file("${buildDir}/outputs/apk/$buildType")
//
//    return tasks.create<Copy>("duplicate${buildType.capitalize()}ApkTask") {
//        group = "build"
//        description = "复制一个 mgmovie-plugin-app-${buildType}.apk 用于测试"
//        from(apkDir) {
//            include("mgmovie-plugin-app-${buildType}.apk")
//            rename { "mgmovie-plugin-app-${buildType}2.apk" }
//        }
//        into(apkDir)
//
//    }.dependsOn(":mgmovie-plugin-app:assemble${buildType.capitalize()}")
//}
//
//tasks.whenTaskAdded {
//    if (name == "assembleDebug") {
//        val createTask = createDuplicateApkTask("debug")
//        this.finalizedBy(createTask)
//    }
//    if (name == "assembleRelease") {
//        val createTask = createDuplicateApkTask("release")
//        this.finalizedBy(createTask)
//    }
//}

apply(plugin = "com.tencent.shadow.plugin")

extensions.findByType(com.tencent.shadow.core.gradle.ShadowPlugin.ShadowExtension::class.java)?.apply {
    extensions.findByType(com.tencent.shadow.core.gradle.extensions.PackagePluginExtension::class.java)?.apply {
        loaderApkProjectPath = "sample/sample-plugin/sample-loader"
        runtimeApkProjectPath = "sample/sample-plugin/sample-runtime"

        archiveSuffix = System.getenv("PluginSuffix") ?: ""
        archivePrefix = "mgmoviePlugins"
        destinationDir = "${rootProject.buildDir}"

        version = 4
        compactVersion = arrayOf(1, 2, 3)
        uuidNickName = "1.1.5"

        buildTypes {
            maybeCreate("debug").apply {
                loaderApkConfig = groovy.lang.Tuple2("sample-loader-debug.apk", ":sample-loader:assembleDebug")
                runtimeApkConfig = groovy.lang.Tuple2("sample-runtime-debug.apk", ":sample-runtime:assembleDebug")

                pluginApks {
                    maybeCreate("mgmoviePluginApk").apply {
                        businessName = "mgmovie-plugin-app"
                        partKey = "mgmovie-plugin-app"
                        buildTask = ":mgmovie-plugin-app:assembleDebug"
                        apkName = "mgmovie-plugin-app-debug.apk"
                        apkPath = "sample/sample-plugin/mgmovie-plugin-app/build/outputs/apk/debug/mgmovie-plugin-app-debug.apk"
//                        hostWhiteList = arrayOf("com.tencent.shadow.sample.host.lib")
                    }
                }
            }

            maybeCreate("release").apply {
                loaderApkConfig = groovy.lang.Tuple2("sample-loader-release.apk", ":sample-loader:assembleRelease")
                runtimeApkConfig = groovy.lang.Tuple2("sample-runtime-release.apk", ":sample-runtime:assembleRelease")

                pluginApks {
                    maybeCreate("mgmoviePluginApk").apply {
                        businessName = "mgmovie-plugin-app"
                        partKey = "mgmovie-plugin-app"
                        buildTask = ":mgmovie-plugin-app:assembleRelease"
                        apkName = "mgmovie-plugin-app-release.apk"
                        apkPath = "sample/sample-plugin/mgmovie-plugin-app/build/outputs/apk/release/mgmovie-plugin-app-release.apk"
//                        hostWhiteList = arrayOf("com.tencent.shadow.sample.host.lib")
                    }
                }
            }
        }
    }
}
