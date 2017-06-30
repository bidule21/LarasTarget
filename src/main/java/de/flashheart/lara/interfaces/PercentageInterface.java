package de.flashheart.lara.interfaces;

import java.math.BigDecimal;

/**
 * Created by tloehr on 28.04.15.
 */
public abstract class PercentageInterface {
    private String name;

    public PercentageInterface(String name) {
        this.name = name;
    }

    public abstract void setValue(BigDecimal percent);

    @Override
    public String toString() {
        return name;
    }
}
