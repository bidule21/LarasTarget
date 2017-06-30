package de.flashheart.lara;

import com.pi4j.io.gpio.*;
import de.flashheart.lara.listeners.GamemodeListener;
import de.flashheart.lara.listeners.HealthListener;
import de.flashheart.lara.listeners.VibesensorListener;
import de.flashheart.lara.misc.SortedProperties;
import de.flashheart.lara.misc.Tools;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class Main {
    public static GpioController GPIO;
    private static SortedProperties config;

    public static long HEALTH = 1000l;
    public static long HEALTH_CHANGE_PER_HIT = -1l;
    public static long MILLIS_WITHOUT_HIT_BEFORE_RECOVERING = 10000l;
    public static long RECOVERING_HEALTH_PER_SECOND = 10l;    // auf 0 setzen, wenn nicht heilen soll
    private static long health = HEALTH;

    private static long lasthit = Long.MAX_VALUE;
    private static long lastrecover = 0l;

    private static Scheduler scheduler;
    private static Logger logger;
    private static Level logLevel = Level.DEBUG;
    private static StringBuffer csv = new StringBuffer(100000);
    private static GpioPinDigitalInput vibeSensor1;

    private static Pin pinVibeSensor1;
    private static Pin pinRed;
    private static Pin pinGreen;
    private static Pin pinBlue;

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

        logger.debug("\n" +
                "  ____   ___   ___   ___   ___   ___   ___  __  __ __  __ __  __ __  __ __  __ __  __ __  __ \n" +
                " | __ ) / _ \\ / _ \\ / _ \\ / _ \\ / _ \\ / _ \\|  \\/  |  \\/  |  \\/  |  \\/  |  \\/  |  \\/  |  \\/  |\n" +
                " |  _ \\| | | | | | | | | | | | | | | | | | | |\\/| | |\\/| | |\\/| | |\\/| | |\\/| | |\\/| | |\\/| |\n" +
                " | |_) | |_| | |_| | |_| | |_| | |_| | |_| | |  | | |  | | |  | | |  | | |  | | |  | | |  | |\n" +
                " |____/ \\___/ \\___/ \\___/ \\___/ \\___/ \\___/|_|  |_|_|  |_|_|  |_|_|  |_|_|  |_|_|  |_|_|  |_|\n" +
                "                                                                                             ");
    }

    private static void initCommon() {
        config = new SortedProperties();
        // todo: configreader needed
        config.put("vibeSensor1", "GPIO 4");


        try {
            // Grab the Scheduler instance from the Factory
            scheduler = StdSchedulerFactory.getDefaultScheduler();


            // and start it off
            scheduler.start();

            // define the job and tie it to our HelloJob class
            JobDetail job = newJob()
                    .withIdentity("job1", "group1")
                    .build();

            // Trigger the job to run now, and then repeat every 40 seconds
            Trigger trigger = newTrigger()
                    .withIdentity("trigger1", "group1")
                    .startNow()
                    .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(40)
                            .repeatForever())
                    .build();

            // Tell quartz to schedule the job using our trigger
            scheduler.scheduleJob(job, trigger);





        } catch (SchedulerException se) {
            se.printStackTrace();
        }

    }

    private static void initSwingFrame() throws Exception {
        if (Tools.isArm()) return;

        VibesensorListener vibesensorListener = new VibesensorListener(logLevel, HEALTH_CHANGE_PER_HIT);
        HealthListener healthListener = new HealthListener(logLevel, HEALTH);
        GamemodeListener gamemodeListener = new GamemodeListener(0, 1000);

        vibesensorListener.addListener(healthListener);
        healthListener.addListener(gamemodeListener);
        
    }


    private static void initRaspi() throws Exception {
        if (!Tools.isArm()) return;
        GPIO = GpioFactory.getInstance();

        pinVibeSensor1 = RaspiPin.getPinByName(config.getProperty("vibeSensor1"));

//        pinRed = RaspiPin.GPIO_00;
//        pinGreen = RaspiPin.GPIO_03;
//        pinBlue = RaspiPin.GPIO_05;
//
//        GpioPinPwmOutput pwmRed = GPIO.provisionSoftPwmOutputPin(pinRed);
//        GpioPinPwmOutput pwmGreen = GPIO.provisionSoftPwmOutputPin(pinGreen);
//        GpioPinPwmOutput pwmBlue = GPIO.provisionSoftPwmOutputPin(pinBlue);


        VibesensorListener vibesensorListener = new VibesensorListener(logLevel, HEALTH_CHANGE_PER_HIT);
        HealthListener healthListener = new HealthListener(logLevel, HEALTH);
        GamemodeListener gamemodeListener = new GamemodeListener(0, 1000);

        vibesensorListener.addListener(healthListener);
        healthListener.addListener(gamemodeListener);


        vibeSensor1 = GPIO.provisionDigitalInputPin(pinVibeSensor1, "vibeSensor1", PinPullResistance.PULL_DOWN);
        vibeSensor1.setDebounce(15, PinState.LOW, PinState.HIGH);
        vibeSensor1.addListener();


        // you can optionally use these wiringPi methods to further customize the PWM generator
        // see: http://wiringpi.com/reference/raspberry-pi-specifics/
//        com.pi4j.wiringpi.Gpio.pwmSetMode(com.pi4j.wiringpi.Gpio.PWM_MODE_MS);
//        com.pi4j.wiringpi.Gpio.pwmSetRange(255);
//        com.pi4j.wiringpi.Gpio.pwmSetClock(500);
//
//        pwmRed.setPwm(255);
//        pwmGreen.setPwm(0);
//        pwmBlue.setPwm(0);

    }


}
