package de.flashheart.lara.listeners;


import de.flashheart.lara.interfaces.GamemodeListenerInterface;
import de.flashheart.lara.jobs.DelayedGameStartJob;
import de.flashheart.lara.jobs.GametimesUpJob;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.quartz.*;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
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
    private JobDataMap jobDataMap;

    Logger logger = Logger.getLogger(getClass());
    private int buttonPressedCount = 1;
    private int gamemode, gamemodeToStartAfterDelay;

    JobKey delayJobKey = null;
    JobKey gametimerJobKey = null;

    public GamemodeListener(Scheduler scheduler, int delayInSeconds, int gameLengthInSeconds) throws SchedulerException {
        this.scheduler = scheduler;
        this.gameLengthInSeconds = gameLengthInSeconds;
        this.gamemode = GAME_PRE_GAME;
        this.delayInSeconds = delayInSeconds;
        jobDataMap = new JobDataMap();
        this.scheduler.getContext().put("gamemodelistener", this);
    }

    @Override
    public void targetDestroyed() {
        setGamemode(GAME_OVER_TARGET_DESTROYED);
    }

    @Override
    public void targetDefended() {
        setGamemode(GAME_OVER_TARGET_DEFENDED);
    }

    @Override
    public void startGame() {
        setGamemode(gamemodeToStartAfterDelay);
    }

    boolean isGameOver() {
        return gamemode == GAME_OVER_TARGET_DEFENDED || gamemode == GAME_OVER_TARGET_DESTROYED;
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

    private void setGamemode(int gamemode) {
        switch (gamemode) {
            case GAME_PRE_GAME: {

            }
            case GAME_ABOUT_TO_RUN: {
                gamemodeToStartAfterDelay = GAME_RUNNING;
                setupDelayJobs();
            }
            case GAME_RUNNING: {
                setupGametimerJobs();
            }
            case GAME_ABOUT_TO_RUN_WITH_AUTOREPAIR: {
                gamemodeToStartAfterDelay = GAME_RUNNING_WITH_AUTOREPAIR;
                setupDelayJobs();
            }
            case GAME_RUNNING_WITH_AUTOREPAIR: {
                setupGametimerJobs();
            }
            case GAME_OVER_TARGET_DEFENDED: {

            }
            case GAME_OVER_TARGET_DESTROYED: {

            }
            default: {

            }
        }
    }

    void deleteJobs() throws SchedulerException {
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

    void setupGametimerJobs() {
        try {
            deleteJobs();
            JobDetail job = newJob(GametimesUpJob.class)
                    .withIdentity(GametimesUpJob.name, "group1")
                    .build();
            gametimerJobKey = job.getKey();

            // Trigger the job to run now, and then repeat every 40 seconds
            Trigger trigger = newTrigger()
                    .withIdentity(GametimesUpJob.name + "-trigger", "group1")
                    .startAt(new DateTime().plusSeconds(gameLengthInSeconds).toDate())
                    .withSchedule(simpleSchedule().withRepeatCount(1))
                    .build();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            logger.fatal(e);
            System.exit(0);
        }
    }

    void setupDelayJobs() {
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
                    .withSchedule(simpleSchedule().withRepeatCount(1))
                    .build();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            logger.fatal(e);
            System.exit(0);
        }
    }
}
