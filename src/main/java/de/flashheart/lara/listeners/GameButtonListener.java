package de.flashheart.lara.listeners;

import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import de.flashheart.lara.handlers.GamemodeHandler;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tloehr on 05.07.17.
 */
public class GameButtonListener implements GpioPinListenerDigital {

    private List<GamemodeHandler> listeners = new ArrayList<>();
    Logger logger = Logger.getLogger(getClass());

    public GameButtonListener(Level level) {
        super();
        logger.setLevel(level);
    }

    public void addListener(GamemodeHandler gamemodeHandler) {
        listeners.add(gamemodeHandler);
    }

    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        logger.debug(event);

        if (event.getState() == PinState.HIGH) {
            logger.debug("Game Button");
            for (GamemodeHandler gl : listeners) {
                gl.gamemodeButtonPressed();
            }
        }
    }


}
