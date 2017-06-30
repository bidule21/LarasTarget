package de.flashheart.lara.handlers;

import de.flashheart.lara.interfaces.PercentageInterface;
import org.apache.log4j.Logger;

import java.awt.*;
import java.math.BigDecimal;

/**
 * Created by tloehr on 07.06.15.
 */
public class RelayProgressRGB extends PercentageInterface {

    protected final Logger logger = Logger.getLogger(getClass());
    private final String pinRed;
    private final String pinGreen;
    private final String pinBlue;
    protected int previousRelay = -1;

    final Color yellow = new Color(255, 255, 0);
    final Color purple = new Color(255, 0, 255);
    final Color cyan = new Color(0, 255, 255);

    final Color green = new Color(0, 255, 0);
    final Color blue = new Color(0, 0, 255);
    final Color red = new Color(255, 0, 0);

    final String on = "1;" + Long.MAX_VALUE + ",0";
    final String slow = Integer.toString(Integer.MAX_VALUE) + ";1000,1000";
    final String fast = Integer.toString(Integer.MAX_VALUE) + ";500,500";

    final Color[] colors = {red, red, red, blue, blue, blue, green, green, green};
//    final String[] schemes = {on, slow, fast, on, slow, fast, on, slow, fast};
    final String[] schemes = {fast, slow, on, fast, slow, on, fast, slow, on};

    public RelayProgressRGB(String pinRed, String pinGreen, String pinBlue) {
        super("ProgressRGB");
        this.pinRed = pinRed;
        this.pinGreen = pinGreen;
        this.pinBlue = pinBlue;
    }


    @Override
    public void setValue(BigDecimal percent) {

//        logger.debug("PERCENT: " + percent);

        if (percent.compareTo(BigDecimal.ZERO) < 0) {
//            MissionBox.off(pinRed);
//            MissionBox.off(pinGreen);
//            MissionBox.off(pinBlue);
            return;
        }

        BigDecimal myPercent = new BigDecimal(100).subtract(percent);
        int colornum = new BigDecimal(colors.length).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP).multiply(myPercent).intValue();

        // shortcut
        if (previousRelay == colornum) return;
        previousRelay = colornum;

        if (colornum >= colors.length) {
//            MissionBox.off(pinRed);
//            MissionBox.off(pinGreen);
//            MissionBox.off(pinBlue);
        } else {
            setColor(colors[colornum], schemes[colornum]);
        }

    }

    void setColor(Color color, String scheme) {
        if (color.getRed() > 0) {
//            MissionBox.setScheme(pinRed, scheme);
        } else {
//            MissionBox.off(pinRed);
        }
        if (color.getBlue() > 0) {

//            MissionBox.setScheme(pinBlue, scheme);
        } else {
//            MissionBox.off(pinBlue);
        }
        if (color.getGreen() > 0) {

//            MissionBox.setScheme(pinGreen, scheme);
        } else {
//            MissionBox.off(pinGreen);
        }
    }
}
