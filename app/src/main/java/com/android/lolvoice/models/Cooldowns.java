package com.android.lolvoice.models;

import android.util.Log;

import com.android.lolvoice.models.event.SpeakEvent;

import de.greenrobot.event.EventBus;

public class Cooldowns {

    private Role mRole;
    private Spell mSpell1;
    private Spell mSpell2;
    private long mSpell1CD = Long.MAX_VALUE;
    private long mSpell2CD = Long.MAX_VALUE;

    public Cooldowns(Role role, Spell spell1, Spell spell2) {
        mRole = role;
        mSpell1 = spell1;
        mSpell2 = spell2;
    }

    public boolean useSpell(Spell spell) {
        if (spell.equals(mSpell1)) {
            mSpell1CD = System.currentTimeMillis() + mSpell1.getCooldown();
            return true;
        }
        if (spell.equals(mSpell2)) {
            mSpell2CD = System.currentTimeMillis() + mSpell2.getCooldown();
            return true;
        }
        return false;
    }

    public void timer(long time) {
        if (mSpell1CD < time) {
            EventBus.getDefault()
                    .post(new SpeakEvent(mRole.getRoleName() + " has " + mSpell1.name()));
            mSpell1CD = Long.MAX_VALUE;
        }
        if (mSpell2CD < time) {
            EventBus.getDefault()
                    .post(new SpeakEvent(mRole.getRoleName() + " has " + mSpell2.name()));
            mSpell2CD = Long.MAX_VALUE;
        }
    }
}
