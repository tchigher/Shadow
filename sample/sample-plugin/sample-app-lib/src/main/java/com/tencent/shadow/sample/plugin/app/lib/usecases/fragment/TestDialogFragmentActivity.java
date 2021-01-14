package com.tencent.shadow.sample.plugin.app.lib.usecases.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tencent.shadow.sample.plugin.app.lib.R;
import com.tencent.shadow.sample.plugin.app.lib.gallery.BaseActivity;
import com.tencent.shadow.sample.plugin.app.lib.gallery.cases.entity.UseCase;

public class TestDialogFragmentActivity extends BaseActivity {

    public static class Case extends UseCase {
        @Override
        public String getName() {
            return "DialogFragment相关测试";
        }

        @Override
        public String getSummary() {
            return "测试DialogFragment使用setWindowAnimations";
        }

        @Override
        public Class getPageClass() {
            return TestDialogFragmentActivity.class;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_fragment_activity);

        String msg = "这是TestDialogFragment";
        Bundle bundle = new Bundle();
        bundle.putString("msg", msg);
        TestDialogFragment testFragment = TestDialogFragment.newInstance(bundle);
        testFragment.show(getFragmentManager(), "TestDialogFragment");

        getWindow().setWindowAnimations(R.style.dialog_exit_fade_out);
    }
}
