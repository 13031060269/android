package com.lwp.app;

import androidx.lifecycle.ViewModelProvider;

import com.lwp.lib.mvp.interfaces.GainLayout;

public class Test {
    void test(ViewModelProvider provider) {
        MainViewModel mainViewModel = provider.get(MainViewModel.class);

    }

}
