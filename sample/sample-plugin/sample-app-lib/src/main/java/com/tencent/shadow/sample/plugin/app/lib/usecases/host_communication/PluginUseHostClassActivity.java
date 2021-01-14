package com.tencent.shadow.sample.plugin.app.lib.usecases.host_communication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.tencent.shadow.sample.host.lib.HostUiLayerProvider;
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

        LinearLayout linearLayout = new LinearLayout(this);

        HostUiLayerProvider hostUiLayerProvider = HostUiLayerProvider.getInstance();
        View hostUiLayer = hostUiLayerProvider.buildHostUiLayer();
        linearLayout.addView(hostUiLayer);

        setContentView(linearLayout);
    }
}
