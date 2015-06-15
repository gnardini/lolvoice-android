package com.android.lolvoice.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public abstract class BaseFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(layout(), container, false);
        setUi(v);
        init();
        populate();
        setListeners();
        return v;
    }

    protected abstract int layout();

    protected abstract void setUi(View v);

    protected abstract void init();

    protected abstract void populate();

    protected abstract void setListeners();

    protected void showToast(int resId) {
        Toast.makeText(getActivity(), resId, Toast.LENGTH_SHORT).show();
    }

    protected void showToast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    protected void replaceFragment(int resId, Fragment f) {
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(resId, f)
                .commit();
    }
}