package com.android.lolvoice.fragments;

import android.content.Intent;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;

import com.android.lolvoice.Configuration;
import com.android.lolvoice.R;
import com.android.lolvoice.activities.CurrentGameActivity;
import com.android.lolvoice.models.ChampionInfo;
import com.android.lolvoice.models.SummonerSpellInfo;
import com.android.lolvoice.utils.ChampionsUtils;
import com.android.lolvoice.utils.CurrentGameUtils;
import com.android.lolvoice.utils.SummonerUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.robrua.orianna.api.core.RiotAPI;
import com.robrua.orianna.type.core.currentgame.CurrentGame;
import com.robrua.orianna.type.core.staticdata.SummonerSpell;
import com.robrua.orianna.type.dto.currentgame.Participant;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CurrentGameFragment extends BaseFragment {

    private static final int CHAMPION_COUNT = 5;
    private static final String CHAMPION = "champion_";
    private static final String ID = "id";

    @Bind(R.id.current_game_speak) View mSpeak;
    @Bind(R.id.current_game_loading) View mLoadingView;
    private int mLoading;
    private List<ChampionPortrait> mChampionPortraits;
    private Integer mSelectedRole;

    public static CurrentGameFragment newInstance() {
        CurrentGameFragment f = new CurrentGameFragment();
        return f;
    }

    @Override
    protected int layout() {
        return R.layout.fragment_home;
    }

    @Override
    protected void setUi(View v) {
        mChampionPortraits = new ArrayList<>(CHAMPION_COUNT);
        for (int i = 0 ; i < CHAMPION_COUNT ; i++) {
            int championId = getResources().getIdentifier(CHAMPION + (i + 1), ID,
                    getActivity().getPackageName());
            ChampionPortrait portrait = new ChampionPortrait();
            portrait.mFrame = v.findViewById(championId);
            portrait.mFrame.setTag(i);
            portrait.initViews(portrait.mFrame);
            mChampionPortraits.add(portrait);
        }
    }

    @Override
    protected void init() {
        CurrentGame currentGame = CurrentGameUtils.getCurrentGame();
        loadChampionImages(getEnemies(currentGame, SummonerUtils.getSummonerName()));
    }

    private long getPlayerTeamId(List<Participant> players, String summoner) {
        for (Participant player : players)
            if (player.getSummonerName().equals(summoner))
                return player.getTeamId();
        return players.get(0).getTeamId();
    }

    private List<Participant> getEnemies(CurrentGame currentGame, String summonerName) {
        List<Participant> enemies = new ArrayList<Participant>();
        long teamId = getPlayerTeamId(currentGame.getDto().getParticipants(), summonerName);
        for (Participant player : currentGame.getDto().getParticipants())
            if (player.getTeamId() != teamId)
                enemies.add(player);
        return enemies;
    }

    @Override
    protected void setListeners() {
        mSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                startActivityForResult(intent, CurrentGameActivity.SPEECH_REQUEST_CODE);
            }
        });
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedRole == null) {
                    mSelectedRole = (int) v.getTag();
                    mChampionPortraits.get(mSelectedRole).mFrame.setSelected(true);
                } else {
                    int newSelected = (int) v.getTag();
                    if (newSelected != mSelectedRole) {
                        ChampionPortrait portrait1 = mChampionPortraits.get(mSelectedRole);
                        ChampionPortrait portrait2 = mChampionPortraits.get(newSelected);
                        ChampionInfo champion = portrait1.mChampion;
                        Participant participant = portrait1.mParticipant;
                        portrait1.mChampion = portrait2.mChampion;
                        portrait1.mParticipant = portrait2.mParticipant;
                        portrait2.mChampion = champion;
                        portrait2.mParticipant = participant;
                        loadChampionData(portrait1);
                        loadChampionData(portrait2);
                    }
                    mChampionPortraits.get(mSelectedRole).mFrame.setSelected(false);
                    mSelectedRole = null;
                }
            }
        };
        for (ChampionPortrait portrait : mChampionPortraits)
            portrait.mFrame.setOnClickListener(listener);
    }

    private void loadChampionImages(final List<Participant> players) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLoadingView.setVisibility(View.GONE);
                for (int i = 0; i < mChampionPortraits.size(); i++) {
                    ChampionPortrait portrait = mChampionPortraits.get(i);
                    portrait.mParticipant = players.get(i);
                    portrait.mChampion = ChampionsUtils.getChampionById(
                            (int) ((long) portrait.mParticipant.getChampionId()));
                    loadChampionData(portrait);
                }
            }
        });
    }

    private void loadChampionData(ChampionPortrait portrait) {
        Participant player = portrait.mParticipant;
        portrait.mChampionImage.setImageURI(Uri.parse(Configuration.IMAGES_URL
                + portrait.mChampion.getImageGroup() + "/"
                + portrait.mChampion.getImageFull()));
        SummonerSpellInfo spell = ChampionsUtils
                .getSummonerSpellById((int)(long) player.getSpell1Id());
        portrait.mSpell1.setImageURI(Uri.parse(Configuration.IMAGES_URL
                + spell.getImageGroup() + "/"
                + spell.getImageFull()));
        spell = ChampionsUtils.getSummonerSpellById((int) (long) player.getSpell2Id());
        portrait.mSpell2.setImageURI(Uri.parse(Configuration.IMAGES_URL
                + spell.getImageGroup() + "/"
                + spell.getImageFull()));
    }

    public void onSpeachResults(List<String> results) {
        for (String s : results) {
            Log.e("Result: ", s);
        }
    }

    static class ChampionPortrait {

        ChampionInfo mChampion;
        Participant mParticipant;
        View mFrame;
        @Bind(R.id.champion_image) SimpleDraweeView mChampionImage;
        @Bind(R.id.champion_spell_1) SimpleDraweeView mSpell1;
        @Bind(R.id.champion_spell_2) SimpleDraweeView mSpell2;

        public void initViews(View v) {
            ButterKnife.bind(this, v);
        }
    }
}
