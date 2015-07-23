package com.android.lolvoice.utils;

import android.content.Context;
import android.content.Intent;

import com.android.lolvoice.activities.CurrentGameActivity;
import com.android.lolvoice.listeners.CurrentGameLoadingListener;
import com.android.lolvoice.listeners.OnFinishLoadingListener;
import com.robrua.orianna.api.core.AsyncRiotAPI;
import com.robrua.orianna.type.api.Action;
import com.robrua.orianna.type.core.currentgame.CurrentGame;
import com.robrua.orianna.type.exception.APIException;

import java.util.List;

public class CurrentGameUtils {

    private static CurrentGame sCurrentGame;
    private static boolean sLoadingGame;

    public static CurrentGame getCurrentGame() {
        return sCurrentGame;
    }

    public static void startSummonerGame(final Context context,
                                         final CurrentGameLoadingListener listener) {
        if (sLoadingGame) return;
        sLoadingGame = true;
        AsyncRiotAPI.getCurrentGame(new Action<CurrentGame>() {
            @Override
            public void handle(APIException exception) {
                sLoadingGame = false;
                listener.onLoadingFail();
            }

            @Override
            public void perform(CurrentGame currentGame) {
                if (currentGame == null) {
                    listener.onLoadingFail();
                    return;
                }
                listener.onGameLoaded();
                sCurrentGame = currentGame;
                startCurrentGame(context, listener);
            }
        }, SummonerUtils.getSummoner().getSummonerId());
    }

    public static void startRandomGame(final Context context,
                                       final CurrentGameLoadingListener listener) {
        if (sLoadingGame) return;
        sLoadingGame = true;
        AsyncRiotAPI.getFeaturedGames(new Action<List<CurrentGame>>() {
            @Override
            public void handle(APIException exception) {
                sLoadingGame = false;
                listener.onLoadingFail();
            }

            @Override
            public void perform(List<CurrentGame> responseData) {
                listener.onGameLoaded();
                SummonerUtils.setSummoner(null);
                sCurrentGame = responseData.get(0);
                startCurrentGame(context, listener);
            }
        });
    }

    public static void startCurrentGame(final Context context,
                                        final CurrentGameLoadingListener listener) {
        ChampionsUtils.loadChampions(new OnFinishLoadingListener() {
            @Override
            public void onLoadingSuccess() {
                sLoadingGame = false;
                listener.onChampionInfoLoaded();
                context.startActivity(new Intent(context, CurrentGameActivity.class));
            }

            @Override
            public void onLoadingFail() {
                sLoadingGame = false;
                listener.onLoadingFail();
                sCurrentGame = null;
            }
        });
    }
}
