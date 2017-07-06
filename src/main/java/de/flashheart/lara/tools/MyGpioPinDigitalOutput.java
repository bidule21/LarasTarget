package de.flashheart.lara.tools;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import org.apache.log4j.Logger;

/**
 * Created by tloehr on 04.07.17.
 */
public class MyGpioPinDigitalOutput {
    final Logger logger = Logger.getLogger(getClass());
    final GpioPinDigitalOutput gpioPinDigitalOutput;
    private final String name;

    public MyGpioPinDigitalOutput(GpioPinDigitalOutput gpioPinDigitalOutput) {
        this.gpioPinDigitalOutput = gpioPinDigitalOutput;
        this.name = gpioPinDigitalOutput.getName();
    }

    public MyGpioPinDigitalOutput(String name) {
        this.gpioPinDigitalOutput = null;
        this.name = name;
    }

    public void high() {
        logger.debug(String.format("[%s] setting pwm value to: HIGH", name));
        if (gpioPinDigitalOutput == null) return;
        gpioPinDigitalOutput.high();
    }


    public void low() {
        logger.debug(String.format("[%s] setting pwm value to: HIGH", name));
        if (gpioPinDigitalOutput == null) return;
        gpioPinDigitalOutput.high();
    }


}
