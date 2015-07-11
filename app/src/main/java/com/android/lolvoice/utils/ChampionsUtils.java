package com.android.lolvoice.utils;

import com.android.lolvoice.services.Callback;
import com.robrua.orianna.api.core.AsyncRiotAPI;
import com.robrua.orianna.type.api.Action;
import com.robrua.orianna.type.core.staticdata.Champion;
import com.robrua.orianna.type.core.staticdata.SummonerSpell;
import com.robrua.orianna.type.exception.APIException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChampionsUtils {

    public static Map<Integer, Champion> sChampions;

    public static Champion getChampionById(int id) {
        return sChampions.get(id);
    }

    public static void loadChampions(final Callback<Void> callback) {
        sChampions = new HashMap<>();
        AsyncRiotAPI.getChampions(new Action<List<Champion>>() {
            @Override
            public void handle(APIException exception) {
            }

            @Override
            public void perform(List<Champion> champions) {
                for (Champion champion : champions)
                    sChampions.put((int) champion.getID(), champion);
                callback.callback(null);
            }
        });
        AsyncRiotAPI.getSummonerSpells(new Action<List<SummonerSpell>>() {
            @Override
            public void handle(APIException exception) {
            }

            @Override
            public void perform(List<SummonerSpell> responseData) {
                callback.callback(null);
            }
        });
    }
}
