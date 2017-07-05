package de.flashheart.lara.listeners;

import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tloehr on 05.07.17.
 */
public class GameButtonListener implements GpioPinListenerDigital {

    private List<GamemodeListener> listeners = new ArrayList<>();
    Logger logger = Logger.getLogger(getClass());

    public GameButtonListener(Level level) {
        super();
        logger.setLevel(level);
    }

    public void addListener(GamemodeListener gamemodeListener) {
        listeners.add(gamemodeListener);
    }

    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        logger.debug(event);

        if (event.getState() == PinState.HIGH) {
            logger.debug("Game Button");
            for (GamemodeListener gl : listeners) {
                gl.gamemodeButtonPressed();
            }
        }
    }


}
