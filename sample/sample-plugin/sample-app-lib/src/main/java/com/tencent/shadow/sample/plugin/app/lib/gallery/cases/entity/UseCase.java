package com.tencent.shadow.sample.plugin.app.lib.gallery.cases.entity;

import android.os.Bundle;

public abstract class UseCase {

    public abstract String getName();

    public abstract String getSummary();

    public abstract Class getPageClass();

    public Bundle getPageParams(){
        return null;
    }
}
