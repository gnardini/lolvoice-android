package com.android.lolvoice.activities;

import com.android.lolvoice.R;
import com.android.lolvoice.fragments.SummonerSelectionFragment;

public class SummonerSelectionActivity extends BaseActivity {

    @Override
    protected int layout() {
        return R.layout.activity_summoner_selection;
    }

    @Override
    protected void init() {
        replaceFragment(R.id.summoner_selection_container,
                SummonerSelectionFragment.newInstance());
    }
}
