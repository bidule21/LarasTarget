package de.flashheart.lara.handlers;

import com.pi4j.io.gpio.GpioPinPwmOutput;
import de.flashheart.lara.interfaces.PercentageInterface;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.UnableToInterruptJobException;

import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by tloehr on 07.06.15.
 */
public class LEDRGBProgress implements PercentageInterface {

    protected final Logger logger = Logger.getLogger(getClass());
    private final Scheduler scheduler;

    protected int previousPercent = -1;

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

    ArrayList<Integer[]> ledscheme = new ArrayList<>();
    ArrayList<GpioPinPwmOutput> pins = new ArrayList<>();



    private JobKey jobKey;

    public LEDRGBProgress(Scheduler scheduler, GpioPinPwmOutput pinRed, GpioPinPwmOutput pinGreen, GpioPinPwmOutput pinBlue, Level level) {
        logger.setLevel(level);
        this.scheduler = scheduler;
        pins.add(pinRed);
        pins.add(pinGreen);
        pins.add(pinBlue);

    }


    @Override
    public void setValue(BigDecimal bdPercent) {
        try {

            if (previousPercent == bdPercent.intValue()) return;
            previousPercent = bdPercent.intValue();
            ledscheme.clear();

            if (bdPercent.compareTo(BigDecimal.ZERO) <= 0 && jobKey != null) {
                scheduler.interrupt(jobKey);
                scheduler.deleteJob(jobKey);
                return;
            }

            Color color = getColor(bdPercent.divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_UP));


            ledscheme.add(new Integer[]{color.getRed(),Integer.MAX_VALUE});
            ledscheme.add(new Integer[]{color.getRed(),Integer.MAX_VALUE});
            ledscheme.add(new Integer[]{color.getRed(),Integer.MAX_VALUE});



        } catch (Exception e){
            logger.trace(e);
            System.exit(0);
        }

    }
    
    //https://stackoverflow.com/questions/340209/generate-colors-between-red-and-green-for-a-power-meter
    int getTrafficlightColor(double value){
        return java.awt.Color.HSBtoRGB((float)value/3f, 1f, 1f);
    }

    public Color getColor(BigDecimal power)
    {
        double H = power.doubleValue() * 0.4; // Hue (note 0.4 = Green, see huge chart below)
        double S = 0.9; // Saturation
        double B = 0.9; // Brightness

        return Color.getHSBColor((float)H, (float)S, (float)B);
    }
}
