package de.flashheart.lara.listeners;

import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tloehr on 29.06.17.
 */
public class VibesensorListener implements GpioPinListenerDigital {
    private final long HEALTH_CHANGE_PER_HIT;
    private List<HealthListener> listeners = new ArrayList<>();
    Logger logger = Logger.getLogger(getClass());

    public VibesensorListener(Level level, long healthChangePerHit) {
        super();
        HEALTH_CHANGE_PER_HIT = healthChangePerHit;
        logger.setLevel(level);
    }

    public void addListener(HealthListener healthListener) {
        listeners.add(healthListener);
    }

    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        logger.debug(event);

        if (event.getState() == PinState.HIGH) {
            logger.debug("Damage detected");
            for (HealthListener hl : listeners) {
                hl.healthChangedBy(HEALTH_CHANGE_PER_HIT);
            }
        }
    }
}
