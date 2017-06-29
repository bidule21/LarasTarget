package main.java.de.flashheart.lara.listeners;


import main.java.de.flashheart.lara.interfaces.GamemodeListenerInterface;

/**
 * Created by tloehr on 29.06.17.
 */
public class GamemodeListener implements GamemodeListenerInterface
{

    public static final int GAME_PRE_GAME = 0;
    public static final int GAME_ABOUT_TO_RUN = 1;
    public static final int GAME_RUNNING = 2;
    public static final int GAME_ABOUT_TO_RUN_WITH_AUTOREPAIR = 5;
    public static final int GAME_RUNNING_WITH_AUTOREPAIR = 6;
    public static final int GAME_OVER_TARGET_DESTROYED = 10;
    public static final int GAME_OVER_TARGET_DEFENDED = 11;

    private final long millisUntilGameStarting;

    private int buttonPressedCount = 1;
    private int gamemode;

    public GamemodeListener(int gamemode, long millisUntilGameStarting) {
        this.gamemode = gamemode;
        this.millisUntilGameStarting = millisUntilGameStarting;
    }

    @Override
    public void targetDestroyed() {
        setGamemode(GAME_OVER_TARGET_DESTROYED);
    }

    @Override
    public void targetDefended() {
        setGamemode(GAME_OVER_TARGET_DEFENDED);
    }

    boolean isGameOver() {
        return gamemode == GAME_OVER_TARGET_DEFENDED || gamemode == GAME_OVER_TARGET_DESTROYED;
    }

    @Override
    public void gamemodeButtonPressed() {

        if (isGameOver()) {
            buttonPressedCount = 1;
            setGamemode(GAME_PRE_GAME);
        } else {
            if (buttonPressedCount == 1) {
                buttonPressedCount = 2;
                setGamemode(GAME_ABOUT_TO_RUN);
            } else if (buttonPressedCount == 2) {
                buttonPressedCount = 3;
                setGamemode(GAME_ABOUT_TO_RUN_WITH_AUTOREPAIR);
            } else if (buttonPressedCount == 3) {
                buttonPressedCount = 1;
                setGamemode(GAME_PRE_GAME);
            }
        }
    }

    void setGamemode(int gamemode) {

    }
}
