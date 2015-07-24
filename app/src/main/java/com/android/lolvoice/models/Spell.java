package com.android.lolvoice.models;

import java.util.Arrays;
import java.util.List;

public enum Spell {

    // Ids got from Riots' official documentation
    FLASH(4, 2600, Arrays.asList("flash")),
    IGNITE(14, 180000, Arrays.asList("ignite")),
    TP(12, 240000, Arrays.asList("tp", "teleport")),
    EXHAUST(3, 180000, Arrays.asList("exhaust")),
    GHOST(6, 180000, Arrays.asList("ghost")),
    HEAL(7, 210000, Arrays.asList("heal")),
    CLEANSE(1, 180000, Arrays.asList("cleanse", "clean")),
    BARRIER(21, 180000, Arrays.asList("barrier", "bar"));

    private int mId;
    private List<String> mNames;
    private long mCooldown;

    Spell(int id, long cd, List<String> names) {
        mId = id;
        mNames = names;
        mCooldown = cd;
    }

    public int getId() {
        return mId;
    }

    public List<String> getNames() {
        return mNames;
    }

    public long getCooldown() {
        return mCooldown;
    }
}
