package com.tencent.shadow.sample.plugin.app.lib.usecases.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;

import com.tencent.shadow.sample.plugin.app.lib.R;
import com.tencent.shadow.sample.plugin.app.lib.gallery.cases.entity.UseCase;

public class TestActivityOptionMenu extends Activity {

    public static class Case extends UseCase {
        @Override
        public String getName() {
            return "Activity Menu测试";
        }

        @Override
        public String getSummary() {
            return "测试Activity的 onCreateOptionsMenu";
        }

        @Override
        public Class getPageClass() {
            return TestActivityOptionMenu.class;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.PluginAppThemeLight);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_settheme);
        setTitle("看右边的 menu ->");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.case_test_activity_option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
