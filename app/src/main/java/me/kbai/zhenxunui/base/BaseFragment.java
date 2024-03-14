package me.kbai.zhenxunui.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

/**
 * @author sean 2021/5/11
 */
public abstract class BaseFragment<VB extends ViewBinding> extends Fragment {
    protected VB viewBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        if (viewBinding == null) {
            viewBinding = getViewBinding(inflater, container);
            initView();
        }
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewBinding = null;
    }

    /**
     * 设置 viewBinding
     *
     * @param inflater layoutInflater
     * @return viewBinding
     */
    @NonNull
    protected abstract VB getViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container);

    protected abstract void initView();

    protected void initData() {
    }

    @Nullable
    public VB getViewBinding() {
        return viewBinding;
    }
}
