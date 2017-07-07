package de.flashheart.lara.handlers;


import com.pi4j.io.gpio.event.GpioPinListener;
import de.flashheart.lara.jobs.DelayedGameStartJob;
import de.flashheart.lara.jobs.GametimeIsUpJob;
import de.flashheart.lara.jobs.GametimeNotificationJob;
import de.flashheart.lara.jobs.PinHandlerRGBJob;
import de.flashheart.lara.tools.MyGpioPinDigitalOutput;
import de.flashheart.lara.tools.MyGpioPinPwmOutput;
import de.flashheart.lara.tools.RGBBean;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.quartz.*;

import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by tloehr on 29.06.17.
 */
public class GamemodeHandler implements GpioPinListener {
    public static final int GAME_PRE_GAME = 0;
    public static final int GAME_ABOUT_TO_RUN = 1;
    public static final int GAME_RUNNING = 2;
    public static final int GAME_ABOUT_TO_RUN_WITH_AUTOREPAIR = 5;
    public static final int GAME_RUNNING_WITH_AUTOREPAIR = 6;
    public static final int GAME_OVER_TARGET_DESTROYED = 10;
    public static final int GAME_OVER_TARGET_DEFENDED = 11;

    private final Color[] colors = new Color[]{Color.red, new Color(255, 10, 0), new Color(255, 25, 0), new Color(255, 50, 0), Color.green};
    private final int delayInSeconds;
    private final Scheduler scheduler;
    private int lastpos = -1; // damit die LED Jobs nicht zu oft unnötig geändert werden

    Logger logger = Logger.getLogger(getClass());
    private int buttonPressedCount = 1;
    private int gamemode, gamemodeToStartAfterDelay;
    private final MyGpioPinDigitalOutput pinSiren;
    private final MyGpioPinPwmOutput pwmRed;
    private final MyGpioPinPwmOutput pwmGreen;
    private final MyGpioPinPwmOutput pwmBlue;

    long lasthit = Long.MAX_VALUE;
    long health;
    final long HEALTH;

    private JobKey delayJobKey = null;
    private JobKey gametimeIsUpJobKey = null;
    private JobKey gametimerJobKey = null;
    private JobKey rgbLEDJobKey;

    private final long gameLengthInMillis;
    private long gameStartedAt, gametimer, gametimeRemaining, gametimeRemainingInPercent, lastGametimeRemainingInPercent = -1;
    private int minutes;
    private Color color;
    private  ArrayList<RGBBean> ledpattern ;

    public GamemodeHandler(MyGpioPinDigitalOutput pinSiren, MyGpioPinPwmOutput pwmRed, MyGpioPinPwmOutput pwmGreen, MyGpioPinPwmOutput pwmBlue, Scheduler scheduler, int delayInSeconds, int gameLengthInSeconds, long HEALTH) throws SchedulerException {
        ledpattern = new ArrayList<>();
        this.pinSiren = pinSiren;
        this.pwmRed = pwmRed;
        this.pwmGreen = pwmGreen;
        this.pwmBlue = pwmBlue;
        this.scheduler = scheduler;
        this.gameLengthInMillis = gameLengthInSeconds * 1000;
        this.HEALTH = HEALTH;
        this.delayInSeconds = delayInSeconds;
        this.scheduler.getContext().put("gamemodelistener", this);
        setGamemode(GAME_PRE_GAME);


    }

    public void healthChangedBy(long deltaHealth) {
        if (!isGameRunning()) return;

        if (deltaHealth < 0) lasthit = System.currentTimeMillis();
        health += deltaHealth;
        if (health > HEALTH) health = HEALTH;
        if (health < 0) health = 0;

        logger.debug("!!! HIT: new health value: " + health);

//        long healthpercent = health / HEALTH * 100;
        BigDecimal healthpercent = new BigDecimal(health).divide(new BigDecimal(HEALTH)).multiply(new BigDecimal(100));

        if (health == 0) {
            setGamemode(GAME_OVER_TARGET_DESTROYED);
        } else {
            int pos = new BigDecimal(colors.length).divide(new BigDecimal(100), 2, BigDecimal.ROUND_UP).multiply(healthpercent).intValue();
            if (lastpos != pos) {
                lastpos = pos;

//                ArrayList<RGBBean> ledpattern = new ArrayList<>();
//                ledpattern.add(new RGBBean(pwmRed, pwmGreen, pwmBlue, colors[pos], 500l));


                ArrayList<RGBBean> ledpattern = new ArrayList<>();
                for (int min = 0; min < minutes; min++) {
                    ledpattern.add(new RGBBean(pwmRed, pwmGreen, pwmBlue, colors[pos], 500l));
                    ledpattern.add(new RGBBean(pwmRed, pwmGreen, pwmBlue, Color.BLACK, 100l));
                }
                ledpattern.add(new RGBBean(pwmRed, pwmGreen, pwmBlue, Color.BLACK, 1500l));

                color = colors[pos];
                setRGBLedJob(colors[pos]);
//                gametimeNotification();
            }

        }
    }


