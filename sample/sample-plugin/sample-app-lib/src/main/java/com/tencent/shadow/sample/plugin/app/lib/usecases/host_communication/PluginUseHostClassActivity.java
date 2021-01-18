package com.tencent.shadow.sample.plugin.app.lib.usecases.host_communication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;

import com.tencent.shadow.sample.host.lib.SampleHostUIProvider;
import com.tencent.shadow.sample.plugin.app.lib.gallery.BaseActivity;
import com.tencent.shadow.sample.plugin.app.lib.gallery.cases.entity.UseCase;

public class PluginUseHostClassActivity extends BaseActivity {

    public static class Case extends UseCase {
        @Override
        public String getName() {
            return "插件使用宿主类测试";
        }

        @Override
        public String getSummary() {
            return "测试插件中调用宿主类的方法";
        }

        @Override
        public Class getPageClass() {
            return PluginUseHostClassActivity.class;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RelativeLayout relativeLayout = new RelativeLayout(this);

        SampleHostUIProvider sampleHostUIProvider = SampleHostUIProvider.getInstance();
        View welcomeUIFromHost = sampleHostUIProvider.createWelcomeUIFromHost();

        relativeLayout.addView(welcomeUIFromHost);
        addRuleProperty(welcomeUIFromHost, RelativeLayout.CENTER_IN_PARENT);

        setContentView(relativeLayout);
    }

    private void addRuleProperty(
            View view,
            int property
    ) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        layoutParams.addRule(property);
        view.setLayoutParams(layoutParams);
    }

}
