package com.android.lolvoice;


import android.content.Context;

import com.android.lolvoice.utils.ChampionsUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.orm.SugarApp;
import com.robrua.orianna.api.core.AsyncRiotAPI;
import com.robrua.orianna.type.api.LoadPolicy;
import com.robrua.orianna.type.core.common.Region;

public class LoLVoiceApplication extends SugarApp {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        LoLVoiceApplication.sContext = getApplicationContext();
        Fresco.initialize(sContext);
        AsyncRiotAPI.setMirror(Region.LAS);
        AsyncRiotAPI.setRegion(Region.LAS);
        AsyncRiotAPI.setAPIKey(Configuration.API_KEY);
        AsyncRiotAPI.setLoadPolicy(LoadPolicy.LAZY);
        ChampionsUtils.loadChampions();
    }

    public static Context getAppContext() {
        return LoLVoiceApplication.sContext;
    }
}
