package com.tencent.shadow.sample.plugin.app.lib.gallery.splash;

public interface ISplashAnimation {

    void start();

    void stop();

    void setAnimationListener(AnimationListener animationListener);


    interface AnimationListener{
        void onAnimationEnd();
    }
}
