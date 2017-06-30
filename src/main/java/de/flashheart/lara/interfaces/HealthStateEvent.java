package de.flashheart.lara.interfaces;

import org.apache.log4j.Level;

/**
 * Created by tloehr on 28.06.17.
 */
public class HealthStateEvent extends MessageEvent {
    private final long health;

    public HealthStateEvent(Object source, Level loglevel, int gameState, long health) {
        super(source, loglevel, gameState);
        this.health = health;
    }

    

}
