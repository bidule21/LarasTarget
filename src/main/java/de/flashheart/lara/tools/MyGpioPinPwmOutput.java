package de.flashheart.lara.tools;

import com.pi4j.io.gpio.GpioPinPwmOutput;
import de.flashheart.lara.swing.FrameDebug;
import org.apache.log4j.Logger;

import java.awt.*;

/**
 * Created by tloehr on 04.07.17.
 */
public class MyGpioPinPwmOutput {
    final Logger logger = Logger.getLogger(getClass());
    final GpioPinPwmOutput gpioPinPwmOutput;
    private final String name;
    private final FrameDebug frameDebug;

    public MyGpioPinPwmOutput(GpioPinPwmOutput gpioPinPwmOutput) {
        this.frameDebug = null;
        this.gpioPinPwmOutput = gpioPinPwmOutput;
        this.name = gpioPinPwmOutput.getName();
    }

    public MyGpioPinPwmOutput(String name) {
        this.frameDebug = null;
        this.gpioPinPwmOutput = null;
        this.name = name;
    }

    public MyGpioPinPwmOutput(String name, FrameDebug frameDebug) {
        this.frameDebug = frameDebug;
        this.gpioPinPwmOutput = null;
        this.name = name;
    }

    public void setPwm(int value) {
//        logger.debug(String.format("[%s] setting pwm value to: %d", name, value));
        if (gpioPinPwmOutput != null) gpioPinPwmOutput.setPwm(value);
        if (frameDebug != null) frameDebug.setRGB(getColorByName(name), value);
    }

    public void setPwmRange(int range) {
//        logger.debug(String.format("[%s] setting pwm range value to: %d", name, range));
        if (gpioPinPwmOutput == null) return;
        gpioPinPwmOutput.setPwmRange(range);
    }

    Color getColorByName(String colorname){
        Color color = Color.white;
        if (colorname.equalsIgnoreCase("red")){
            color = Color.RED;
        } else if (colorname.equalsIgnoreCase("green")){
            color = Color.GREEN;
        } else if (colorname.equalsIgnoreCase("blue")){
            color = Color.BLUE;
        }
        return color;
    }

}
