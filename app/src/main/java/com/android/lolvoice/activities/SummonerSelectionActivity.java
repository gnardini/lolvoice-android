package com.android.lolvoice.activities;

import com.android.lolvoice.fragments.SummonerSelectionFragment;

public class SummonerSelectionActivity extends BaseActivity {

    @Override
    protected int layout() {
        return com.android.lolvoice.R.layout.activity_summoner_selection;
    }

    @Override
    protected void setUi() {
    }

    @Override
    protected void populate() {
    }

    @Override
    protected void init() {
        replaceFragment(com.android.lolvoice.R.id.summoner_selection_container,
                SummonerSelectionFragment.newInstance());
    }

    @Override
    protected void setListeners() {
    }
}
