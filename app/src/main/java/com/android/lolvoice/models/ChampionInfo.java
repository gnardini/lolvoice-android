package com.android.lolvoice.models;

import com.orm.SugarRecord;
import com.robrua.orianna.type.core.staticdata.Champion;
import com.robrua.orianna.type.core.staticdata.ChampionSpell;

import java.util.List;

public class ChampionInfo extends SugarRecord<ChampionInfo> {

    private int championId;
    private String name;
    private String imageGroup;
    private String imageFull;

    public ChampionInfo() {
    }

    public ChampionInfo(Champion champion) {
        championId = (int) champion.getID();
        name = champion.getName();
        imageGroup = champion.getImage().getGroup();
        imageFull = champion.getImage().getFull();
        save();
    }

    public int getChampionId() {
        return championId;
    }

    public String getName() {
        return name;
    }

    public String getImageGroup() {
        return imageGroup;
    }

    public String getImageFull() {
        return imageFull;
    }
}
