package main.java.de.flashheart.lara.listeners;


import main.java.de.flashheart.lara.interfaces.HealthListenerInterface;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tloehr on 29.06.17.
 */
public class HealthListener implements HealthListenerInterface {
    private List<GamemodeListener> listeners = new ArrayList<>();
    Logger logger = Logger.getLogger(getClass());
    long lasthit = Long.MAX_VALUE;
    long health;
    final long HEALTH;

    public HealthListener(Level level, long HEALTH) {
        super();
        logger.setLevel(level);
        this.HEALTH = HEALTH;
        health = HEALTH; // initial health setting
    }

    public void addListener(GamemodeListener gamemodeListener) {
        listeners.add(gamemodeListener);
    }

    @Override
    public void healthChangedBy(long deltaHealth) {
        if (deltaHealth < 0) lasthit = System.currentTimeMillis();
        health += deltaHealth;
        if (health > HEALTH) health = HEALTH;
        if (health < 0) health = 0;

        if (health == 0) {
            for (GamemodeListener gml : listeners) {
                gml.targetDestroyed();
            }
        }
    }
}
