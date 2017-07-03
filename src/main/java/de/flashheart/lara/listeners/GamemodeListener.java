package de.flashheart.lara.listeners;


import de.flashheart.lara.interfaces.GamemodeListenerInterface;
import de.flashheart.lara.jobs.DelayedGameStartJob;
import de.flashheart.lara.jobs.GametimeIsUpJob;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.quartz.*;

import java.util.ArrayList;
import java.util.List;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by tloehr on 29.06.17.
 */
public class GamemodeListener implements GamemodeListenerInterface {
    public static final int GAME_PRE_GAME = 0;
    public static final int GAME_ABOUT_TO_RUN = 1;
    public static final int GAME_RUNNING = 2;
    public static final int GAME_ABOUT_TO_RUN_WITH_AUTOREPAIR = 5;
    public static final int GAME_RUNNING_WITH_AUTOREPAIR = 6;
    public static final int GAME_OVER_TARGET_DESTROYED = 10;
    public static final int GAME_OVER_TARGET_DEFENDED = 11;

    private final int delayInSeconds;
    private final Scheduler scheduler;
    private final int gameLengthInSeconds;


    Logger logger = Logger.getLogger(getClass());
    private int buttonPressedCount = 1;
    private int gamemode, gamemodeToStartAfterDelay;


    private List<GamemodeListener> listeners = new ArrayList<>();
    long lasthit = Long.MAX_VALUE;
    long health;
    final long HEALTH;

    JobKey delayJobKey = null;
    JobKey gametimerJobKey = null;


    public GamemodeListener(Scheduler scheduler, int delayInSeconds, int gameLengthInSeconds, long HEALTH) throws SchedulerException {
        this.scheduler = scheduler;
        this.gameLengthInSeconds = gameLengthInSeconds;
        this.HEALTH = HEALTH;
        this.delayInSeconds = delayInSeconds;
        this.scheduler.getContext().put("gamemodelistener", this);
        setGamemode(GAME_PRE_GAME);
    }

    @Override
    public void healthChangedBy(long deltaHealth) {
        if (!isGameRunning()) return;

        if (deltaHealth < 0) lasthit = System.currentTimeMillis();
        health += deltaHealth;
        if (health > HEALTH) health = HEALTH;
        if (health < 0) health = 0;

        logger.debug("!!! HIT: new health value: " + health);

        if (health == 0) {
            setGamemode(GAME_OVER_TARGET_DESTROYED);
        }
    }

    @Override
    public void targetDefended() {
        setGamemode(GAME_OVER_TARGET_DEFENDED);
    }

    @Override
    public void startGame() {
        setGamemode(gamemodeToStartAfterDelay);
    }

    private boolean isGameOver() {
        return gamemode == GAME_OVER_TARGET_DEFENDED || gamemode == GAME_OVER_TARGET_DESTROYED;
    }