    public void targetDefended() {
        setGamemode(GAME_OVER_TARGET_DEFENDED);
    }

    public void gametimeNotification() {
        if (gameStartedAt == -1l) return;
        gametimer = System.currentTimeMillis() - gameStartedAt;
        gametimeRemaining = gameStartedAt + gameLengthInMillis - gametimer;
//        gametimeRemainingInPercent = new BigDecimal(gametimer).divide(new BigDecimal(gameLengthInMillis), 2, BigDecimal.ROUND_UP).multiply(new BigDecimal(100)).setScale(-1, RoundingMode.HALF_UP).longValue();
        DateTime dt = new DateTime(gameLengthInMillis - gametimer, DateTimeZone.UTC).plusMillis(20);
        logger.debug(dt);
        minutes = dt.getMinuteOfHour();
//        tenseconds = dt.getSecondOfMinute() / 10;

        setRGBLedJob();
    }


    public void startGame() {
        setGamemode(gamemodeToStartAfterDelay);
    }

    private boolean isGameOver() {
        return gamemode == GAME_OVER_TARGET_DEFENDED || gamemode == GAME_OVER_TARGET_DESTROYED;
    }

    private boolean isGameRunning() {
        return gamemode == GAME_RUNNING || gamemode == GAME_RUNNING_WITH_AUTOREPAIR;
    }


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
                gameStartedAt = -1l;
                gametimer = -1l;
                gametimeRemaining = -1l;

                ArrayList<RGBBean> ledpattern = new ArrayList<>();
                ledpattern.add(new RGBBean(pwmRed, pwmGreen, pwmBlue, Color.BLUE, 1000l));
                ledpattern.add(new RGBBean(pwmRed, pwmGreen, pwmBlue, Color.black, 1000l));
                setRGBLedJob(ledpattern, Integer.MAX_VALUE);
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

                ArrayList<RGBBean> ledpattern = new ArrayList<>();
                ledpattern.add(new RGBBean(pwmRed, pwmGreen, pwmBlue, Color.RED, 1000l));
                ledpattern.add(new RGBBean(pwmRed, pwmGreen, pwmBlue, Color.GREEN, 1000l));
                setRGBLedJob(ledpattern, Integer.MAX_VALUE);

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
                gameStartedAt = System.currentTimeMillis();
                color = Color.green;
                setupGametimerJobs();

//                gametimeNotification();


                setupGameTimeIsUpJobs();
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

                ArrayList<RGBBean> ledpattern = new ArrayList<>();
                ledpattern.add(new RGBBean(pwmRed, pwmGreen, pwmBlue, Color.BLUE, 1000l));
                ledpattern.add(new RGBBean(pwmRed, pwmGreen, pwmBlue, Color.WHITE, 1000l));

                setRGBLedJob(ledpattern, Integer.MAX_VALUE);

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
                gameStartedAt = System.currentTimeMillis();
                color = Color.green;
                setRGBLedJob();

