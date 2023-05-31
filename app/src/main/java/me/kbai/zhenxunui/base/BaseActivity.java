package me.kbai.zhenxunui.base;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

/**
 * @author sean 2021/5/11
 */
public abstract class BaseActivity<VB extends ViewBinding> extends AppCompatActivity {
    protected VB viewBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = getViewBinding(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        initView();
        initData();
    }

    /**
     * 设置 viewBinding
     *
     * @param inflater layoutInflater
     * @return viewBinding
     */
    @NonNull
    protected abstract VB getViewBinding(@NonNull LayoutInflater inflater);

    protected abstract void initView();

    protected void initData() {
    }
}
