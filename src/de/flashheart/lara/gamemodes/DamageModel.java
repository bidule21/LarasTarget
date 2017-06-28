package de.flashheart.lara.gamemodes;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinListener;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import de.flashheart.lara.interfaces.GameMode;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Created by tloehr on 28.06.17.
 */
public class DamageModel implements GameMode {

    public static final int GAME_NON_EXISTENT = -1;
    public static final int GAME_PRE_GAME = 0;
    public static final int GAME_GAME_RUNNING = 1;
    public static final int GAME_GAME_OVER_TARGET_DESTROYED = 2;
    public static final int GAME_GAME_OVER_TARGET_ALIVE = 3;

    private final long HEALTH;
    private long health;
    private final Logger logger;
    private static long lasthit = Long.MAX_VALUE;
    private GpioPinListener vibe1Listener;
    private final GpioPinDigitalOutput vibe1;
    private Thread gamethread;

    public DamageModel(GpioPinDigitalOutput vibe1, long DAMAGE_PER_HIT, long HEALTH, Level logLevel) {
        this.vibe1 = vibe1;
        this.HEALTH = HEALTH;
        health = HEALTH;
        logger = Logger.getLogger(getClass());
        logger.setLevel(logLevel);

        vibe1.addListener((GpioPinListenerDigital) event -> {
            logger.debug(event);
            if (event.getState() == PinState.HIGH) {
                lasthit = System.currentTimeMillis();
                logger.debug("Hit detected!!!");
                health -= DAMAGE_PER_HIT;
                if (health <= 0){
                   //notify about destruction
                }
            }
        });


        logger.debug("\n" +
                "  ____   ___   ___   ___   ___   ___   ___  __  __ __  __ __  __ __  __ __  __ __  __ __  __ \n" +
                " | __ ) / _ \\ / _ \\ / _ \\ / _ \\ / _ \\ / _ \\|  \\/  |  \\/  |  \\/  |  \\/  |  \\/  |  \\/  |  \\/  |\n" +
                " |  _ \\| | | | | | | | | | | | | | | | | | | |\\/| | |\\/| | |\\/| | |\\/| | |\\/| | |\\/| | |\\/| |\n" +
                " | |_) | |_| | |_| | |_| | |_| | |_| | |_| | |  | | |  | | |  | | |  | | |  | | |  | | |  | |\n" +
                " |____/ \\___/ \\___/ \\___/ \\___/ \\___/ \\___/|_|  |_|_|  |_|_|  |_|_|  |_|_|  |_|_|  |_|_|  |_|\n" +
                "                                                                                             ");
    }

    @Override
    public void startGame() {

    }

    @Override
    public void stopGame() {

    }
}
