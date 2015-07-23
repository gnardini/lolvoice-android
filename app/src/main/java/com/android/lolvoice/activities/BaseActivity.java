package com.android.lolvoice.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import butterknife.ButterKnife;

public abstract class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout());
        ButterKnife.bind(this);
        populate();
        init();
        setListeners();
    }

    protected abstract int layout();

    protected void setUi(){}

    protected void setListeners(){}

    protected void populate(){}

    protected void init(){}

    protected void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    protected void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    protected void replaceFragment(int resId, Fragment f) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(resId, f)
                .commit();
    }

    protected void replaceFragment(int resId, Fragment f, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(resId, f, tag)
                .commit();
    }
}