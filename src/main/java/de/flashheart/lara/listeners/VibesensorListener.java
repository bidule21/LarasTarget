package de.flashheart.lara.listeners;

import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Created by tloehr on 29.06.17.
 */
public class VibesensorListener implements GpioPinListenerDigital {
    private final GamemodeListener gamemodeListener;
    private final long HEALTH_CHANGE_PER_HIT;
    Logger logger = Logger.getLogger(getClass());

    public VibesensorListener(GamemodeListener gamemodeListener, Level level, long healthChangePerHit) {
        super();
        this.gamemodeListener = gamemodeListener;
        HEALTH_CHANGE_PER_HIT = healthChangePerHit;
        logger.setLevel(level);
    }


    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
//        logger.debug(event);

        if (event.getState() == PinState.HIGH) {
//            logger.debug("Damage detected");
            gamemodeListener.healthChangedBy(HEALTH_CHANGE_PER_HIT);
        }
    }
}
