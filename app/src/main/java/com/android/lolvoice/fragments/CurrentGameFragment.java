package com.android.lolvoice.fragments;

import android.content.Intent;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.view.View;

import com.android.lolvoice.Configuration;
import com.android.lolvoice.R;
import com.android.lolvoice.activities.CurrentGameActivity;
import com.android.lolvoice.helper.CooldownsHelper;
import com.android.lolvoice.models.ChampionInfo;
import com.android.lolvoice.models.Role;
import com.android.lolvoice.models.Spell;
import com.android.lolvoice.models.SummonerSpellInfo;
import com.android.lolvoice.models.event.SpeakEvent;
import com.android.lolvoice.utils.ChampionsUtils;
import com.android.lolvoice.utils.CurrentGameUtils;
import com.android.lolvoice.utils.SummonerUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.robrua.orianna.type.core.currentgame.CurrentGame;
import com.robrua.orianna.type.dto.currentgame.Participant;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class CurrentGameFragment extends BaseFragment {

    private static final int CHAMPION_COUNT = 5;
    private static final String CHAMPION = "champion_";
    private static final String ID = "id";

    @Bind(R.id.current_game_speak) View mSpeak;
    private List<ChampionPortrait> mChampionPortraits;
    private CooldownsHelper mCooldownsHelper;
    private Integer mSelectedRole;
    private Role mRequestedRole;

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
        super.init();
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
                if (mCooldownsHelper == null) startCooldownTracking();
                requestRole();
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
        if (portrait.mChampion == null) {
            return;
        }
        portrait.mChampionImage.setImageURI(Uri.parse(Configuration.IMAGES_URL
                + portrait.mChampion.getImageGroup() + "/"
                + portrait.mChampion.getImageFull()));
        portrait.mSummonerSpell1 =
                ChampionsUtils.getSummonerSpellById((int) (long) player.getSpell1Id());
        portrait.mSpell1.setImageURI(Uri.parse(Configuration.IMAGES_URL
                + portrait.mSummonerSpell1.getImageGroup() + "/"
                + portrait.mSummonerSpell1.getImageFull()));
        portrait.mSummonerSpell2 =
                ChampionsUtils.getSummonerSpellById((int) (long) player.getSpell2Id());
        portrait.mSpell2.setImageURI(Uri.parse(Configuration.IMAGES_URL
                + portrait.mSummonerSpell2.getImageGroup() + "/"
                + portrait.mSummonerSpell2.getImageFull()));
    }

    private void startCooldownTracking() {
        mCooldownsHelper = new CooldownsHelper();
        for (int i = 0 ; i < Role.COUNT ; i++) {
            ChampionPortrait portrait = mChampionPortraits.get(i);
            mCooldownsHelper.setRole(ChampionsUtils.getRole(i),
                    ChampionsUtils.getSpell(portrait.mSummonerSpell1.getSummonerSpellId()),
                    ChampionsUtils.getSpell(portrait.mSummonerSpell2.getSummonerSpellId()));
        }
        mCooldownsHelper.start();
    }

    public void requestRole() {
        EventBus.getDefault().post(new SpeakEvent("Name role"));
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        getActivity().startActivityForResult(intent, CurrentGameActivity.REQUEST_ROLE);
    }

    public void onRoleSelected(Role role) {
        if (role == null) requestRole();
        else {
            mRequestedRole = role;
            requestSpell();
        }
    }

    public void requestSpell() {
        EventBus.getDefault().post(new SpeakEvent("Name spell"));
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        getActivity().startActivityForResult(intent, CurrentGameActivity.REQUEST_SPELL);
    }

    public void onSpellSelected(Spell spell) {
        if (spell == null) requestSpell();
        else {
            mCooldownsHelper.setCooldown(mRequestedRole, spell);
            mRequestedRole = null;
        }
    }

    static class ChampionPortrait {

        ChampionInfo mChampion;
        Participant mParticipant;
        SummonerSpellInfo mSummonerSpell1;
        SummonerSpellInfo mSummonerSpell2;
        View mFrame;
        @Bind(R.id.champion_image) SimpleDraweeView mChampionImage;
        @Bind(R.id.champion_spell_1) SimpleDraweeView mSpell1;
        @Bind(R.id.champion_spell_2) SimpleDraweeView mSpell2;

        public void initViews(View v) {
            ButterKnife.bind(this, v);
        }
    }
}
