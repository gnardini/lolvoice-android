package com.android.lolvoice.helper;

import android.os.Handler;

import com.android.lolvoice.models.Cooldowns;
import com.android.lolvoice.models.Role;
import com.android.lolvoice.models.Spell;

import java.util.ArrayList;
import java.util.List;

public class CooldownsHelper {

    private static final int DELAY = 1000;

    private List<Cooldowns> mCooldowns;
    private Handler mTimeHandler;
    private Runnable mTimeRunnable;
    private long mStartTime;

    public CooldownsHelper() {
        mCooldowns = new ArrayList<>(Role.COUNT);
    }

    public void setRole(Role role, Spell spell1, Spell spell2) {
        mCooldowns.add(role.getPosition(), new Cooldowns(role, spell1, spell2));
    }

    public void start() {
        mTimeRunnable = new Runnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                for (Cooldowns cds : mCooldowns) cds.timer(currentTime);
                long delay = DELAY - ((currentTime - mStartTime) % DELAY);
                mTimeHandler.postDelayed(this, delay);
            }
        };
        mTimeHandler = new Handler();
        mStartTime = System.currentTimeMillis();
        mTimeHandler.postDelayed(mTimeRunnable, DELAY);
    }

    public void setCooldown(Role role, Spell spell) {
        mCooldowns.get(role.getPosition()).useSpell(spell);
    }
}
