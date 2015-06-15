package com.android.lolvoice.utils;

import com.robrua.orianna.type.core.currentgame.CurrentGame;

public class CurrentGameUtils {

    private static CurrentGame sCurrentGame;

    public static void setCurrentGame(CurrentGame currentGame) {
        sCurrentGame = currentGame;
    }

    public static CurrentGame getCurrentGame() {
        return sCurrentGame;
    }
}
