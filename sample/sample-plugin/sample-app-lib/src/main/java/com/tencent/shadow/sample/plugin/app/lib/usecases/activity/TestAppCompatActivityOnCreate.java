package com.tencent.shadow.sample.plugin.app.lib.usecases.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.tencent.shadow.sample.plugin.app.lib.R;
import com.tencent.shadow.sample.plugin.app.lib.gallery.cases.entity.UseCase;
import com.tencent.shadow.sample.plugin.app.lib.gallery.util.ToastUtil;

public class TestAppCompatActivityOnCreate extends AppCompatActivity {

    public static class Case extends UseCase{
        @Override
        public String getName() {
            return "AppCompatActivity 测试";
        }

        @Override
        public String getSummary() {
            return "由于Android自己的实现中\n"
                + "AppCompatActivity的AppCompatDelegateImpl使用了\n --makeOptionalFitsSystemWindows\n --computeFitSystemWindows。\n属于Android P浅灰API\n\n"
                + "所以当前测试用例运行在Android 9 或以上的设备时,  您需要先手动取消HostApplication中的严格模式 ";
        }

        @Override
        public Class getPageClass() {
            return TestAppCompatActivityOnCreate.class;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_lifecycle);
        ToastUtil.showToast(this,"onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        ToastUtil.showToast(this,"onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ToastUtil.showToast(this,"onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        ToastUtil.showToast(this,"onResume");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ToastUtil.showToast(this,"onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ToastUtil.showToast(this,"onRestoreInstanceState");
    }

    @Override
    protected void onStop() {
        super.onStop();
        ToastUtil.showToast(this,"onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ToastUtil.showToast(this,"onDestroy");
    }
}
