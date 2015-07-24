package com.android.lolvoice.utils;

import com.android.lolvoice.listeners.OnFinishLoadingListener;
import com.android.lolvoice.models.ChampionInfo;
import com.android.lolvoice.models.Role;
import com.android.lolvoice.models.Spell;
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

    public static final int TOP = 0;
    public static final int JG = 1;
    public static final int MID = 2;
    public static final int ADC = 3;
    public static final int SUP = 4;

    public static final int FLASH = 4;
    public static final int IGNITE = 14;
    public static final int TP = 12;
    public static final int EXHAUST = 3;
    public static final int GHOST = 6;
    public static final int HEAL = 7;
    public static final int CLEANSE = 1;
    public static final int BARRIER = 21;

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
                if (sListener != null && !sChampionsLoaded && sSummonerSpellsLoaded)
                    sListener.onLoadingSuccess();
                sChampionsLoaded = true;
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
                if (sListener != null && !sSummonerSpellsLoaded && sChampionsLoaded)
                    sListener.onLoadingSuccess();
                sSummonerSpellsLoaded = true;
            }
        });
    }

    public static Role getRole(int position) {
        switch (position) {
            case TOP: return Role.TOP;
            case JG: return Role.JG;
            case MID: return Role.MID;
            case ADC: return Role.ADC;
            case SUP: return Role.SUP;
            default: return null;
        }
    }

    public static int getRolePosition(Role role) {
        switch (role) {
            case TOP: return TOP;
            case JG: return JG;
            case MID: return MID;
            case ADC: return ADC;
            case SUP: return SUP;
            default: return -1;
        }
    }

    public static Spell getSpell(int position) {
        switch (position) {
            case FLASH: return Spell.FLASH;
            case IGNITE: return Spell.IGNITE;
            case TP: return Spell.TP;
            case EXHAUST: return Spell.EXHAUST;
            case GHOST: return Spell.GHOST;
            case HEAL: return Spell.HEAL;
            case CLEANSE: return Spell.CLEANSE;
            case BARRIER: return Spell.BARRIER;
            default: return null;
        }
    }
}