                setupGametimerJobs();
                setupGameTimeIsUpJobs();
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
                try {
                    deleteAllJobs();
                } catch (SchedulerException e) {
                    e.printStackTrace();
                }
                gameStartedAt = -1l;
                gametimer = -1l;
                gametimeRemaining = -1l;
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
                try {
                    deleteAllJobs();
                } catch (SchedulerException e) {
                    e.printStackTrace();
                }
                gameStartedAt = -1l;
                gametimer = -1l;
                gametimeRemaining = -1l;
                break;
            }
            default: {

            }
        }
    }

    private void deleteAllJobs() throws SchedulerException {
        deleteTimerJobs();
        if (gametimerJobKey != null) {
            scheduler.interrupt(gametimerJobKey);
            scheduler.deleteJob(gametimerJobKey);
            gametimerJobKey = null;
        }
        if (rgbLEDJobKey != null) {
            scheduler.interrupt(rgbLEDJobKey);
            scheduler.deleteJob(rgbLEDJobKey);
            rgbLEDJobKey = null;
        }
    }

    private void deleteTimerJobs() throws SchedulerException {
        if (delayJobKey != null) {
            scheduler.interrupt(delayJobKey);
            scheduler.deleteJob(delayJobKey);
            delayJobKey = null;
        }
        if (gametimeIsUpJobKey != null) {
            scheduler.interrupt(gametimeIsUpJobKey);
            scheduler.deleteJob(gametimeIsUpJobKey);
            gametimeIsUpJobKey = null;
        }
//        if (gametimerJobKey != null) {
//            scheduler.interrupt(gametimerJobKey);
//            scheduler.deleteJob(gametimerJobKey);
//            gametimerJobKey = null;
//        }
    }

    private void setupGametimerJobs() {
        try {
            if (gametimerJobKey != null) {
                scheduler.interrupt(gametimerJobKey);
                scheduler.deleteJob(gametimerJobKey);
                gametimerJobKey = null;
            }

            JobDetail job = newJob(GametimeNotificationJob.class)
                    .withIdentity(GametimeNotificationJob.name, "group1")
                    .build();
            gametimerJobKey = job.getKey();

            // Trigger the job to run now, and then repeat every 40 seconds
            Trigger trigger = newTrigger()
                    .withIdentity(GametimeNotificationJob.name + "-trigger", "group1")
                    .withSchedule(simpleSchedule().withRepeatCount(Integer.MAX_VALUE).withIntervalInSeconds(30))
                    .startNow()
                    .build();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            logger.fatal(e);
            System.exit(0);
        }
    }


    private void setupGameTimeIsUpJobs() {
        try {
            deleteTimerJobs();
            JobDetail job = newJob(GametimeIsUpJob.class)
                    .withIdentity(GametimeIsUpJob.name, "group1")
                    .build();
            gametimeIsUpJobKey = job.getKey();

            // Trigger the job to run now, and then repeat every 40 seconds
            Trigger trigger = newTrigger()
                    .withIdentity(GametimeIsUpJob.name + "-trigger", "group1")
                    .startAt(new DateTime().plusMillis(new Long(gameLengthInMillis).intValue()).toDate())
                    .build();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            logger.fatal(e);
            System.exit(0);
        }
    }

    private void setupDelayJobs() {
        try {
            deleteTimerJobs();
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

//    // Pattern: redValue, greenValue, blueValue, hold4ms usw.
//    private void setRGBLedJob(Object pattern, int repeat) {
//        try {
//
//            if (rgbLEDJobKey != null) {
//                scheduler.interrupt(rgbLEDJobKey);
//                scheduler.deleteJob(rgbLEDJobKey);
//                rgbLEDJobKey = null;
//            }
//
//            JobDetail job = newJob(PinHandlerRGBJob.class)
//                    .withIdentity("rgbhandler1", "group1")
//                    .build();
//
//            rgbLEDJobKey = job.getKey();
//
//            job.getJobDataMap().put("ledpattern", pattern);
//
//            // Trigger the job to run now, and then repeat every 40 seconds
//            Trigger trigger = newTrigger()
//                    .withIdentity("rgbhandler1-trigger", "group1")
//                    .withSchedule(simpleSchedule().withRepeatCount(repeat).withIntervalInMilliseconds(1))
//                    .startNow()
//                    .build();
//
//            scheduler.scheduleJob(job, trigger);
//
//        } catch (SchedulerException e) {
//            e.printStackTrace();
//            logger.fatal(e);
//            System.exit(0);
//        }
//
//    }


    private void setRGBLedJob() {
        try {
            // todo: lösen mit einer Pattern-Klasse die auf Farbänderungen reagieren kann.
            if (rgbLEDJobKey != null) {
                scheduler.interrupt(rgbLEDJobKey);
                // todo: geht das ?
                JobDetail jobDetail = scheduler.getJobDetail(rgbLEDJobKey);
                jobDetail.getJobDataMap().put("ledpattern", ledpattern);
                scheduler.addJob(jobDetail, true);
            } else {
                JobDetail job = newJob(PinHandlerRGBJob.class)
                        .withIdentity("rgbhandler1", "group1")
                        .build();

                rgbLEDJobKey = job.getKey();

                job.getJobDataMap().put("ledpattern", ledpattern);

                // Trigger the job to run now, and then repeat every 40 seconds
                Trigger trigger = newTrigger()
                        .withIdentity("rgbhandler1-trigger", "group1")
                        .withSchedule(simpleSchedule().withRepeatCount(Integer.MAX_VALUE).withIntervalInMilliseconds(1))
                        .startNow()
                        .build();

                scheduler.scheduleJob(job, trigger);
            }


        } catch (SchedulerException e) {
            e.printStackTrace();
            logger.fatal(e);
            System.exit(0);
        }

    }


}
