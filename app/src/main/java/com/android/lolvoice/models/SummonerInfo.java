package com.android.lolvoice.models;


import com.orm.SugarRecord;
import com.robrua.orianna.type.core.summoner.Summoner;

public class SummonerInfo extends SugarRecord<SummonerInfo> {

    private long summonerId;
    private String name;

    public SummonerInfo() {
    }

    public SummonerInfo(Summoner summoner) {
        summonerId = summoner.getID();
        name = summoner.getName();
        save();
    }

    public long getSummonerId() {
        return summonerId;
    }

    public String getName() {
        return name;
    }
}
