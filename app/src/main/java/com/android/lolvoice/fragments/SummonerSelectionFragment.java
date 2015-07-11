package com.android.lolvoice.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.android.lolvoice.activities.HomeActivity;
import com.android.lolvoice.adapters.SummonersAdapter;
import com.android.lolvoice.models.SummonerInfo;
import com.android.lolvoice.utils.CurrentGameUtils;
import com.android.lolvoice.utils.SummonerUtils;
import com.robrua.orianna.api.core.AsyncRiotAPI;
import com.robrua.orianna.type.api.Action;
import com.robrua.orianna.type.core.currentgame.CurrentGame;
import com.robrua.orianna.type.core.summoner.Summoner;
import com.robrua.orianna.type.exception.APIException;

import java.util.LinkedList;
import java.util.List;


public class SummonerSelectionFragment extends BaseFragment {

    private EditText mNewSummoner;
    private View mAddSummoner;
    private View mRandomSummoner;
    private ListView mSummoners;
    private SummonersAdapter mSummonerAdapter;
    private List<SummonerInfo> mSummonersList;

    public static SummonerSelectionFragment newInstance() {
        SummonerSelectionFragment fragment = new SummonerSelectionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int layout() {
        return com.android.lolvoice.R.layout.fragment_summoner_select;
    }

    @Override
    protected void setUi(View v) {
        mNewSummoner = (EditText) v.findViewById(com.android.lolvoice.R.id.summoner_selection_new);
        mAddSummoner = v.findViewById(com.android.lolvoice.R.id.summoner_selection_send);
        mSummoners = (ListView) v.findViewById(com.android.lolvoice.R.id.collection_view);
        mRandomSummoner = v.findViewById(com.android.lolvoice.R.id.summoner_selection_random);
    }

    @Override
    protected void populate() {
    }

    @Override
    protected void init() {
        mSummonersList = new LinkedList<SummonerInfo>(SummonerUtils.getStoredSummoners());
        mSummonerAdapter = new SummonersAdapter(getActivity(), mSummonersList);
        mSummoners.setAdapter(mSummonerAdapter);
    }

    @Override
    protected void setListeners() {
        mAddSummoner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncRiotAPI.getSummonerByName(new Action<Summoner>() {
                    @Override
                    public void handle(APIException exception) {
                        showToast(com.android.lolvoice.R.string.invalid_name);
                    }

                    @Override
                    public void perform(Summoner summoner) {
                        mSummonersList.add(new SummonerInfo(summoner));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mSummonerAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }, mNewSummoner.getText().toString());
            }
        });
        mSummoners.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SummonerUtils.setSummoner(mSummonersList.get(position));
                getActivity().startActivity(new Intent(getActivity(), HomeActivity.class));
            }
        });
        mRandomSummoner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncRiotAPI.getFeaturedGames(new Action<List<CurrentGame>>() {
                    @Override
                    public void handle(APIException exception) {
                    }

                    @Override
                    public void perform(List<CurrentGame> responseData) {
                        SummonerUtils.setSummoner(null);
                        CurrentGameUtils.setCurrentGame(responseData.get(0));
                        getActivity().startActivity(new Intent(getActivity(), HomeActivity.class));
                    }
                });
            }
        });
    }
}
