package com.android.lolvoice.utils;

import com.android.lolvoice.listeners.OnFinishLoadingListener;
import com.android.lolvoice.models.ChampionInfo;
import com.android.lolvoice.models.SummonerSpellInfo;
import com.robrua.orianna.api.core.AsyncRiotAPI;
import com.robrua.orianna.type.api.Action;
import com.robrua.orianna.type.core.staticdata.Champion;
import com.robrua.orianna.type.core.staticdata.SummonerSpell;
import com.robrua.orianna.type.exception.APIException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChampionsUtils {

    private static Map<Integer, ChampionInfo> sChampions;
    private static Map<Integer, SummonerSpellInfo> sSummonerSpells;
    private static boolean sChampionsLoading;
    private static boolean sSummonerSpellsLoading;
    private static boolean sChampionsLoaded;
    private static boolean sSummonerSpellsLoaded;
    private static OnFinishLoadingListener sListener;

    public static ChampionInfo getChampionById(int id) {
        return sChampions.get(id);
    }

    public static SummonerSpellInfo getSummonerSpellById(int id) {
        return sSummonerSpells.get(id);
    }

    public static void loadChampions() {
        if (sSummonerSpellsLoaded && sChampionsLoaded)  return;
        getChampions();
        getSummonerSpells();
    }

    public static void loadChampions(OnFinishLoadingListener listener) {
        if (sSummonerSpellsLoaded && sChampionsLoaded) {
            listener.onLoadingSuccess();
            return;
        }
        quickLoad(listener);
        sListener = listener;
        getChampions();
        getSummonerSpells();
    }

    private static void quickLoad(OnFinishLoadingListener listener) {
        if (sChampions == null) {
            List<ChampionInfo> champions = ChampionInfo.listAll(ChampionInfo.class);
            if (champions != null && !champions.isEmpty()) {
                sChampions = new HashMap<>();
                for (ChampionInfo champion : champions)
                    sChampions.put(champion.getChampionId(), champion);
                sChampionsLoaded = true;
            }
        }
        if (sSummonerSpells == null) {
            List<SummonerSpellInfo> summonerSpells =
                    SummonerSpellInfo.listAll(SummonerSpellInfo.class);
            if (summonerSpells != null && !summonerSpells.isEmpty()) {
                sSummonerSpells = new HashMap<>();
                for (SummonerSpellInfo ss : summonerSpells)
                    sSummonerSpells.put(ss.getSummonerSpellId(), ss);
                sSummonerSpellsLoaded = true;
            }
        }
        if (listener != null && sChampions != null && sSummonerSpells != null)
            listener.onLoadingSuccess();
    }

    private static void getChampions() {
        if (sChampionsLoading) return;
        sChampionsLoading = true;
        AsyncRiotAPI.getChampions(new Action<List<Champion>>() {
            @Override
            public void handle(APIException exception) {
                sChampionsLoading = false;
                if (sListener != null) sListener.onLoadingFail();
            }

            @Override
            public void perform(List<Champion> champions) {
                ChampionInfo.deleteAll(ChampionInfo.class);
                sChampions = new HashMap<>();
                for (Champion champion : champions)
                    sChampions.put((int) champion.getID(), new ChampionInfo(champion));
                sChampionsLoading = false;
                sChampionsLoaded = true;
                if (sListener != null && sSummonerSpellsLoaded) sListener.onLoadingSuccess();
            }
        });
    }

    private static void getSummonerSpells() {
        if (sSummonerSpellsLoading) return;
        sSummonerSpellsLoading = true;
        AsyncRiotAPI.getSummonerSpells(new Action<List<SummonerSpell>>() {
            @Override
            public void handle(APIException exception) {
                sSummonerSpellsLoading = false;
                if (sListener != null) sListener.onLoadingFail();
            }

            @Override
            public void perform(List<SummonerSpell> summonerSpells) {
                SummonerSpellInfo.deleteAll(SummonerSpellInfo.class);
                sSummonerSpells = new HashMap<>();
                for (SummonerSpell ss : summonerSpells)
                    sSummonerSpells.put((int) ss.getID(), new SummonerSpellInfo(ss));
                sSummonerSpellsLoading = false;
                sSummonerSpellsLoaded = true;
                if (sListener != null && sChampionsLoaded) sListener.onLoadingSuccess();
            }
        });
    }
}
