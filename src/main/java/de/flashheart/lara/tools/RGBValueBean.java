package de.flashheart.lara.tools;

import com.pi4j.io.gpio.GpioPinPwmOutput;

/**
 * Created by tloehr on 03.07.17.
 */
public class RGBValueBean {
    GpioPinPwmOutput pinRed;
    GpioPinPwmOutput pinGreen;
    GpioPinPwmOutput pinBlue;
    int valueRed;
    int valueGreen;
    int valueBlue;
    long ms;

    public RGBValueBean(GpioPinPwmOutput pinRed, GpioPinPwmOutput pinGreen, GpioPinPwmOutput pinBlue, int valueRed, int valueGreen, int valueBlue, long ms) {
        this.pinRed = pinRed;
        this.pinGreen = pinGreen;
        this.pinBlue = pinBlue;
        this.valueRed = valueRed;
        this.valueGreen = valueGreen;
        this.valueBlue = valueBlue;
        this.ms = ms;
    }

    public void showLEDs() throws InterruptedException {
        pinRed.setPwm(valueRed);
        pinGreen.setPwm(valueGreen);
        pinBlue.setPwm(valueBlue);
        Thread.sleep(ms);
    }

}
