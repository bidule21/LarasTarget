package main.java.de.flashheart.lara.handlers;



import main.java.de.flashheart.lara.interfaces.PercentageInterface;

import java.math.BigDecimal;

/**
 * Created by tloehr on 21.06.17.
 */
public class LEDProgress extends PercentageInterface {
    public LEDProgress(String name) {
        super(name);
    }

    @Override
    public void setValue(BigDecimal percent) {

    }
}
