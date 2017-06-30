package de.flashheart.lara.interfaces;

import java.util.EventListener;

/**
 * Created by tloehr on 30.06.17.
 */
public interface GameTimeListener extends EventListener {
    void setTimeTo(long gametime);
}
