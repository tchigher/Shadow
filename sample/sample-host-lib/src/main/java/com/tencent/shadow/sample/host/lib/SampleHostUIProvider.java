package com.tencent.shadow.sample.host.lib;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * 这是一个将要打包到宿主中的类.
 */
public class SampleHostUIProvider {

    private static SampleHostUIProvider sInstance;

    public static void init(
            Context hostApplicationContext
    ) {
        sInstance = new SampleHostUIProvider(hostApplicationContext);
    }

    final private Context mHostApplicationContext;

    private SampleHostUIProvider(
            Context hostApplicationContext
    ) {
        mHostApplicationContext = hostApplicationContext;
    }

    public static SampleHostUIProvider getInstance() {
        return sInstance;
    }

    public View createWelcomeUIFromHost() {
        return LayoutInflater.from(mHostApplicationContext).inflate(
                R.layout.sample_host_lib__welcome_ui, null, false
        );
    }

}
