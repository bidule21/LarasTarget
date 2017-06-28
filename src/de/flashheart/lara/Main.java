package de.flashheart.lara;

import com.pi4j.io.gpio.*;
import de.flashheart.lara.misc.SortedProperties;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class Main {
    public static GpioController GPIO;
    private static SortedProperties config;

    public static long HEALTH = 1000l;
    public static long DAMAGE_PER_HIT = 1l;
    public static long MILLIS_WITHOUT_HIT_BEFORE_RECOVERING = 10000l;
    public static long RECOVERING_HEALTH_PER_SECOND = 10l;    // auf 0 setzen, wenn nicht heilen soll
    private static long health = HEALTH;

    private static long lasthit = Long.MAX_VALUE;
    private static long lastrecover = 0l;

    private static Logger logger;
    private static Level logLevel = Level.DEBUG;
    private static StringBuffer csv = new StringBuffer(100000);
    private static GpioPinDigitalInput vibeSensor1;

    private static Pin pinVibeSensor1;
    private static Pin pinRed;
    private static Pin pinGreen;
    private static Pin pinBlue;


    public static void main(String[] args) throws Exception {
        // init log4j (auch log4j.properties wirkt sich hier aus)
        System.setProperty("logs", Tools.getMissionboxDirectory());
        logger = Logger.getLogger("Main");
        logger.setLevel(logLevel);

        // init config
        initConfig();

        initRaspi();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                try {
                    FileUtils.writeStringToFile(new File("/home/pi/larastarget.csv"), csv.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.trace(e);
                }
            }
        }));


        while (health > 0) {
            if (vibeSensor1.isLow()) {
                csv.append(String.format("%s;%s;%s\n", System.currentTimeMillis(), 0l, health));
                logger.debug("-------------- nothing happened --------------");
            } else {
                csv.append(String.format("%s;%s;%s\n", System.currentTimeMillis(), DAMAGE_PER_HIT, health));
                health -= DAMAGE_PER_HIT;
                lasthit = System.currentTimeMillis();
                logger.debug(String.format("OUCH!!! -------------- remaining health: %d", health));
            }
            Thread.sleep(25);

            // revovering if necessary
            if (RECOVERING_HEALTH_PER_SECOND > 0 && health < HEALTH && System.currentTimeMillis() - lasthit > MILLIS_WITHOUT_HIT_BEFORE_RECOVERING && System.currentTimeMillis() - 1000 > lastrecover) {
                lastrecover = System.currentTimeMillis();
                health += RECOVERING_HEALTH_PER_SECOND;
                if (health > HEALTH) health = HEALTH;

                logger.debug(String.format("RECOVERING -------------- new health: %d", health));
            }

        }

        logger.debug("\n" +
                "  ____   ___   ___   ___   ___   ___   ___  __  __ __  __ __  __ __  __ __  __ __  __ __  __ \n" +
                " | __ ) / _ \\ / _ \\ / _ \\ / _ \\ / _ \\ / _ \\|  \\/  |  \\/  |  \\/  |  \\/  |  \\/  |  \\/  |  \\/  |\n" +
                " |  _ \\| | | | | | | | | | | | | | | | | | | |\\/| | |\\/| | |\\/| | |\\/| | |\\/| | |\\/| | |\\/| |\n" +
                " | |_) | |_| | |_| | |_| | |_| | |_| | |_| | |  | | |  | | |  | | |  | | |  | | |  | | |  | |\n" +
                " |____/ \\___/ \\___/ \\___/ \\___/ \\___/ \\___/|_|  |_|_|  |_|_|  |_|_|  |_|_|  |_|_|  |_|_|  |_|\n" +
                "                                                                                             ");
    }

    private static void initConfig() {
        config = new SortedProperties();
        // todo: configreader needed
        config.put("vibeSensor1", "GPIO 4");
    }

    private static void initRaspi() throws Exception {
        GPIO = GpioFactory.getInstance();

        pinVibeSensor1 = RaspiPin.getPinByName(config.getProperty("vibeSensor1"));

//        pinRed = RaspiPin.GPIO_00;
//        pinGreen = RaspiPin.GPIO_03;
//        pinBlue = RaspiPin.GPIO_05;
//
//        GpioPinPwmOutput pwmRed = GPIO.provisionSoftPwmOutputPin(pinRed);
//        GpioPinPwmOutput pwmGreen = GPIO.provisionSoftPwmOutputPin(pinGreen);
//        GpioPinPwmOutput pwmBlue = GPIO.provisionSoftPwmOutputPin(pinBlue);

        vibeSensor1 = GPIO.provisionDigitalInputPin(pinVibeSensor1, "vibeSensor1", PinPullResistance.PULL_DOWN);
        vibeSensor1.setDebounce(15, PinState.LOW, PinState.HIGH);



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
