package com.tencent.shadow.sample.plugin.app.lib.usecases.context;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tencent.shadow.sample.plugin.app.lib.gallery.cases.entity.UseCase;

public class ApplicationContextSubDirTestActivity extends SubDirContextThemeWrapperTestActivity {
    public static class Case extends UseCase {
        @Override
        public String getName() {
            return "ApplicationContextSubDir测试";
        }

        @Override
        public String getSummary() {
            return "测试Application作为Context因BusinessName不同而隔离的相关特性";
        }

        @Override
        public Class getPageClass() {
            return ApplicationContextSubDirTestActivity.class;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fillTestValues(getApplication());
    }
}
