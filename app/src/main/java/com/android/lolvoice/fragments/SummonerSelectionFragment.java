package com.android.lolvoice.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.lolvoice.R;
import com.android.lolvoice.activities.CurrentGameActivity;
import com.android.lolvoice.adapters.SummonersAdapter;
import com.android.lolvoice.listeners.CurrentGameLoadingListener;
import com.android.lolvoice.models.SummonerInfo;
import com.android.lolvoice.utils.CurrentGameUtils;
import com.android.lolvoice.utils.SummonerUtils;
import com.robrua.orianna.api.core.AsyncRiotAPI;
import com.robrua.orianna.type.api.Action;
import com.robrua.orianna.type.core.common.Region;
import com.robrua.orianna.type.core.summoner.Summoner;
import com.robrua.orianna.type.exception.APIException;

import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

public class SummonerSelectionFragment extends BaseFragment implements CurrentGameLoadingListener {

    @Bind(R.id.summoner_selection_toolbar)
    Toolbar mToolbar;
    @Bind(R.id.summoner_selection_new)
    EditText mNewSummoner;
    @Bind(R.id.summoner_selection_listview)
    ListView mSummoners;
    @Bind(R.id.summoner_selection_loading)
    View mLoading;
    @Bind(R.id.summoner_selection_loading_text)
    TextView mLoadingText;
    /*
    @Bind(R.id.summoner_selection_region)
    Spinner mRegion;*/
    private SummonersAdapter mSummonerAdapter;
    private List<SummonerInfo> mSummonersList;
    private boolean mStartGameOnLoad;

    public static SummonerSelectionFragment newInstance() {
        SummonerSelectionFragment fragment = new SummonerSelectionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int layout() {
        return R.layout.fragment_summoner_select;
    }

    @Override
    protected void populate() {
        mToolbar.setTitle(R.string.summoner_selection);
    }

    @Override
    protected void init() {
        mSummonersList = new LinkedList<>(SummonerUtils.getStoredSummoners());
        mSummonerAdapter = new SummonersAdapter(getActivity(), mSummonersList);
        mSummoners.setAdapter(mSummonerAdapter);
        /*
        RegionsAdapter regionsAdapter = new RegionsAdapter(getActivity(),
                android.R.layout.simple_spinner_item, Region.values());
        regionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRegion.setAdapter(regionsAdapter);
        */
    }

    @Override
    protected void setListeners() {
        mSummoners.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SummonerInfo summonerInfo = mSummonersList.get(position);
                if (summonerInfo.getSummonerId() == 0) mStartGameOnLoad = true;
                else startSummonerGame(summonerInfo);
            }
        });
    }

    @OnClick(R.id.summoner_selection_send)
    public void addSummoner() {
        String summonerName = mNewSummoner.getText().toString();
        if (summonerRepeated(summonerName)) return;
        mNewSummoner.setText("");
        final SummonerInfo summonerInfo = new SummonerInfo(summonerName, Region.LAS);
        mSummonersList.add(summonerInfo);
        mSummonerAdapter.notifyDataSetChanged();
        AsyncRiotAPI.getSummonerByName(new Action<Summoner>() {
            @Override
            public void handle(APIException exception) {
                mStartGameOnLoad = false;
                mSummonersList.remove(summonerInfo);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast(R.string.invalid_name);
                        mSummonerAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void perform(Summoner summoner) {
                summonerInfo.setSummonerId(summoner.getID());
                if (mStartGameOnLoad) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startSummonerGame(summonerInfo);
                        }
                    });
                }
            }
        }, summonerName);
    }

    private void startSummonerGame(SummonerInfo summonerInfo) {
        startLoading();
        SummonerUtils.setSummoner(summonerInfo);
        CurrentGameUtils.startSummonerGame(getActivity(), this);
    }

    @OnClick(R.id.summoner_selection_random)
    public void startRandomGame() {
        startLoading();
        CurrentGameUtils.startRandomGame(getActivity(), this);
    }

    private boolean summonerRepeated(String summonerName) {
        for (SummonerInfo summoner : mSummonersList)
            if (summoner.getName().equals(summonerName)) return true;
        return false;
    }

    private void startLoading() {
        mLoadingText.setText(R.string.summoner_selection_loading_game);
        mLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void onGameLoaded() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLoadingText.setText(R.string.summoner_selection_fetching_champion);
            }
        });
    }

    @Override
    public void onChampionInfoLoaded() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLoading.setVisibility(View.GONE);
            }
        });
        getActivity().startActivity(new Intent(getActivity(), CurrentGameActivity.class));
    }

    @Override
    public void onLoadingFail() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLoading.setVisibility(View.GONE);
                showToast(R.string.summoner_selection_not_ranked_game);
            }
        });
    }
}
