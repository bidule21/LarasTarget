package main.java.de.flashheart.lara.interfaces;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.EventObject;


/**
 * Created by tloehr on 25.04.15.
 */
public class MessageEvent extends EventObject {


    protected final int gameState;
    protected final long eventTime;
    protected final Logger logger = Logger.getLogger(getClass());


    public MessageEvent(Object source, Level loglevel, int gameState) {
        super(source);
        logger.setLevel(loglevel);
        this.gameState = gameState;
        eventTime = System.currentTimeMillis();
    }

    public int getGameState() {
        return gameState;
    }

    public long getEventTime() {
        return eventTime;
    }
}
