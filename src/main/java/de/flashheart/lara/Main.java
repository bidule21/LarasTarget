package de.flashheart.lara;

import com.pi4j.io.gpio.*;
import de.flashheart.lara.listeners.GameButtonListener;
import de.flashheart.lara.listeners.GamemodeListener;
import de.flashheart.lara.listeners.VibesensorListener;
import de.flashheart.lara.swing.FrameDebug;
import de.flashheart.lara.tools.MyGpioPinPwmOutput;
import de.flashheart.lara.tools.SortedProperties;
import de.flashheart.lara.tools.Tools;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import javax.swing.*;

public class Main {
    public static GpioController GPIO;
    private static SortedProperties config;

    //    public static long HEALTH = 1000l;
//    public static long HEALTH_CHANGE_PER_HIT = -1l;
    public static long MILLIS_WITHOUT_HIT_BEFORE_RECOVERING = 10000l;
    public static long RECOVERING_HEALTH_PER_SECOND = 10l;    // auf 0 setzen, wenn nicht heilen soll
//    private static long health = HEALTH;

    private static long lasthit = Long.MAX_VALUE;
    private static long lastrecover = 0l;

    private static Scheduler scheduler;
    private static Logger logger;
    private static Level logLevel = Level.DEBUG;
    private static StringBuffer csv = new StringBuffer(100000);
    private static GpioPinDigitalInput vibeSensor1;

    private static VibesensorListener vibesensorListener;
    private static GamemodeListener gamemodeListener;


