package de.flashheart.lara.events;

import java.util.EventObject;

/**
 * Created by tloehr on 29.06.17.
 */
public class GamemodeEvent extends EventObject {
    private int gamemode;

    public GamemodeEvent(Object source, int gamemode) {
        super(source);
        this.gamemode = gamemode;
    }
}
