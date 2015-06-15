package com.android.lolvoice.models;

import com.robrua.orianna.type.core.staticdata.Champion;
import com.robrua.orianna.type.core.staticdata.ChampionSpell;

import java.util.List;

public class ChampionInfo {

    private int id;
    private String name;
    private List<Double> ultCd;

    public ChampionInfo(Champion champion) {
        id = (int) champion.getID();
        name = champion.getName();
        for (ChampionSpell spell : champion.getSpells()) {
            if (spell.getMaxRank() == 3)
                ultCd = spell.getCooldown();
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Double> getUltCd() {
        return ultCd;
    }
}
