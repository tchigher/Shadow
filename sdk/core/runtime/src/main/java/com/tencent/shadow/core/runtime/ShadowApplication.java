package com.tencent.shadow.core.runtime;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Build;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于在 plugin-loader 中调用假的 Application 方法的接口
 */
public class ShadowApplication extends ShadowContext {

    private Application mHostApplication;

    private Map<String, List<String>> mBroadcasts;

    private ShadowAppComponentFactory mAppComponentFactory;

    public boolean isCallOnCreate;

    @Override
    public Context getApplicationContext() {
        return this;
    }

    final private Map<ShadowActivityLifecycleCallbacks,
            Application.ActivityLifecycleCallbacks>
            mActivityLifecycleCallbacksMap = new HashMap<>();

    public void onCreate() {

        isCallOnCreate = true;

        for (Map.Entry<String, List<String>> entry: mBroadcasts.entrySet()){
            try {
                Class<?> clazz = mPluginClassLoader.loadClass(entry.getKey());
                BroadcastReceiver receiver = ((BroadcastReceiver) clazz.newInstance());
                mAppComponentFactory.instantiateReceiver(mPluginClassLoader, entry.getKey(), null);

                IntentFilter intentFilter = new IntentFilter();
                for (String action:entry.getValue()
                     ) {
                    intentFilter.addAction(action);
                }
                registerReceiver(receiver, intentFilter);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        mHostApplication.registerComponentCallbacks(new ComponentCallbacks2() {
            @Override
            public void onTrimMemory(int level) {
                ShadowApplication.this.onTrimMemory(level);
            }

            @Override
            public void onConfigurationChanged(Configuration newConfig) {
                ShadowApplication.this.onConfigurationChanged(newConfig);
            }

            @Override
            public void onLowMemory() {
                ShadowApplication.this.onLowMemory();
            }
        });
    }


    public void onTerminate() {
        throw new UnsupportedOperationException();
    }


    public void onConfigurationChanged(Configuration newConfig) {
        //do nothing.
    }


    public void onLowMemory() {
        //do nothing.
    }


    public void onTrimMemory(int level) {
        //do nothing.
    }


    public void registerComponentCallbacks(ComponentCallbacks callback) {
        mHostApplication.registerComponentCallbacks(callback);
    }


    public void unregisterComponentCallbacks(ComponentCallbacks callback) {
        mHostApplication.unregisterComponentCallbacks(callback);
    }


    public void registerActivityLifecycleCallbacks(ShadowActivityLifecycleCallbacks callback) {
        synchronized (mActivityLifecycleCallbacksMap) {
            final ShadowActivityLifecycleCallbacks.Wrapper wrapper
                    = new ShadowActivityLifecycleCallbacks.Wrapper(callback, this);
            mActivityLifecycleCallbacksMap.put(callback, wrapper);
            mHostApplication.registerActivityLifecycleCallbacks(wrapper);
        }
    }


    public void unregisterActivityLifecycleCallbacks(ShadowActivityLifecycleCallbacks callback) {
        synchronized (mActivityLifecycleCallbacksMap) {
            final Application.ActivityLifecycleCallbacks activityLifecycleCallbacks
                    = mActivityLifecycleCallbacksMap.get(callback);
            if (activityLifecycleCallbacks != null) {
                mHostApplication.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks);
                mActivityLifecycleCallbacksMap.remove(callback);
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void registerOnProvideAssistDataListener(Application.OnProvideAssistDataListener callback) {
        mHostApplication.registerOnProvideAssistDataListener(callback);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void unregisterOnProvideAssistDataListener(Application.OnProvideAssistDataListener callback) {
        mHostApplication.unregisterOnProvideAssistDataListener(callback);
    }

    public void setHostApplicationContextAsBase(Context hostAppContext) {
        super.attachBaseContext(hostAppContext);
        mHostApplication = (Application) hostAppContext;
    }

    public void setBroadcasts(Map<String, List<String>> broadcast){
        mBroadcasts = broadcast;
    }

    public void attachBaseContext(Context base) {
        //do nothing.
    }

    public void setAppComponentFactory(ShadowAppComponentFactory factory) {
        mAppComponentFactory = factory;
    }
}
