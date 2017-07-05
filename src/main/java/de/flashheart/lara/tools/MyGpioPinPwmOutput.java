package de.flashheart.lara.tools;

import com.pi4j.io.gpio.GpioPinPwmOutput;
import org.apache.log4j.Logger;

/**
 * Created by tloehr on 04.07.17.
 */
public class MyGpioPinPwmOutput {
    final Logger logger = Logger.getLogger(getClass());
    final GpioPinPwmOutput gpioPinPwmOutput;
    private final String name;

    public MyGpioPinPwmOutput(GpioPinPwmOutput gpioPinPwmOutput) {
        this.gpioPinPwmOutput = gpioPinPwmOutput;
        this.name = gpioPinPwmOutput.getName();
    }

    public MyGpioPinPwmOutput(String name) {
        this.gpioPinPwmOutput = null;
        this.name = name;
    }

    public void setPwm(int value) {
//        logger.debug(String.format("[%s] setting pwm value to: %d", name, value));
        if (gpioPinPwmOutput == null) return;
        gpioPinPwmOutput.setPwm(value);
    }

    public void setPwmRange(int range) {
//        logger.debug(String.format("[%s] setting pwm range value to: %d", name, range));
        if (gpioPinPwmOutput == null) return;
        gpioPinPwmOutput.setPwmRange(range);
    }

}
