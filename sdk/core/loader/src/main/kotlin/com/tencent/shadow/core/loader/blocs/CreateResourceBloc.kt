package com.tencent.shadow.core.loader.blocs

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import android.webkit.WebView
import java.util.concurrent.CountDownLatch

object CreateResourceBloc {

    fun create(
            packageArchiveInfo: PackageInfo,
            archiveFilePath: String,
            hostAppContext: Context
    ): Resources {
        //先用宿主context初始化一个WebView，以便WebView的逻辑去修改sharedLibraryFiles，将webview.apk添加进去
        val latch = CountDownLatch(1)
        Handler(Looper.getMainLooper()).post {
            WebView(hostAppContext)
            latch.countDown()
        }
        latch.await()

        val packageManager = hostAppContext.packageManager
        packageArchiveInfo.applicationInfo.publicSourceDir = archiveFilePath
        packageArchiveInfo.applicationInfo.sourceDir = archiveFilePath
        packageArchiveInfo.applicationInfo.sharedLibraryFiles = hostAppContext.applicationInfo.sharedLibraryFiles
        try {
            return packageManager.getResourcesForApplication(packageArchiveInfo.applicationInfo)
        } catch (e: PackageManager.NameNotFoundException) {
            throw RuntimeException(e)
        }
    }

}
