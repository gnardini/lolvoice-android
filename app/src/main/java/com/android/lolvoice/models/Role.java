package com.android.lolvoice.models;

import java.util.Arrays;
import java.util.List;

public enum Role {

    TOP(0, "top", Arrays.asList("top")),
    JG(1, "jungler", Arrays.asList("jungle", "jungler")),
    MID(2, "mid", Arrays.asList("middle", "mid")),
    ADC(3, "marksman", Arrays.asList("ad", "mark", "marksman")),
    SUP(4, "support", Arrays.asList("sup", "support"));

    public final static int COUNT = 5;

    private int mPosition;
    private String mRoleName;
    private List<String> mNames;

    Role (int position, String roleName, List<String> names) {
        mPosition = position;
        mRoleName = roleName;
        mNames = names;
    }

    public int getPosition() {
        return mPosition;
    }

    public String getRoleName() {
        return mRoleName;
    }

    public List<String> getNames() {
        return mNames;
    }
}
