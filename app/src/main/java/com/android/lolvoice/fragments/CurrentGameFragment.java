package com.android.lolvoice.fragments;

import android.content.Intent;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;

import com.android.lolvoice.utils.CurrentGameUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.android.lolvoice.activities.HomeActivity;
import com.android.lolvoice.services.Callback;
import com.android.lolvoice.utils.ChampionsUtils;
import com.robrua.orianna.type.core.currentgame.CurrentGame;
import com.robrua.orianna.type.core.currentgame.Participant;
import com.robrua.orianna.type.core.staticdata.Champion;

import java.util.ArrayList;
import java.util.List;

import com.android.lolvoice.Configuration;
import com.android.lolvoice.utils.SummonerUtils;

public class CurrentGameFragment extends BaseFragment {

    private static final int CHAMPION_COUNT = 5;
    private static final int ULT = 3;
    private static final String CHAMPION = "champion_";
    private static final String ID = "id";

    private View mSpeak;
    private View mLoadingView;
    private int mLoading;
    private List<ChampionPortrait> mChampionPortraits;

    public static CurrentGameFragment newInstance() {
        CurrentGameFragment f = new CurrentGameFragment();
        return f;
    }

    @Override
    protected int layout() {
        return com.android.lolvoice.R.layout.fragment_home;
    }

    @Override
    protected void setUi(View v) {
        mSpeak = v.findViewById(com.android.lolvoice.R.id.current_game_speak);
        mLoadingView = v.findViewById(com.android.lolvoice.R.id.current_game_loading);
        mChampionPortraits = new ArrayList<>(CHAMPION_COUNT);
        for (int i = 0 ; i < CHAMPION_COUNT ; i++) {
            int championId = getResources().getIdentifier(CHAMPION + (i + 1), ID,
                    getActivity().getPackageName());
            View champion = v.findViewById(championId);
            ChampionPortrait portrait = new ChampionPortrait();
            portrait.mChampionImage = (SimpleDraweeView) champion.findViewById(com.android.lolvoice.R.id.champion_image);
            portrait.mSpell1 = (SimpleDraweeView) champion.findViewById(com.android.lolvoice.R.id.champion_spell_1);
            portrait.mSpell2 = (SimpleDraweeView) champion.findViewById(com.android.lolvoice.R.id.champion_spell_2);
            portrait.mUlt = (SimpleDraweeView) champion.findViewById(com.android.lolvoice.R.id.champion_ult);
            mChampionPortraits.add(portrait);
        }
    }

    @Override
    protected void populate() {
    }

    @Override
    protected void init() {
        mLoading = 0;
        mLoadingView.setVisibility(View.VISIBLE);
        Callback<Void> callback = new Callback<Void>() {
            @Override
            public void callback(Void value) {
                mLoading++;
                if (mLoading == 2) {
                    if (SummonerUtils.hasSummoner()){
                        SummonerUtils.getCurrentGame(new Callback<CurrentGame>() {

                            @Override
                            public void callback(CurrentGame currentGame) {
                                loadChampionImages(getEnemies(currentGame,
                                        SummonerUtils.getSummonerName()));
                            }
                        });
                    } else {
                        loadChampionImages(getEnemies(CurrentGameUtils.getCurrentGame(),null));
                    }
                }

            }

            private long getPlayerTeamId(List<Participant> players, String summoner) {
                for (Participant player : players)
                    if (player.getSummoner().getName().equals(summoner))
                        return player.getTeam().getID();
                return players.get(0).getTeam().getID();
            }

            private List<Participant> getEnemies(CurrentGame currentGame, String summonerName) {
                List<Participant> enemies = new ArrayList<Participant>();
                long teamId = getPlayerTeamId(currentGame.getParticipants(), summonerName);
                for (Participant player : currentGame.getParticipants())
                    if (player.getTeam().getID() != teamId)
                        enemies.add(player);
                return enemies;
            }
        };
        ChampionsUtils.loadChampions(callback);
    }

    @Override
    protected void setListeners() {
        mSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                startActivityForResult(intent, HomeActivity.SPEECH_REQUEST_CODE);
            }
        });
    }

    private void loadChampionImages(final List<Participant> players) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLoadingView.setVisibility(View.GONE);
                for (int i = 0 ; i < mChampionPortraits.size() ; i++) {
                    ChampionPortrait portrait = mChampionPortraits.get(i);
                    Participant player = players.get(i);
                    portrait.mChampion = player.getChampion();
                    portrait.mChampionImage.setImageURI(Uri.parse(Configuration.IMAGES_URL
                            + portrait.mChampion.getImage().getGroup() + "/"
                            + portrait.mChampion.getImage().getFull()));
                    portrait.mSpell1.setImageURI(Uri.parse(Configuration.IMAGES_URL
                            + player.getSummonerSpell1().getImage().getGroup() + "/"
                            + player.getSummonerSpell1().getImage().getFull()));
                    portrait.mSpell2.setImageURI(Uri.parse(Configuration.IMAGES_URL
                            + player.getSummonerSpell2().getImage().getGroup() + "/"
                            + player.getSummonerSpell2().getImage().getFull()));
                    portrait.mUlt.setImageURI(Uri.parse(Configuration.IMAGES_URL
                            + portrait.mChampion.getSpells().get(ULT).getImage().getGroup() + "/"
                            + portrait.mChampion.getSpells().get(ULT).getImage().getFull()));
                }
            }
        });
    }

    public void onSpeachResults(List<String> results) {
        for (String s : results) {
            Log.e("Result: ", s);
        }
    }

    private class ChampionPortrait {

        Champion mChampion;
        SimpleDraweeView mChampionImage;
        SimpleDraweeView mSpell1;
        SimpleDraweeView mSpell2;
        SimpleDraweeView mUlt;
    }
}
