package com.android.lolvoice.utils;

import com.android.lolvoice.models.SummonerInfo;

import java.util.List;

public class SummonerUtils {

    private static SummonerInfo sSummoner;

    public static void setSummoner(SummonerInfo summoner) {
        sSummoner = summoner;
    }

    public static boolean hasSummoner() {
        return sSummoner != null;
    }

    public static SummonerInfo getSummoner() {
        return sSummoner;
    }

    public static String getSummonerName() {
        return sSummoner == null ? null : sSummoner.getName();
    }

    public static List<SummonerInfo> getStoredSummoners() {
        return SummonerInfo.listAll(SummonerInfo.class);
    }
}
