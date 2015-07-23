package com.android.lolvoice.listeners;

public interface CurrentGameLoadingListener {

    void onGameLoaded();

    void onChampionInfoLoaded();

    void onLoadingFail();
}
