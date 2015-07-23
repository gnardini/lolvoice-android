package com.android.lolvoice.models;

import com.orm.SugarRecord;
import com.robrua.orianna.type.core.staticdata.SummonerSpell;

public class SummonerSpellInfo extends SugarRecord<SummonerSpellInfo> {

    private int summonerSpellId;
    private String imageGroup;
    private String imageFull;

    public SummonerSpellInfo() {
    }

    public SummonerSpellInfo(SummonerSpell summonerSpell) {
        summonerSpellId = (int) summonerSpell.getID();
        imageGroup = summonerSpell.getImage().getGroup();
        imageFull = summonerSpell.getImage().getFull();
        save();
    }

    public int getSummonerSpellId() {
        return summonerSpellId;
    }

    public String getImageGroup() {
        return imageGroup;
    }

    public String getImageFull() {
        return imageFull;
    }
}