    /**
     * ## Large headline
     * ### Smaller headline
     * <p>
     * This is a comment that contains `code` parts.
     * <p>
     * Code blocks:
     * <p>
     * ```java
     * int foo = 42;
     * System.out.println(foo);
     * ```
     * <p>
     * Quote blocks:
     * <p>
     * > This is a block quote
     * <p>
     * lists:
     * <p>
     * - first item
     * - second item
     * - third item
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // init log4j (auch log4j.properties wirkt sich hier aus)
        System.setProperty("logs", Tools.getWorkingPath());
        logger = Logger.getLogger("Main");
        logger.setLevel(logLevel);

        // init config
        initCommon();
        initSwingFrame();
        initRaspi();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {

                try {
                    scheduler.shutdown();
                } catch (SchedulerException e) {
                    logger.fatal(e);
                }

//                try {
//                    FileUtils.writeStringToFile(new File("/home/pi/larastarget.csv"), csv.toString());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    logger.trace(e);
//                }
            }
        }));


//        while (health > 0) {
//            if (vibeSensor1.isLow()) {
//                csv.append(String.format("%s;%s;%s\n", System.currentTimeMillis(), 0l, health));
//                logger.debug("-------------- nothing happened --------------");
//            } else {
//                csv.append(String.format("%s;%s;%s\n", System.currentTimeMillis(), DAMAGE_PER_HIT, health));
//                health -= DAMAGE_PER_HIT;
//                lasthit = System.currentTimeMillis();
//                logger.debug(String.format("OUCH!!! -------------- remaining health: %d", health));
//            }
//            Thread.sleep(25);
//
//            // revovering if necessary
//            if (RECOVERING_HEALTH_PER_SECOND > 0 && health < HEALTH && System.currentTimeMillis() - lasthit > MILLIS_WITHOUT_HIT_BEFORE_RECOVERING && System.currentTimeMillis() - 1000 > lastrecover) {
//                lastrecover = System.currentTimeMillis();
//                health += RECOVERING_HEALTH_PER_SECOND;
//                if (health > HEALTH) health = HEALTH;
//
//                logger.debug(String.format("RECOVERING -------------- new health: %d", health));
//            }
//
//        }


    }

    private static void initCommon() throws Exception {

        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();

            scheduler.start();
            scheduler.getContext().put("loglevel", logLevel);
        } catch (SchedulerException se) {
            logger.fatal(se);
            System.exit(0);
        }


        config = new SortedProperties();
        // todo: configreader needed
        config.put("vibeSensor1", "GPIO 4");
        config.put("HEALTH_CHANGE_PER_HIT", "-1");
        config.put("GAME_LENGTH_IN_SECONDS", "60");
        config.put("DELAY_BEFORE_GAME_STARTS_IN_SECONDS", "5");
        config.put("MAX_HEALTH", "1000");
        config.put("DEBOUNCE", "15");

        config.put("pwmRed", "GPIO 0");
        config.put("pwmGreen", "GPIO 3");
        config.put("pwmBlue", "GPIO 5");

        config.put("pinSiren", "GPIO 6");
        config.put("pinGameModeButton", "GPIO 22");

        vibesensorListener = new VibesensorListener(logLevel, Long.parseLong(config.getProperty("HEALTH_CHANGE_PER_HIT")));
        gamemodeListener = new GamemodeListener(scheduler, Integer.parseInt(config.getProperty("DELAY_BEFORE_GAME_STARTS_IN_SECONDS")),
                Integer.parseInt(config.getProperty("GAME_LENGTH_IN_SECONDS")),
                Long.parseLong(config.getProperty("MAX_HEALTH"))
        );

        vibesensorListener.addListener(gamemodeListener);


    }


    private static void initSwingFrame() throws Exception {
        if (Tools.isArm()) return;

        MyGpioPinPwmOutput pwmRed = new MyGpioPinPwmOutput("pwmRed");
        MyGpioPinPwmOutput pwmGreen = new MyGpioPinPwmOutput("pwmGreen");
        MyGpioPinPwmOutput pwmBlue = new MyGpioPinPwmOutput("pwmBlue");

        scheduler.getContext().put("pwmRed", pwmRed);
        scheduler.getContext().put("pwmGreen", pwmGreen);
        scheduler.getContext().put("pwmBlue", pwmBlue);


        FrameDebug frameDebug = new FrameDebug(gamemodeListener);
        frameDebug.pack();
        frameDebug.setVisible(true);
        frameDebug.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


    }


    private static void initRaspi() throws Exception {
        if (!Tools.isArm()) return;
        GPIO = GpioFactory.getInstance();

        Pin pinRed = RaspiPin.getPinByName(config.getProperty("pwmRed"));
        Pin pinGreen = RaspiPin.getPinByName(config.getProperty("pwmGreen"));
        Pin pinBlue = RaspiPin.getPinByName(config.getProperty("pwmBlue"));

        Pin pinSiren = RaspiPin.getPinByName(config.getProperty("pinSiren"));

        MyGpioPinPwmOutput pwmRed = new MyGpioPinPwmOutput(GPIO.provisionSoftPwmOutputPin(pinRed));
        MyGpioPinPwmOutput pwmGreen = new MyGpioPinPwmOutput(GPIO.provisionSoftPwmOutputPin(pinGreen));
        MyGpioPinPwmOutput pwmBlue = new MyGpioPinPwmOutput(GPIO.provisionSoftPwmOutputPin(pinBlue));

        scheduler.getContext().put("pwmRed", pwmRed);
        scheduler.getContext().put("pwmGreen", pwmGreen);
        scheduler.getContext().put("pwmBlue", pwmBlue);

        Pin pinVibeSensor1 = RaspiPin.getPinByName(config.getProperty("vibeSensor1"));
        vibeSensor1 = GPIO.provisionDigitalInputPin(pinVibeSensor1, "vibeSensor1", PinPullResistance.PULL_DOWN);
        vibeSensor1.setDebounce(Integer.parseInt(config.getProperty("DEBOUNCE")), PinState.LOW, PinState.HIGH);
        vibeSensor1.addListener(vibesensorListener);

        GpioPinDigitalInput gamebutton = GPIO.provisionDigitalInputPin(RaspiPin.getPinByName(config.getProperty("pinGameModeButton")), "gamemodeButton", PinPullResistance.PULL_UP);
        GameButtonListener gameButtonListener = new GameButtonListener(logLevel);
        gameButtonListener.addListener(gamemodeListener);
        gamebutton.addListener(gameButtonListener);


//        GpioPinDigitalOutput gpioSiren = GPIO.provisionDigitalOutputPin(pinSiren, "Siren", PinState.LOW);
//        gpioSiren.high();
//        Thread.sleep(1000);
//        gpioSiren.low();


//        ArrayList<RGBBean> ledpattern = new ArrayList<>();
//        ledpattern.add(new RGBBean(pwmRed, pwmGreen, pwmBlue, 255, 0, 0, 1000l));
//        ledpattern.add(new RGBBean(pwmRed, pwmGreen, pwmBlue, 0, 255, 0, 1000l));
//        ledpattern.add(new RGBBean(pwmRed, pwmGreen, pwmBlue, 0, 0, 255, 1000l));
////
////                    String ledp = "pwnRed,pwmGreen,";
//
//
//        try {
//
//            JobDetail job = newJob(PinHandlerRGBJob.class)
//                    .withIdentity("rgbhandler1", "group1")
//
//                    .build();
//
//            job.getJobDataMap().put("ledpattern", ledpattern);
////            SimpleTrigger trigger = (SimpleTrigger) newTrigger()
////                    .withIdentity("trigger2","group1")
////                    .startNow()
////                    .forJob("rgbhandler1","group1")
////                    .build();
//
//            // Trigger the job to run now, and then repeat every 40 seconds
//            Trigger trigger = newTrigger()
//                    .withIdentity("rgbhandler1-trigger", "group1")
//                    .withSchedule(simpleSchedule().repeatForever().withIntervalInMilliseconds(1))
//                    .startNow()
//                    .build();
//
//
//            scheduler.scheduleJob(job, trigger);
//
//        } catch (SchedulerException e) {
//            logger.fatal(e);
//            e.printStackTrace();
//            System.exit(0);
//        }


//        for (int i = 0; i < 100; i++) {
//            Color color = getColor(new BigDecimal(i));
//            pwmRed.setPwm(color.getRed());
//            pwmGreen.setPwm(color.getGreen());
//            pwmBlue.setPwm(color.getBlue());
//
//            logger.debug(String.format("%d|%d|%d", color.getRed(), color.getGreen(), color.getBlue()));
//
//
//            Thread.sleep(50);
//
//        }

//        Thread.sleep(3000);


    }


}
