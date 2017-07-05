package de.flashheart.lara.tools;

import com.pi4j.io.gpio.GpioPinPwmOutput;

import java.awt.*;

/**
 * Created by tloehr on 03.07.17.
 */
public class RGBBean {
    MyGpioPinPwmOutput pinRed;
    MyGpioPinPwmOutput pinGreen;
    MyGpioPinPwmOutput pinBlue;
    int valueRed;
    int valueGreen;
    int valueBlue;
    long ms;

    public RGBBean(MyGpioPinPwmOutput pinRed, MyGpioPinPwmOutput pinGreen, MyGpioPinPwmOutput pinBlue, Color color, long ms) {
        this.pinRed = pinRed;
        this.pinGreen = pinGreen;
        this.pinBlue = pinBlue;
        this.valueRed = color.getRed();
        this.valueGreen = color.getGreen();
        this.valueBlue = color.getBlue();
        this.ms = ms;
    }

    public void showLEDs() throws InterruptedException {
        pinRed.setPwm(valueRed);
        pinGreen.setPwm(valueGreen);
        pinBlue.setPwm(valueBlue);
        Thread.sleep(ms);
    }

}
