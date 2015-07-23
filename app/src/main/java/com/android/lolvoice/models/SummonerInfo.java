package com.android.lolvoice.models;


import com.orm.SugarRecord;
import com.robrua.orianna.type.core.common.Region;
import com.robrua.orianna.type.core.summoner.Summoner;

public class SummonerInfo extends SugarRecord<SummonerInfo> {

    private long summonerId;
    private String name;
    private Region region;

    public SummonerInfo() {
    }

    public SummonerInfo(String summonerName, Region region) {
        name = summonerName;
        this.region = region;
    }

    public void setSummonerId(long summonerId) {
        this.summonerId = summonerId;
        save();
    }

    public long getSummonerId() {
        return summonerId;
    }

    public String getName() {
        return name;
    }

    public Region getRegion() {
        return region;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof  SummonerInfo)) return false;
        SummonerInfo summonerInfo = (SummonerInfo) o;
        return name.equals(summonerInfo.getName()) && region.equals(summonerInfo.getRegion());
    }
}