    private boolean isGameRunning() {
        return gamemode == GAME_RUNNING || gamemode == GAME_RUNNING_WITH_AUTOREPAIR;
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

    private void setGamemode(int newgamemode) {
        gamemode = newgamemode;
        switch (gamemode) {
            case GAME_PRE_GAME: {
                logger.debug("\n" +
                        "  ____  ____  _____ ____    _    __  __ _____ \n" +
                        " |  _ \\|  _ \\| ____/ ___|  / \\  |  \\/  | ____|\n" +
                        " | |_) | |_) |  _|| |  _  / _ \\ | |\\/| |  _|  \n" +
                        " |  __/|  _ <| |__| |_| |/ ___ \\| |  | | |___ \n" +
                        " |_|   |_| \\_\\_____\\____/_/   \\_\\_|  |_|_____|\n" +
                        "                                              ");
                break;
            }
            case GAME_ABOUT_TO_RUN: {
                logger.debug("\n" +
                        "   ____    _    __  __ _____      _    ____   ___  _   _ _____   _____ ___    ____  _   _ _   _ \n" +
                        "  / ___|  / \\  |  \\/  | ____|    / \\  | __ ) / _ \\| | | |_   _| |_   _/ _ \\  |  _ \\| | | | \\ | |\n" +
                        " | |  _  / _ \\ | |\\/| |  _|     / _ \\ |  _ \\| | | | | | | | |     | || | | | | |_) | | | |  \\| |\n" +
                        " | |_| |/ ___ \\| |  | | |___   / ___ \\| |_) | |_| | |_| | | |     | || |_| | |  _ <| |_| | |\\  |\n" +
                        "  \\____/_/   \\_\\_|  |_|_____| /_/   \\_\\____/ \\___/ \\___/  |_|     |_| \\___/  |_| \\_\\\\___/|_| \\_|\n" +
                        "                                                                                                ");
                gamemodeToStartAfterDelay = GAME_RUNNING;
                health = HEALTH; // the box starts completely healthy
                setupDelayJobs();
                break;
            }
            case GAME_RUNNING: {
                logger.debug("\n" +
                        "   ____    _    __  __ _____   ____  _   _ _   _ _   _ ___ _   _  ____ \n" +
                        "  / ___|  / \\  |  \\/  | ____| |  _ \\| | | | \\ | | \\ | |_ _| \\ | |/ ___|\n" +
                        " | |  _  / _ \\ | |\\/| |  _|   | |_) | | | |  \\| |  \\| || ||  \\| | |  _ \n" +
                        " | |_| |/ ___ \\| |  | | |___  |  _ <| |_| | |\\  | |\\  || || |\\  | |_| |\n" +
                        "  \\____/_/   \\_\\_|  |_|_____| |_| \\_\\\\___/|_| \\_|_| \\_|___|_| \\_|\\____|\n" +
                        "                                                                       ");
                setupGametimerJobs();
                break;
            }
            case GAME_ABOUT_TO_RUN_WITH_AUTOREPAIR: {
                logger.debug("\n" +
                        "   ____    _    __  __ _____      _    ____   ___  _   _ _____   _____ ___    ____  _   _ _   _      _   _   _ _____ ___  ____  _____ ____   _    ___ ____  \n" +
                        "  / ___|  / \\  |  \\/  | ____|    / \\  | __ ) / _ \\| | | |_   _| |_   _/ _ \\  |  _ \\| | | | \\ | |    / \\ | | | |_   _/ _ \\|  _ \\| ____|  _ \\ / \\  |_ _|  _ \\ \n" +
                        " | |  _  / _ \\ | |\\/| |  _|     / _ \\ |  _ \\| | | | | | | | |     | || | | | | |_) | | | |  \\| |   / _ \\| | | | | || | | | |_) |  _| | |_) / _ \\  | || |_) |\n" +
                        " | |_| |/ ___ \\| |  | | |___   / ___ \\| |_) | |_| | |_| | | |     | || |_| | |  _ <| |_| | |\\  |  / ___ \\ |_| | | || |_| |  _ <| |___|  __/ ___ \\ | ||  _ < \n" +
                        "  \\____/_/   \\_\\_|  |_|_____| /_/   \\_\\____/ \\___/ \\___/  |_|     |_| \\___/  |_| \\_\\\\___/|_| \\_| /_/   \\_\\___/  |_| \\___/|_| \\_\\_____|_| /_/   \\_\\___|_| \\_\\\n" +
                        "                                                                                                                                                            ");
                health = HEALTH; // the box starts completely healthy
                gamemodeToStartAfterDelay = GAME_RUNNING_WITH_AUTOREPAIR;
                setupDelayJobs();
                break;
            }
            case GAME_RUNNING_WITH_AUTOREPAIR: {
                logger.debug("\n" +
                        "   ____    _    __  __ _____   ____  _   _ _   _ _   _ ___ _   _  ____      _   _   _ _____ ___  ____  _____ ____   _    ___ ____  \n" +
                        "  / ___|  / \\  |  \\/  | ____| |  _ \\| | | | \\ | | \\ | |_ _| \\ | |/ ___|    / \\ | | | |_   _/ _ \\|  _ \\| ____|  _ \\ / \\  |_ _|  _ \\ \n" +
                        " | |  _  / _ \\ | |\\/| |  _|   | |_) | | | |  \\| |  \\| || ||  \\| | |  _    / _ \\| | | | | || | | | |_) |  _| | |_) / _ \\  | || |_) |\n" +
                        " | |_| |/ ___ \\| |  | | |___  |  _ <| |_| | |\\  | |\\  || || |\\  | |_| |  / ___ \\ |_| | | || |_| |  _ <| |___|  __/ ___ \\ | ||  _ < \n" +
                        "  \\____/_/   \\_\\_|  |_|_____| |_| \\_\\\\___/|_| \\_|_| \\_|___|_| \\_|\\____| /_/   \\_\\___/  |_| \\___/|_| \\_\\_____|_| /_/   \\_\\___|_| \\_\\\n" +
                        "                                                                                                                                   ");
                setupGametimerJobs();
                break;
            }
            case GAME_OVER_TARGET_DEFENDED: {
                logger.debug("\n" +
                        "  _____  _    ____   ____ _____ _____   ____  _____ _____ _____ _   _ ____  _____ ____  \n" +
                        " |_   _|/ \\  |  _ \\ / ___| ____|_   _| |  _ \\| ____|  ___| ____| \\ | |  _ \\| ____|  _ \\ \n" +
                        "   | | / _ \\ | |_) | |  _|  _|   | |   | | | |  _| | |_  |  _| |  \\| | | | |  _| | | | |\n" +
                        "   | |/ ___ \\|  _ <| |_| | |___  | |   | |_| | |___|  _| | |___| |\\  | |_| | |___| |_| |\n" +
                        "   |_/_/   \\_\\_| \\_\\\\____|_____| |_|   |____/|_____|_|   |_____|_| \\_|____/|_____|____/ \n" +
                        "                                                                                        ");
                break;
            }
            case GAME_OVER_TARGET_DESTROYED: {
                logger.debug("\n" +
                        "  _____  _    ____   ____ _____ _____   ____  _____ ____ _____ ____   _____   _______ ____  \n" +
                        " |_   _|/ \\  |  _ \\ / ___| ____|_   _| |  _ \\| ____/ ___|_   _|  _ \\ / _ \\ \\ / / ____|  _ \\ \n" +
                        "   | | / _ \\ | |_) | |  _|  _|   | |   | | | |  _| \\___ \\ | | | |_) | | | \\ V /|  _| | | | |\n" +
                        "   | |/ ___ \\|  _ <| |_| | |___  | |   | |_| | |___ ___) || | |  _ <| |_| || | | |___| |_| |\n" +
                        "   |_/_/   \\_\\_| \\_\\\\____|_____| |_|   |____/|_____|____/ |_| |_| \\_\\\\___/ |_| |_____|____/ \n" +
                        "                                                                                             ");
                break;
            }
            default: {

            }
        }
    }

    private void deleteJobs() throws SchedulerException {
        if (delayJobKey != null) {
            scheduler.interrupt(delayJobKey);
            scheduler.deleteJob(delayJobKey);
            delayJobKey = null;
        }
        if (gametimerJobKey != null) {
            scheduler.interrupt(gametimerJobKey);
            scheduler.deleteJob(gametimerJobKey);
            gametimerJobKey = null;
        }
    }

    private void setupGametimerJobs() {
        try {
            deleteJobs();
            JobDetail job = newJob(GametimeIsUpJob.class)
                    .withIdentity(GametimeIsUpJob.name, "group1")
                    .build();
            gametimerJobKey = job.getKey();

            // Trigger the job to run now, and then repeat every 40 seconds
            Trigger trigger = newTrigger()
                    .withIdentity(GametimeIsUpJob.name + "-trigger", "group1")
                    .startAt(new DateTime().plusSeconds(gameLengthInSeconds).toDate())
                    .build();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            logger.fatal(e);
            System.exit(0);
        }
    }

    private void setupDelayJobs() {
        try {
            deleteJobs();
            JobDetail job = newJob(DelayedGameStartJob.class)
                    .withIdentity(DelayedGameStartJob.name, "group1")
                    .build();
            delayJobKey = job.getKey();

            // Trigger the job to run now, and then repeat every 40 seconds
            Trigger trigger = newTrigger()
                    .withIdentity(DelayedGameStartJob.name + "-trigger", "group1")
                    .startAt(new DateTime().plusSeconds(delayInSeconds).toDate())
                    .build();
            scheduler.scheduleJob(job, trigger);

        } catch (SchedulerException e) {
            logger.fatal(e);
            System.exit(0);
        }
    }


}
