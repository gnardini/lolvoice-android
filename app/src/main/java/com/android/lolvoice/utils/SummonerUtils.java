package com.android.lolvoice.utils;

import com.android.lolvoice.models.SummonerInfo;
import com.android.lolvoice.services.Callback;
import com.robrua.orianna.api.core.AsyncRiotAPI;
import com.robrua.orianna.type.api.Action;
import com.robrua.orianna.type.core.currentgame.CurrentGame;
import com.robrua.orianna.type.exception.APIException;

import java.util.List;

public class SummonerUtils {

    private static SummonerInfo sSummoner;

    public static void setSummoner(SummonerInfo summoner) {
        sSummoner = summoner;
    }

    public static boolean hasSummoner() {
        return sSummoner != null;
    }

    public static void getCurrentGame(final Callback<CurrentGame> callback) {
        AsyncRiotAPI.getCurrentGame(new Action<CurrentGame>() {
            @Override
            public void handle(APIException exception) {
            }

            @Override
            public void perform(CurrentGame currentGame) {
                callback.callback(currentGame);
            }
        }, sSummoner.getId());
    }

    public static String getSummonerName() {
        return sSummoner.getName();
    }

    public static List<SummonerInfo> getStoredSummoners() {
        return SummonerInfo.listAll(SummonerInfo.class);
    }
}
